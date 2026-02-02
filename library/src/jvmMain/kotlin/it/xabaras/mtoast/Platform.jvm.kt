package it.xabaras.mtoast

class DesktopPlatform: Platform {
    private val osName: String = System.getProperty("os.name")
    private val osVersion: String = System.getProperty("os.version")
    private val osArch: String = System.getProperty("os.arch")
    override val name: PlatformName = PlatformName.JVM
    override val info: String = "$osName $osVersion (${this.osArch})"
}

actual fun getPlatform(): Platform = DesktopPlatform()