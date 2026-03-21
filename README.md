# AppClock

AppClock is an Android app for scheduled app launching and per-app daily usage monitoring.

## What it does

- Schedule installed apps to launch at a selected date/time.
- Edit, delete, and track schedule execution state (upcoming, launched, failed, canceled).
- Monitor selected apps against a daily usage limit.
- Run background usage checks with periodic WorkManager and send limit alerts.

## Stack

- Kotlin, Jetpack Compose, MVVM
- Hilt for dependency injection
- Room for local persistence
- WorkManager + AlarmManager for background work/scheduling

## Permissions used

- `SCHEDULE_EXACT_ALARM`
- `SYSTEM_ALERT_WINDOW`
- `PACKAGE_USAGE_STATS`
- `POST_NOTIFICATIONS` (Android 13+)

## Screenshots

<p align="center">
  <img src="./assets/home_screen.jpg" alt="Home screen showing scheduled apps" width="200" />
  <img src="./assets/edit_screen.jpg" alt="Edit screen for modifying a scheduled app" width="200" />
  <img src="./assets/executed_schedules.jpg" alt="History screen showing executed schedules" width="200" />
</p>
