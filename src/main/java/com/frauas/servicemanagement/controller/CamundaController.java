package com.frauas.servicemanagement.controller;

import com.frauas.servicemanagement.entity.ProviderOffer;
import com.frauas.servicemanagement.entity.ServiceRequest;
import com.frauas.servicemanagement.service.CamundaProcessService;
import com.frauas.servicemanagement.service.ProviderOfferService;
import com.frauas.servicemanagement.service.ServiceRequestService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/camunda")
public class CamundaController {

    private static final Logger LOG = LoggerFactory.getLogger(CamundaController.class);

    @Autowired
    private CamundaProcessService camundaProcessService;

    @Autowired
    private ProviderOfferService providerOfferService;

    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private RuntimeService runtimeService;

    /* ===============================
       TASK LIST VIEWS
       =============================== */

    @GetMapping("/tasks")
    public String getAllTasks(Model model) {
        List<Task> tasks = camundaProcessService.getAllActiveTasks();
        model.addAttribute("tasks", tasks);
        model.addAttribute("viewTitle", "All Active Tasks");
        return "camunda-tasks";
    }

    @GetMapping("/tasks/{assignee}")
    public String getTasksForAssignee(@PathVariable String assignee, Model model) {
        List<Task> tasks = camundaProcessService.getTasksForAssignee(assignee);
        model.addAttribute("tasks", tasks);
        model.addAttribute("viewTitle", "Tasks for " + assignee);
        return "camunda-tasks";
    }

    /* ===============================
       TASK DETAILS VIEW
       =============================== */

    @GetMapping("/task/{taskId}")
    public String getTaskDetails(@PathVariable String taskId, Model model) {

        Task task = camundaProcessService.getTaskById(taskId);

        // ✅ Null safety
        if (task == null) {
            LOG.warn("Task {} not found. Redirecting to dashboard.", taskId);
            return "redirect:/dashboard";
        }

        model.addAttribute("task", task);

        String processInstanceId = task.getProcessInstanceId();

        // Get requestId safely
        Long requestId = getVariableSafe(processInstanceId, "requestId", Long.class);
        model.addAttribute("requestId", requestId);

        // Load ServiceRequest details (embedded view)
        if (requestId != null) {
            serviceRequestService.getServiceRequestById(requestId)
                    .ifPresent(req -> model.addAttribute("requestDetails", req));
        }

        String taskName = task.getName();

        /* ===============================
           FIXED: EVALUATE OFFERS
           =============================== */
        // Handles "Evaluate", "Evaluate Offers", "Evaluate Provider Offers", etc.
        if (taskName != null && taskName.contains("Evaluate") && requestId != null) {
            List<ProviderOffer> offers =
                    providerOfferService.getOffersByServiceRequest(requestId);
            model.addAttribute("offers", offers);
        }

        /* ===============================
           FIXED: FINAL VERIFICATION
           =============================== */
        if (taskName != null &&
                (taskName.contains("Final Verify") || taskName.contains("Final Verification"))) {

            Long selectedOfferId =
                    getVariableSafe(processInstanceId, "selectedOfferId", Long.class);
            String reason =
                    getVariableSafe(processInstanceId, "selectionReason", String.class);

            if (selectedOfferId != null && requestId != null) {
                List<ProviderOffer> offers =
                        providerOfferService.getOffersByServiceRequest(requestId);

                ProviderOffer selected = offers.stream()
                        .filter(o -> o.getId().equals(selectedOfferId))
                        .findFirst()
                        .orElse(null);

                model.addAttribute("selectedOffer", selected);
                model.addAttribute("selectionReason", reason);
            }
        }

        return "camunda-task-details";
    }

    /* ===============================
       COMPLETE TASK
       =============================== */

    @PostMapping("/task/{taskId}/complete")
    public String completeTask(@PathVariable String taskId,
                               @RequestParam Map<String, String> formData) {

        Map<String, Object> variables = new HashMap<>();

        formData.forEach((key, value) -> {
            if (!"_csrf".equals(key)) {
                if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                    variables.put(key, Boolean.valueOf(value));
                } else {
                    try {
                        variables.put(key, Long.valueOf(value));
                    } catch (NumberFormatException e) {
                        variables.put(key, value);
                    }
                }
            }
        });

        camundaProcessService.completeTask(taskId, variables);
        return "redirect:/dashboard";
    }

    /* ===============================
       SAFE VARIABLE FETCH
       =============================== */

    @SuppressWarnings("unchecked")
    private <T> T getVariableSafe(String executionId, String name, Class<T> type) {
        try {
            Object val = runtimeService.getVariable(executionId, name);
            if (val == null) return null;

            if (type == Long.class && val instanceof Integer) {
                return type.cast(((Integer) val).longValue());
            }

            if (type == Long.class && val instanceof String) {
                return type.cast(Long.valueOf((String) val));
            }

            return type.cast(val);

        } catch (Exception e) {
            LOG.warn("Could not read variable {} from execution {}", name, executionId);
            return null;
        }
    }
}
