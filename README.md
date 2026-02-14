# Service Management Tool (SMT) with Camunda

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green)
![Camunda](https://img.shields.io/badge/Camunda-7.24-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED)

> **Frankfurt University of Applied Sciences** > **Master of Engineering in Information Technology | WS 2025-26** > **Course:** Agile Development in Cloud Computing Environments  

## 📄 Project Overview

In the modern enterprise, the procurement of external workforce (freelancers, consultants) is often trapped in legacy, siloed processes involving fragmented emails, spreadsheets, and compliance risks. 

The **Service Management Tool (SMT)** is a centralized orchestration hub designed to digitize the end-to-end procurement lifecycle. Built as a middleware solution, it utilizes **Spring Boot** and an **Embedded Camunda BPM engine** to enforce a standardized, compliant workflow connecting Project Managers, Procurement Officers, and external providers.

### Key Capabilities
* **Process Orchestration:** Manages long-running transactions (Draft → Approval → Market → Order) via a BPMN 2.0 state machine.
* **Role-Based Access Control (RBAC):** Distinct dashboards and permissions for Project Managers (PM), Procurement Officers (PO), and Resource Planners (RP).
* **External Integration:** Acts as an SOA nexus, integrating with Workforce Management, Contract Management, and External Provider systems via REST APIs.
* **"Aurora" Dashboard:** A modern, glass-morphism UI built with Thymeleaf and Bootstrap 5 for real-time visualization.
* **Auditability:** Full transaction history and audit logs via Camunda History tables.

---

## 🏗 System Architecture

The SMT follows a modular **Service-Oriented Architecture (SOA)**. It decouples internal business logic from external integration points, ensuring robustness and scalability.

### High-Level Components
1.  **Authentication & RBAC:** Spring Security handling internal actor authorization.
2.  **Process Engine:** Embedded Camunda 7 engine handling state persistence and flow execution.
3.  **Integration Layer:** REST Controllers managing data exchange with external groups (1b, 2b, 4b, 5b).
4.  **Reporting Interface:** Pull-based API architecture for business intelligence.

> *[Insert Image of High-Level Architecture Diagram Here]*

---

## 🛠 Tech Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Language** | Java 17 LTS | Core backend logic and strict typing. |
| **Framework** | Spring Boot 3.2 | Web, Data JPA, and Security modules. |
| **Orchestration** | Camunda BPM 7 | Embedded workflow engine (BPMN 2.0). |
| **Database** | PostgreSQL | Relational persistence for business & process data. |
| **Frontend** | Thymeleaf + Bootstrap 5 | Server-side rendering with "Aurora" glass-morphism theme. |
| **Deployment** | Docker & Render | Containerized cloud deployment. |
| **Testing** | Postman | End-to-end API integration testing. |

---

## 🔄 User Flow & BPMN Process

The core logic is defined by a **BPMN 2.0 model** (`service-request-process.bpmn`).

1.  **Draft Creation:** PM creates a request.
2.  **Contract Validation:** System automatically checks Group 2b API for valid frame agreements.
3.  **Approval:** Procurement Officer validates budget and need.
4.  **Market Publication:** Request is broadcast to External Providers (Group 4b).
5.  **Offer Evaluation:** Providers submit bids; PM selects the best candidate.
6.  **Final Order:** Logistics confirmed by Resource Planner; Service Order generated.

> *[Insert Image of BPMN Workflow Diagram Here]*

---

## 💻 Getting Started

### Prerequisites
* Java Development Kit (JDK) 17
* Maven 3.8+
* PostgreSQL (Local or Cloud)
* Docker (Optional, for containerization)

### Installation

1.  **Clone the Repository**
    ```bash
    git clone [https://github.com/YOUR_USERNAME/smt-camunda-orchestrator.git](https://github.com/YOUR_USERNAME/smt-camunda-orchestrator.git)
    cd smt-camunda-orchestrator
    ```

2.  **Configure Database**
    Update `src/main/resources/application.properties` with your PostgreSQL credentials:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/smt_db
    spring.datasource.username=your_user
    spring.datasource.password=your_password
    ```

3.  **Build the Project**
    ```bash
    mvn clean install
    ```

4.  **Run the Application**
    ```bash
    mvn spring-boot:run
    ```

5.  **Access the Application**
    * **Web Dashboard:** `http://localhost:8080`
    * **Camunda Cockpit:** `http://localhost:8080/camunda`
    * **H2 Console (if enabled):** `http://localhost:8080/h2-console`

---

## 🐳 Docker Deployment

The project is cloud-native ready. To run via Docker:

1.  **Build the Image**
    ```bash
    docker build -t smt-app .
    ```

2.  **Run Container**
    ```bash
    docker run -p 8080:8080 -e DB_URL=jdbc:postgresql://host.docker.internal:5432/smt_db smt-app
    ```

---

## 📸 Screenshots

### The "Aurora" Dashboard
> *[Insert Screenshot of Dashboard showing active Service Requests]*

### Camunda Audit Log (Proof of Execution)
> *[Insert Screenshot of Camunda Cockpit/Audit Log]*

---

## 👥 Team Members

**Team 3b**

* **Manoj Hanumanthu** (1566325)
* **Aman Basha Patel** (1565430)
* **Muhammad Ahsan Ijaz** (1566312)
* **Saquib Attar** (1567041)

**Supervisor:** Dr. Patrick Wacht

---

## 📄 License & Acknowledgments

This project was developed for the **Agile Development in Cloud Computing Environments** module at Frankfurt University of Applied Sciences.

* Special thanks to Integration Partners: Groups 1b, 2b, 4b, and 5b.
* Powered by [Camunda Platform](https://camunda.com/).
