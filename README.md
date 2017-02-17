# JourneyApps Bluetooth Scale Android Application

Reference Android application for JourneyApps integration with a Hiweigh Bluetooth scale.


## Scale Reading Broadcasts

The application broadcasts scale reading messages with action `com.journeyapps.bluetoothscale.SCALE_BROADCAST_ACTION`.

- `message` (`String`) - the type of message, will be "reading" for scale readings.
- `unit` (`String`) - either "kg" for kilograms or "lb" for pounds
- `weight` (`Double`)
- `fault` (`Boolean`)
- `zero` (`Boolean`)
- `stable` (`Boolean`)
- `overweight` (`Boolean`)

An example message in JSON format:

```json
{
    "fault": false,
    "message": "reading",
    "overweight": false,
    "stable": false,
    "unit": "kg",
    "weight": 24.3,
    "zero": false
}
```
