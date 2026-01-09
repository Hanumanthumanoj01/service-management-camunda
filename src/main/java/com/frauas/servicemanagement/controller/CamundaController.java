package com.frauas.servicemanagement.controller;

import com.frauas.servicemanagement.entity.ProviderOffer;
import com.frauas.servicemanagement.entity.ServiceRequestStatus;
import com.frauas.servicemanagement.service.CamundaProcessService;
import com.frauas.servicemanagement.service.ProviderOfferService;
import com.frauas.servicemanagement.service.ServiceRequestService;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/camunda")
public class CamundaController {

    private static final Logger LOG = LoggerFactory.getLogger(CamundaController.class);

    @Autowired private CamundaProcessService camundaProcessService;
    @Autowired private ProviderOfferService providerOfferService;
    @Autowired private ServiceRequestService serviceRequestService;
    @Autowired private RuntimeService runtimeService;

    // --- TASK LIST (FIXED NAMING) ---
    @GetMapping("/tasks")
    public String getAllTasks(Model model) {
        return getTasksForAssignee("all", model);
    }

    // --- TASK LIST (FIXED NAMING) ---
    @GetMapping("/tasks/{assignee}")
    public String getTasksForAssignee(@PathVariable String assignee, Model model) {
        List<Task> tasks = assignee.equals("all") ? camundaProcessService.getAllActiveTasks() : camundaProcessService.getTasksForAssignee(assignee);

        // ✅ NEW: Dynamic Naming (Title - Task Name) for ALL tasks
        tasks.forEach(t -> {
            String projectTitle = (String) runtimeService.getVariable(t.getProcessInstanceId(), "title");
            String originalName = t.getName();

            // Special handling for Fix Rejection to make it clearer
            if ("Activity_PM_Fix".equals(t.getTaskDefinitionKey())) {
                originalName = "Correction Required";
            }

            if (projectTitle != null) {
                t.setName(projectTitle + " - " + originalName);
            }
        });

        model.addAttribute("tasks", tasks);
        model.addAttribute("viewTitle", "Inbox: " + assignee);

        if (assignee.contains("pm")) {
            model.addAttribute("drafts", serviceRequestService.getServiceRequestsByStatus(ServiceRequestStatus.DRAFT));
        }
        return "camunda-tasks";
    }

    // --- TASK DETAILS ---
    @GetMapping("/task/{taskId}")
    public String getTaskDetails(@PathVariable String taskId, Model model) {
        Task task = camundaProcessService.getTaskById(taskId);
        if (task == null) return "redirect:/camunda/tasks/pm_user";

        String pId = task.getProcessInstanceId();
        String taskKey = task.getTaskDefinitionKey();

        // 1. Load Request
        Long requestId = getVariableSafe(pId, "requestId", Long.class);
        model.addAttribute("requestId", requestId);
        if (requestId != null) {
            serviceRequestService.getServiceRequestById(requestId).ifPresent(req -> model.addAttribute("requestDetails", req));
        }

        // 2. Load History
        model.addAttribute("commentHistory", getVariableSafe(pId, "commentHistory", String.class));

        // 3. Load Data based on Step
        if ("Activity_PM_Evaluate".equals(taskKey) && requestId != null) {
            List<ProviderOffer> offers = providerOfferService.getOffersByServiceRequest(requestId);
            model.addAttribute("offers", offers);
        }

        if ("Activity_RP_Coordination".equals(taskKey)) {
            Long offerId = getVariableSafe(pId, "selectedOfferId", Long.class);
            if (offerId != null) {
                providerOfferService.getOfferById(offerId).ifPresent(o -> model.addAttribute("selectedOffer", o));
            }
        }

        if ("Activity_PM_Fix".equals(taskKey)) {
            model.addAttribute("rejectionReason", getVariableSafe(pId, "rejectionReason", String.class));
            task.setName("Fix Rejection");
        }

        model.addAttribute("task", task);
        return "camunda-task-details";
    }

    // --- COMPLETE TASK ---
    @PostMapping("/task/{taskId}/complete")
    public String completeTask(@PathVariable String taskId, @RequestParam Map<String, String> formData, Authentication auth) {
        Task task = camundaProcessService.getTaskById(taskId);
        if (task == null) return "redirect:/dashboard";

        String pId = task.getProcessInstanceId();
        String taskKey = task.getTaskDefinitionKey();
        Long requestId = getVariableSafe(pId, "requestId", Long.class);

        // 1. Chat History Log
        String newComment = "";
        String role = (auth != null) ? auth.getAuthorities().stream().findFirst().get().getAuthority().replace("ROLE_", "") : "User";

        if (formData.get("rejectionReason") != null && !formData.get("rejectionReason").isEmpty()) {
            newComment = "🔴 REJECTED by " + role + ": " + formData.get("rejectionReason");
        } else if (formData.get("pmJustification") != null && !formData.get("pmJustification").isEmpty()) {
            newComment = "🔵 RESUBMITTED by PM: " + formData.get("pmJustification");
        } else if (formData.get("selectionReason") != null && !formData.get("selectionReason").isEmpty()) {
            newComment = "🟢 SELECTED by PM: " + formData.get("selectionReason");
        }

        if (!newComment.isEmpty()) {
            String existing = getVariableSafe(pId, "commentHistory", String.class);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM HH:mm"));
            runtimeService.setVariable(pId, "commentHistory", (existing == null ? "" : existing) + "[" + timestamp + "] " + newComment + "\n");
        }

        // 2. Status Updates & Variable Handling
        Map<String, Object> vars = new HashMap<>();

        if (requestId != null) {
            if ("Activity_PO_Approval".equals(taskKey)) {
                if ("false".equals(formData.get("approved"))) {
                    serviceRequestService.updateServiceRequestStatus(requestId, ServiceRequestStatus.NEEDS_CORRECTION);
                    vars.put("approved", false);
                } else {
                    serviceRequestService.updateServiceRequestStatus(requestId, ServiceRequestStatus.PUBLISHED);
                    vars.put("approved", true);
                }
            }
            else if ("Activity_PM_Fix".equals(taskKey)) {
                serviceRequestService.updateServiceRequestStatus(requestId, ServiceRequestStatus.WAITING_APPROVAL);
            }
            else if ("Activity_PM_Evaluate".equals(taskKey)) {
                // Capture selectedOfferId
                if(formData.containsKey("selectedOfferId")) {
                    vars.put("selectedOfferId", Long.valueOf(formData.get("selectedOfferId")));
                }
                serviceRequestService.updateServiceRequestStatus(requestId, ServiceRequestStatus.SELECTED_UNDER_VERIFICATION);
            }
        }

        // 3. Technical Scores
        formData.forEach((k, v) -> {
            if (k.startsWith("techScore_")) providerOfferService.updateTechnicalScore(Long.parseLong(k.split("_")[1]), Double.parseDouble(v));
        });

        // 4. Submit to Camunda
        formData.forEach((k, v) -> {
            if (!k.startsWith("techScore_") && !"_csrf".equals(k) && !vars.containsKey(k)) {
                if ("true".equals(v) || "false".equals(v)) vars.put(k, Boolean.valueOf(v));
                else if (v.matches("-?\\d+")) vars.put(k, Long.valueOf(v));
                else vars.put(k, v);
            }
        });

        camundaProcessService.completeTask(taskId, vars);
        return "redirect:/camunda/tasks/" + (auth != null ? auth.getName() : "all");
    }

    private <T> T getVariableSafe(String id, String name, Class<T> type) {
        Object val = runtimeService.getVariable(id, name);
        if (val == null) return null;
        if (type == Long.class && val instanceof Integer) return type.cast(((Integer) val).longValue());
        return type.cast(val);
    }
}