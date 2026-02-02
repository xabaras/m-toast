package it.xabaras.mtoast

class WasmPlatform: Platform {
    override val name: PlatformName = PlatformName.JS
    override val info: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()