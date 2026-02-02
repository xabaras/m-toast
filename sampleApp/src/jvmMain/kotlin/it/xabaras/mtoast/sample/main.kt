package it.xabaras.mtoast.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import it.xabaras.mtoast.sample.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "mToast",
    ) {
        App()
    }
}