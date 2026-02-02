package it.xabaras.mtoast

enum class PlatformName {
    ANDROID,
    IOS,
    JVM,
    JS,
    // ... other platforms as needed
}

interface Platform {
    val name: PlatformName
    val info: String

    fun isIOS(): Boolean {
        return name == PlatformName.IOS
    }

    fun isAndroid(): Boolean {
        return name == PlatformName.ANDROID
    }
}

expect fun getPlatform(): Platform