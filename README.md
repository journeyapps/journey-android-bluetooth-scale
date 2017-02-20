# JourneyApps Bluetooth Scale Android Application

Reference Android application for JourneyApps integration with a Hiweigh Bluetooth scale.

The current version of the application is not production ready and is merely a proof of concept that should serve as a reference for integration.


## Scale Reading Broadcasts

The application broadcasts scale reading messages with action `com.journeyapps.bluetoothscale.SCALE_BROADCAST_ACTION` (as global Android broadcasts).

Each event contains the following fields:

- `message` (`String`) - the type of message, will be "reading" for scale readings.
- `unit` (`String`) - either "kg" for kilograms or "lb" for pounds
- `weight` (`Double`)
- `fault` (`Boolean`)
- `zero` (`Boolean`)
- `stable` (`Boolean`)
- `overweight` (`Boolean`)

An example event in JSON format:

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


## Development Recommendations

This section details recommendations for further development in order to improve the application's user experience and make it production ready.


### Inbound Broadcasts

The application currently only sends out scale readings via outbound broadcasts. To make the integration seamless and provide the best possible user experience, the application should be extended to also receive broadcasts. This will allow the JourneyApps container to manage the  connection state so that connecting, reading and disconnecting from a Bluetooth scale all happens seamlessly from the JourneyApps container without ever having to open the `journey-android-bluetooth-scale` UI.

Control broadcasts that the application should be able to receive:

- Start application in background with relevant services.
- Stop application and background services.
- Get list of devices (should respond with a device list broadcast).
- Connect to device with given MAC address (should respond with connection status broadcasts).
- Disconnect from device (should respond with connection status broadcasts).


### Foreground Service

A shortcoming of the application is that it needs to remain running in order to send out scale readings (i.e. closing the application with the back button will pause the broadcasts).

The operation of the application and management of Bluetooth connections should be decoupled from `MainActivity` and instead managed by `ScaleBroadcastService` (or a similar service). This service should remain in the foreground via the use of a notification with a facility to stop it.


### Remember Device

The application should be able to store the MAC address of the last connected device and be able to reconnect automatically (or least reconnect without having to select a device from a list).
