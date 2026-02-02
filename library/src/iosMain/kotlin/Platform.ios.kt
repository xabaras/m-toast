package it.xabaras.mtoast

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: PlatformName = PlatformName.IOS
    override val info: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()