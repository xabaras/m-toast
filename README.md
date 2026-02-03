# mToast
[![Maven Central](https://maven-badges.sml.io/maven-central/it.xabaras/mtoast/badge.svg?style=flat&gav=true&version=0.2.0)](https://central.sonatype.com/artifact/it.xabaras/mtoast/0.2.0)

A lightweight Compose Multiplatform library for displaying toast messages across Android, iOS, Desktop (JVM), and Web (Wasm/JS).

![mToast](https://raw.githubusercontent.com/xabaras/m-toast/main/sampleApp/src/commonMain/composeResources/drawable/mtoast.png)

## How do I get set up?

Add the dependency to your `commonMain` source set:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("it.xabaras:mtoast:0.2.0")
        }
    }
}
```

## Setup

To use the library, wrap your main content with the `ToastContainer`. This component hosts the toast overlays.

```kotlin
ToastContainer {
    // Your app content here
    App()
}
```

On iOS, the toast automatically aligns to the top to avoid overlapping with the notch/dynamic island, while on other platforms it defaults to the bottom.

## Usage

The library provides several `showToast` methods to display different types of messages.

### 1. Simple Text Toast
Displays a basic text message with a default duration.

```kotlin
showToast("Hello, World!")
```

### 2. Toast with Duration
You can specify a custom duration in milliseconds, or use constants from `ToastDefaults`.

```kotlin
showToast(
    message = "This is a short toast",
    durationMillis = ToastDefaults.DURATION_SHORT
)
```

### 3. Toast with Icon (ImageVector, ImageBitmap, or Painter)
You can include an icon alongside your message. The library supports `ImageVector`, `ImageBitmap`, and `Painter`.

```kotlin
showToast(
    message = "Success!",
    icon = Icons.Default.Check,
    iconTint = Color.Green,
    durationMillis = ToastDefaults.DURATION_LONG
)
```

### 4. Fully Custom Composable Toast
If you need complete control over the toast's appearance, you can pass a custom Composable.

```kotlin
showToast(durationMillis = 5000L) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text("Loading...")
    }
}
```

## Configuration

### ToastDefaults
The library provides default values that you can use:
- `DURATION_SHORT`: 2000ms
- `DURATION_DEFAULT`: 3000ms
- `DURATION_LONG`: 5000ms
- `DEFAULT_ICON_TINT`: Color.Unspecified (defaults to Black/White based on theme)

## License
Apache-2.0 license.
