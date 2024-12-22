# Premission App
## Overview
This app checks several system conditions (such as network connectivity, battery level, brightness, volume, charging status, and SMS permissions) to determine if the user can proceed with logging into the next screen. If all conditions are met, the login button will be enabled and colored green.

## Author
[Adir Zadok](https://github.com/adirzadok16)


## Installation
1. Clone the repository:
bashCopygit clone https://github.com/adirzadok16/.git
2. Open the project in Android Studio.
3. Build and run the project on an Android device or emulator.

## Conditions
1. Connectivity to Wi-Fi
2. Brightness level (maximum brightness)
3. Battery level (greater than 40%)
4. Volume (muted)
5. Phone charging status
6. SMS message from a specific sender

## Features

1. SMS Permission Request: Requests permission to read SMS messages.
2. Network Connectivity: Verifies if the device is connected to Wi-Fi.
3. Brightness Check: Ensures the device's brightness is at maximum.
4. Battery Check: Ensures the battery is charged above 40% and is charging.
5. Volume Check: Ensures the volume is set to minimum.
6. SMS from Afeka: Checks if an SMS from the specified sender ("Afeka") is present.

## Premissions
1. READ_SMS: The app requests permission to read SMS messages in order to check for a specific message from a sender ("Afeka").
2. ACCESS_NETWORK_STATE: To check the Wi-Fi status and network connectivity.
3. BATTERY_STATS: To check the current battery level and charging status.
4. WRITE_SETTINGS: Used to retrieve screen brightness settings.

## Video
https://github.com/user-attachments/assets/f7ca8ad2-c81f-489c-a3d6-ed5c860dab88
