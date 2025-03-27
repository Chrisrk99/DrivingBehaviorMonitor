# DrivingBehaviorMonitor 🚗📱

 group project for tracking driving behavior using live phone sensors (accelerometer, gyroscope) and GPS data. This app shows real-time readings for acceleration, braking, and speed consistency. 

---

## What Does This App Do? 

This app is all about monitoring driving behavior using the phone’s sensors and GPS. Here’s what we’ve got so far:

- **Live accelerometer and gyroscope data** (x, y, z values) to track acceleration, braking, and turning.
- **GPS-based speed tracking** in m/s and mph, calculated using location updates.
- **Modular screen navigation** using Jetpack Compose, so it’s easy to add new screens.
- **Data updates as you simulate movement** in the emulator (or on a real device).

---

## Project Structure 

organized the project to keep things clean and simple. Here’s how the files are set up:

- **`MainActivity.kt`**: The entry point of the app. It sets up the theme and navigation system.
- **`NavGraph.kt`**: Controls screen navigation (like a map for switching between screens).
- **`screens/`**: This folder has all the UI screens.
  - `HomeScreen.kt`: The main screen with a list of categories (like "Speed Consistency").
  - `AccelerationScreen.kt`: Shows live accelerometer and gyroscope data.
  - `SpeedScreen.kt`: Shows the current speed in mph (and raw m/s for debugging).
- **`utils/`**: Reusable logic for sensors and location stuff.
  - `SensorUtils.kt`: Has composables like `useAccelerometerData()` and `useGyroscopeData()`.
  - `LocationUtils.kt`: Has `useLocationSpeed()` for calculating speed from GPS.
- **`ui.theme/`**: Auto-generated theme files for the app’s look and feel (colors, fonts, etc.).

---

## How to Run the App 🏃‍♂️

Here’s how to get the app up and running on your machine:

1. **Clone the repo**:
   

2. **Open in Android Studio**:
   - Open Android Studio and choose "Open an existing project."
   - Select the `DrivingBehaviorMonitor` folder and let it sync (it’ll download all the dependencies).

3. **Run the app**:
   - Pick an emulator (API 30+ works best) or connect a real device with USB debugging enabled.
   - Click the "Run" button in Android Studio.

4. **Simulate driving in the emulator**:
   - Once the app is running, go to the emulator’s "Extended Controls" > "Location" > "Routes."
   - Add two points (like a start and end location) and save the route.
   - Click "Play Route" to simulate driving (you’ll see the blue dot move on the map).

5. **Allow permissions**:
   - The app will ask for location permission the first time you go to the Speed Consistency screen. Tap "Allow" so we can get GPS data.

6. **Use the app**:
   - You’ll start on the home screen with a list of categories.
   - Tap "Acceleration and Braking Patterns" to see live sensor data.
   - Tap "Speed Consistency" to see the current speed in mph (it’ll update as the route plays).

---

## What You Need to Know 📝

Here are some key things to understand about how the app works:

- **Sensor code is reusable**: All the sensor logic (like accelerometer and gyroscope) is in the `utils/` folder. This keeps our screens clean and makes it easy to use the same logic in new screens.
- **Screens are modular**: Each screen is in its own file in the `screens/` folder, so it’s easy to add new ones or tweak existing ones.
- **Testing in the emulator**: You can test the sensors and speed by playing a route in the emulator. The accelerometer and gyroscope will update live as you tilt the device (or simulate movement).
- **Speed calculation**: We’re calculating speed manually in `useLocationSpeed()` by measuring the distance between two location points and dividing by the time difference. Then we convert it to mph in the `SpeedScreen`.
- **Real device testing**: The app works in the emulator, but it’s a good idea to test on a real phone to see how the sensors and GPS behave with actual movement.

---

## Features So Far 

Here’s what’s working in the app right now:

- **Live accelerometer and gyroscope data**: The `AccelerationScreen` shows real-time x, y, z values for both sensors.
- **GPS-based speed tracking**: The `SpeedScreen` shows the current speed in mph (and raw m/s for debugging). It updates as you move (or as the emulator plays a route).
- **Navigation**: We’re using Jetpack Compose’s navigation to switch between screens. The home screen has a list of categories, and you can tap to go to a specific screen.
- **Emulator support**: You can simulate driving in the emulator using the "Routes" feature, and the speed will update accordingly.

---

## To-Do / Next Steps 📋


- **Add a cornering behavior screen**: We could use the gyroscope data to detect sharp turns and analyze cornering behavior.
- **Store driving sessions**: Right now, the data is live-only. We could save it to a database or file so users can review past drives.
- **Add graphs or reports**: It’d be cool to show a graph of speed or acceleration over time (optional, but would make the app more polished).
- **Improve the UI**: The app works, but we could add some visual polish—like better colors, icons, or layouts—to make it look nicer.

---

## Notes for the Team 💬

- **Manifest Warning**: There’s a small warning in the Logcat about `OnBackInvokedCallback`. To fix it, add this line to the `<application>` tag in `AndroidManifest.xml`:
  ```xml
  android:enableOnBackInvokedCallback="true"
