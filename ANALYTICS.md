# Local analytics event reference

Events are written to Logcat with the `EarthquakeAnalytics` tag. This is a case-study measurement plan, not persisted or uploaded analytics. Events contain no device location or personal identifier.

| Event | Trigger | Parameters | Product question |
| --- | --- | --- | --- |
| `app_opened` | Application process starts | None | How often is the app launched? |
| `earthquake_list_viewed` | Home reaches the resumed state | None | Do users reach the earthquake list? |
| `earthquake_selected` | User taps a Home earthquake | `earthquake_id` | Do users inspect earthquake details? |
| `earthquake_list_refreshed` | User pulls to refresh Home | None | Do users manually seek newer data? |
| `notification_threshold_changed` | A changed threshold is saved successfully | `threshold` | Do users configure alerts? |
| `notification_permission_requested` | Android 13+ permission request launches from Alert Settings | None | Do users attempt to enable alerts? |
| `notification_permission_result` | Android returns the permission result | `granted` | Can users receive alerts? |
| `earthquake_notification_sent` | The app successfully calls Android notification delivery | `earthquake_id`, `magnitude` | Is the alert system producing relevant notifications? |
| `earthquake_notification_opened` | MainActivity receives an earthquake notification intent | `earthquake_id` | Do users act on alerts? |

`app_opened` means application process start. `earthquake_list_viewed` is counted on Home resume, not on Compose recomposition. A normal Detail navigation does not count as a notification open. Notification delivery and opening are local app events, so they do not prove that the user saw or read the notification.
