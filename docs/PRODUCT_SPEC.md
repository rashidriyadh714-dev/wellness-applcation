# World-Class Wellness App — Product Specification

Vision
- Build a wearable-first, subscription-driven wellness platform that delivers daily, personalized recovery and readiness scores, actionable coaching, and long-term health insights — a modern Whoop-like experience.

Key Differentiators
- Classic monochrome UI for focus and elegance
- Transparent, scientifically-informed scoring with explainable contributors
- Fast, private cloud sync with end-to-end user control
- Modular analytics engine enabling rapid experimentation

Core Features (MVP)
- Continuous score (0-100) computed daily from sleep, activity, HRV proxies, and user-entered metrics
- Timeline & trends dashboard (7/30/90-day views)
- Baseline personalization and adaptive scoring
- Secure user accounts and cloud sync (encrypted)
- Simple onboarding flow and daily insights

Advanced Features (post-MVP)
- Sensor integrations (Bluetooth, via companion mobile app)
- Advanced sleep staging & HRV analysis
- Predictive alerts and guided recovery plans
- Cohort analytics, A/B testing, and ML-backed personalization
- Monetization: subscription tiers, premium coaching, enterprise integrations

Data & Sensors
- Start with manual/csv import + simulated sensor data
- Design data model for time-series (timestamped records with types)
- Privacy-first storage: per-user encryption keys, GDPR-ready export/delete

Scoring Overview (high level)
- Score = 50 + sum(weight_i * normalized_contributor_i)
- Contributors: sleep_quality, sleep_duration, activity_load, recovery_metric (HRV proxy), resting_heart_rate
- Keep scoring modular (plugin contributors) to allow A/B experiments

Architecture (MVP)
- Desktop app UI (`src/wellness/ui`) -> local data store (`src/wellness/service/DataStore`) -> Analytics engine (`src/wellness/service/AnalyticsEngine`)
- Cloud scaffold: simple REST API + token-based auth (future)

Success Metrics (initial)
- DAU/MAU ratio > 20%
- 1-week retention > 35%
- Conversion from free->paid (trial) > 3%
- Average score improvements for engaged users

90-Day Roadmap (prioritized)
1. Stabilize core models + unit tests (foundation)
2. Implement modular analytics engine + basic scoring (this sprint)
3. Add secure user accounts + cloud sync scaffold
4. Polish onboarding + monochrome UI flows
5. Mobile companion scaffold and sensor integration plan

Next immediate steps
- Implement `AnalyticsEngine` skeleton and simple unit harness
- Define contributor interfaces and test data format
- Start a lightweight REST scaffold for auth/sync (prototype)

Notes
- I will keep changes incremental and runnable locally. After you confirm, I will implement the analytics skeleton and run a quick test.
