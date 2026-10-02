# AI Usage Report

I used AI-assisted tools to accelerate planning, implementation, testing, QA, and documentation for this Android case study. Their output was treated as a proposal to review and verify, not as automatically accepted code.

## Tools Used

### ChatGPT

I used ChatGPT to break down requirements, discuss architecture and trade-offs, prepare scoped Codex prompts and review implementation summaries.

### Codex

I used Codex to inspect the repository, implement bounded tasks, add tests, run build and lint checks, perform focused QA, and draft documentation.

## Workflow

Requirements → scoped task → AI implementation → developer review → build/tests/lint → accept or revise.

Features were implemented in small, reviewable steps rather than one open-ended AI task. This made changes easier to inspect and regressions easier to identify.

## Developer Ownership

I retained responsibility for feature scope, architecture decisions, UX acceptance, trade-offs, code review, and final submission. AI could suggest or implement an approach; I decided whether it met the case requirements and requested revisions when it did not.

## Examples of Review and Correction

1. **Home cache:** An initial cache approach could leave stale or alert-fetched earthquakes visible on Home. I reviewed this and separated current `home_feed` membership from earthquake records retained for Detail lookup.
2. **Alert logic:** I required the first successful background check to establish a silent baseline, preventing historical notification spam. Processed IDs also prevent old earthquakes from alerting if the user later lowers the threshold.
3. **UI:** The first functional Compose UI felt too default and card-heavy. After manual review, it was redesigned with lighter grouped surfaces, clearer typography, and a more restrained layout.

## Verification

I reviewed the code and ran the current project check:

```bash
./gradlew build testDebugUnitTest lint
```

The build passed; **54 unit tests passed with 0 failures**, and lint reported **0 errors and 14 warnings**. Home and Alert Settings were visually inspected on an emulator during UI refinement. These checks do not prove Android notification delivery, notification-tap behavior, or WorkManager scheduling by the OS.

## AI-Assisted Testing

AI helped create tests for mapping, repository/cache behavior, ViewModel states, preferences, alert baseline and deduplication, and analytics. I reviewed them against intended behavior specified in the scoped requirements: implementation and AI-generated tests can share the same wrong assumption, so passing tests alone are not independent proof of correctness.

## Scope Control

I explicitly kept backend/FCM, maps and location-aware alerts, Paging 3, an external analytics SDK, and unnecessary multi-module architecture out of scope. The goal was to satisfy the case requirements, not maximize generated code; plausible AI suggestions still needed a product reason to be accepted.

## Final Responsibility

I remain responsible for understanding, explaining, verifying, maintaining, and modifying the submitted code, including acknowledging behavior that has not been verified on a device.
