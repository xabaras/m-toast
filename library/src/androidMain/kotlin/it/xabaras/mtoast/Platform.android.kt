package it.xabaras.mtoast

import android.os.Build

class AndroidPlatform : Platform {
    override val name: PlatformName = PlatformName.ANDROID
    override val info: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()