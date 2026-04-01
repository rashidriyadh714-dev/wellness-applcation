# UML Class Diagram (Important Classes Only)

```mermaid
classDiagram
direction LR

class Main
class MainFrame
class LoginDialog

class WellnessManager
class DataStore
class AnalyticsEngine
class GoalTracker
class HabitTracker
class WellnessBot

class WellnessProfile
class ScoreContributor
class AbstractRecord
class HealthRecord
class ActivityRecord

Main --> LoginDialog : authenticate
Main --> MainFrame : launch UI

MainFrame --> WellnessManager : manage app state
MainFrame --> GoalTracker : goals
MainFrame --> HabitTracker : habits
MainFrame --> WellnessBot : assistant

WellnessManager --> DataStore : load/save
WellnessManager --> AnalyticsEngine : scoring
WellnessManager --> WellnessProfile : profile
WellnessManager --> AbstractRecord : records

AbstractRecord ..|> ScoreContributor
HealthRecord --|> AbstractRecord
ActivityRecord --|> AbstractRecord
```
8. `HealthRecord` and `ActivityRecord` inherit from `AbstractRecord`.
9. `AbstractRecord` implements `ScoreContributor`, enabling polymorphic scoring.
10. `CloudSyncService` and `AuthService` handle synchronization and authentication support.

## 5) Report Usage Notes

- Use the starter diagram first during presentation (easy audience onboarding).
- Then switch to the full diagram for technical depth.
- This version intentionally excludes test classes to keep the architecture readable.
