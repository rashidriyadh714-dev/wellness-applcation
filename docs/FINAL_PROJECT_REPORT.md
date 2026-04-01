# Final Project Report
## SDG-Based Object-Oriented Application Development

## 1. Cover Page

Project Title:
- Elite Wellness Monitoring System

Selected SDG:
- SDG 3: Good Health and Well-being

Course Information:
- Course: [COURSE NAME]
- Class Code: [CLASS CODE]
- Program: [PROGRAM]
- Presentation Date: 2 April 2026

Team Information (required by assignment):
- Team Leader Full Name: [FULL NAME]
- Team Leader Student ID: [STUDENT ID]
- Team Leader NRIC/Passport: [NRIC/PASSPORT]
- Member 2 Full Name: [FULL NAME]
- Member 2 Student ID: [STUDENT ID]
- Member 2 NRIC/Passport: [NRIC/PASSPORT]
- Member 3 Full Name: [FULL NAME]
- Member 3 Student ID: [STUDENT ID]
- Member 3 NRIC/Passport: [NRIC/PASSPORT]

Repository URL (required):
- GitHub: [PASTE YOUR GITHUB REPOSITORY URL]

---

## 2. Introduction and SDG Background

This project develops a Java-based GUI application to support healthier daily behavior through tracking, analytics, and guided decisions. The selected SDG is SDG 3 (Good Health and Well-being), which focuses on improving health outcomes and quality of life.

Modern users face common wellness problems such as poor sleep consistency, low activity, dehydration, stress, and lack of structured habit tracking. Existing tools are often fragmented and do not combine profile, records, goals, habits, analytics, and recommendations in one system.

The Elite Wellness Monitoring System addresses this by offering a complete local application with data management, score computation, trend analysis, alerting logic, and interactive dashboards.

---

## 3. Problem Statement

Users need a practical system that can:
- Collect health and activity data in one place.
- Track progress against daily and weekly goals.
- Convert records into clear wellness insights.
- Provide consistent habit and recommendation support.
- Persist data between sessions.

Without such a system, users rely on memory or multiple disconnected apps, resulting in low adherence and weak decision-making.

---

## 4. System Objectives

The project objectives are:
- Build a working Java GUI system (Swing-based) for wellness simulation.
- Apply OOP principles correctly: encapsulation, inheritance, polymorphism, abstraction.
- Use collections and file handling for data persistence.
- Implement meaningful processing logic, not only CRUD.
- Provide clear and professional UI output for demonstration.
- Deliver a stable, testable application for final presentation.

---

## 5. Project Scope and Functional Requirements

### 5.1 Included Features
- Authentication and login dialog.
- User profile creation and update.
- Health record and activity record management.
- Dashboard with KPI cards and trend visualizations.
- Goals and habits management (add, update, delete, complete).
- Habit operations board and goal portfolio views.
- AI assistant guidance modes.
- Advanced insights, scenario simulation, and executive brief export.
- Telemetry logging and analytics smoke tests.

### 5.2 Processing and Simulation Logic
- Wellness scoring engine with contributors.
- Trend smoothing and moving average behavior.
- Goal progress computation.
- Habit streak and completion metrics.
- Alert-level logic and recommendation generation.

---

## 6. OOP Design and UML

The system follows layered structure:
- Model layer: domain entities and abstract/base contracts.
- Service layer: business logic, analytics, alerting, automation.
- UI layer: Swing panels, frame orchestration, user workflows.

UML reference:
- See `docs/UML_Class_Diagram.md` (Mermaid class diagram included).

### 6.1 OOP Principle Mapping

Encapsulation:
- Private fields with controlled access in model classes.
- Validation handled through constructors and setters.

Inheritance:
- `AbstractRecord` as abstract parent.
- `HealthRecord` and `ActivityRecord` extend `AbstractRecord`.

Polymorphism:
- `AbstractRecord` references used to process mixed record types at runtime.
- Contributor-oriented score calculation through interface contracts.

Abstraction:
- Abstract base class `AbstractRecord`.
- Interface `ScoreContributor`.

---

## 7. Package and Class Breakdown (What packages we use)

### 7.1 Java Source Package Structure

Root package:
- `wellness`

Sub-packages:
- `wellness.model`
- `wellness.service`
- `wellness.ui`
- `wellness.test`

### 7.2 `wellness.model` Classes
- `AbstractRecord`
- `ActivityRecord`
- `AlertLevel`
- `HealthRecord`
- `ScoreContributor`
- `UserAccount`
- `WellnessProfile`

### 7.3 `wellness.service` Classes
- `AdvancedAnalytics`
- `AlertService`
- `AnalyticsEngine`
- `AuthService`
- `AutomationService`
- `CloudSyncService`
- `ContributorRegistry`
- `DataStore`
- `DataVisualizationEngine`
- `GoalTracker`
- `HabitTracker`
- `MLAnalyticsEngine`
- `MovingAverage`
- `OnboardingService`
- `PredictiveAnalytics`
- `ScoringPipeline`
- `TelemetryService`
- `WellnessAnalyzer`
- `WellnessBot`
- `WellnessManager`

### 7.4 `wellness.ui` Classes
- `AIInsightsPanel`
- `AdvancedDashboardPanel`
- `AdvancedInsightsPanel`
- `ChatbotPanel`
- `DashboardPanel`
- `GoalsHabitsPanel`
- `LoginDialog`
- `MainFrame`
- `RecordTableModel`

### 7.5 `wellness.test` Classes
- `AnalyticsPipelineTest`
- `AnalyticsTest`
- `TelemetryTest`

### 7.6 Main Entry Point
- `wellness.Main`

---

## 8. Technology and Dependencies

### 8.1 Desktop Application
- Language: Java
- UI: Java Swing
- Build: Maven (project source directory configured as `src`)
- Java Release: 24

### 8.2 Backend (supporting component in repository)
- Node.js + Express
- Dependencies include Express, Mongoose, JWT, bcryptjs, Stripe, Axios, Helmet, CORS, Morgan

### 8.3 Mobile (supporting component in repository)
- React Native
- Dependencies include React Navigation, AsyncStorage, chart libraries, health integration packages

Note: The primary assessed deliverable for this project is the Java OOP application.

---

## 9. Collections and File Handling

Collections used in the Java application:
- `ArrayList`
- `HashMap`
- Other standard Java collection constructs in service/UI logic

File handling and persistence:
- User, profile, and records data are persisted in local files.
- Telemetry output is written for testable logging behavior.

This satisfies the assignment requirement for collections and file I/O persistence.

---

## 10. Implementation Details (Key Workflows)

### 10.1 User Flow
1. User opens app and signs in through `LoginDialog`.
2. `WellnessManager` loads existing profile and records from data store.
3. User adds health and activity records through the UI.
4. Dashboard updates KPIs and trends.
5. User manages goals and habits with update/delete/complete actions.
6. Advanced insights panel computes forecasts and scenarios.

### 10.2 Goal and Habit Management
- CRUD operations supported in `GoalTracker` and `HabitTracker`.
- UI supports add/update/delete/complete workflows.
- Portfolio and operations views present data in structured visual format.

### 10.3 Analytics and Insights
- Score engine combines profile and record factors.
- Trend analysis and smoothing are displayed in dashboard charts.
- Advanced insights provide scenarios and executive summaries.

---

## 11. Testing and Sample Output

Verification command:
- `bash verify.sh`

Build command:
- `bash build.sh`

Run command:
- `java -jar dist/EliteWellness.jar`

Observed verification status:
- Compile: PASSED
- Analytics pipeline smoke test: PASSED
- Telemetry smoke test: PASSED

Sample telemetry output (from smoke run):
- "Telemetry events written."
- "Weighted score: 84.925"
- "Trend: 25.125"

---

## 12. Code Quality and Documentation

Code quality practices applied:
- Modular package structure with clear separation of concerns.
- Consistent naming for classes/methods.
- Services isolate business logic from UI.
- Reusable components for records, tracking, analytics, and visualization.

Documentation included in `docs/`:
- Product specification
- UML class diagram
- Presentation demo guide
- Feature inventory
- Commercialization roadmap
- Security and compliance notes
- Final project report (this document)

---

## 13. Team Contribution and Collaboration

Suggested section for final submission (edit with actual details):
- Member 1: Core architecture, service implementation, data layer.
- Member 2: UI/UX implementation, dashboard, goals/habits interactions.
- Member 3: Testing, documentation, presentation preparation.

Collaboration approach:
- Shared planning and task division.
- Continuous integration of code changes.
- Joint verification and rehearsal for demo.

---

## 14. Discussion and Limitations

Strengths:
- Functional and stable Java GUI application.
- Strong OOP implementation with practical class relationships.
- Real processing logic for scoring, alerts, trends, and progress.
- Good assignment fit for SDG 3 use case.

Current limitations:
- Local file storage is suitable for course scope but not enterprise-scale.
- Some advanced features are simulation-level, not production cloud deployment.
- External wearable and cloud integrations are partially scaffolded.

---

## 15. Future Improvements

Potential next steps:
- Replace local persistence with database-backed storage.
- Add secure cloud sync and multi-device account state.
- Expand recommendation intelligence with more robust AI/ML models.
- Improve role-based access and audit reporting for enterprise mode.
- Add advanced report export templates for clinical and academic use.

---

## 16. Conclusion

The Elite Wellness Monitoring System successfully delivers an SDG 3-aligned OOP application that demonstrates:
- Clear SDG relevance
- Proper object-oriented design and implementation
- Meaningful simulation logic and usability
- Collections and file handling for persistence
- Stable runtime and testable behavior

The project meets the assignment intent by combining software engineering fundamentals with a realistic wellness problem domain and a demonstrable, working Java system.

---

## 17. Submission Checklist (Final)

Before submission on CityU LMS, ensure:
- Team details are fully filled (name, ID, class code, program, NRIC/passport).
- GitHub repository URL is inserted.
- PDF export generated from this report.
- Source code is complete and runnable.
- Live demo prepared (10 to 15 minutes).
- All members are ready to present.
