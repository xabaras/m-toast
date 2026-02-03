package it.xabaras.mtoast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

object ToastDefaults {
    val DEFAULT_COMPOSABLE : @Composable () -> Unit = {}
    const val DURATION_DEFAULT : Long = 3000L
    const val DURATION_SHORT : Long = 2000L
    const val DURATION_LONG : Long = 5000L
    val DEFAULT_ICON_TINT: Color = Color.Unspecified
    val DEFAULT_ALIGNMENT: Alignment = Alignment.Auto
}

@Stable val Alignment.Companion.Auto: BiasAlignment
    get() = BiasAlignment(-2f, -2f)

private class Toast(
    val message: String = "",
    val durationMillis: Long = ToastDefaults.DURATION_DEFAULT,
    val imageVector: ImageVector? = null,
    val imageBitmap: ImageBitmap? = null,
    val iconTint: Color = ToastDefaults.DEFAULT_ICON_TINT,
    val painter: Painter? = null,
    val composable: @Composable () -> Unit = ToastDefaults.DEFAULT_COMPOSABLE,
    val alignment: Alignment = ToastDefaults.DEFAULT_ALIGNMENT
)

@Composable
fun ToastContainer(modifier: Modifier = Modifier.fillMaxSize(), content: @Composable () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var shouldShowToast by remember { mutableStateOf(false) }
    var toast: Toast? by remember{ mutableStateOf(null) }
    var alignment: Alignment by remember { mutableStateOf(ToastDefaults.DEFAULT_ALIGNMENT) }
    var paddingValues: PaddingValues by remember { mutableStateOf(PaddingValues(0.dp)) }
    val isDarkTheme = isSystemInDarkTheme()

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        coroutineScope.launch {
            ToastEvents.subscribe<Toast> {
                toast = it

                if ( toast?.message?.isBlank() == true && toast?.composable == ToastDefaults.DEFAULT_COMPOSABLE )
                    return@subscribe

                if ( toast?.durationMillis == 0L )
                    return@subscribe

                coroutineScope.launch {
                    alignment = getAlignmentOrDefault(toast)
                    paddingValues = getPaddingValues(alignment)
                    shouldShowToast = true
                    delay(toast?.durationMillis ?: 0L)
                    shouldShowToast = false
                }
            }
        }
    }

    BoxWithConstraints(
        modifier.fillMaxSize()
    ) {
        content.invoke()
        AnimatedVisibility(
            visible = shouldShowToast,
            enter = fadeIn() + expandHorizontally(expandFrom = Alignment.CenterHorizontally),
            exit =  shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally) + fadeOut(),
            modifier = Modifier.align(alignment)
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .background(if ( isDarkTheme ) Color.White else Color.Black, RoundedCornerShape(24.dp))
                    .widthIn(ButtonDefaults.MinWidth, 300.dp)
                    .padding(horizontal = 12.dp, vertical = 5.dp),
            ) {
                if ( toast?.composable == ToastDefaults.DEFAULT_COMPOSABLE ) {
                    toast?.imageVector?.let {
                        Icon(
                            it,
                            "Toast icon",
                            tint = if ( toast?.iconTint == ToastDefaults.DEFAULT_ICON_TINT ) { if (isDarkTheme) Color.Black else Color.White } else toast!!.iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    toast?.imageBitmap?.let {
                        Icon(
                            it,
                            "Toast icon",
                            tint = if ( toast?.iconTint == ToastDefaults.DEFAULT_ICON_TINT ) { if (isDarkTheme) Color.Black else Color.White } else toast!!.iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    toast?.painter?.let {
                        Icon(
                            it,
                            "Toast icon",
                            tint = if ( toast?.iconTint == ToastDefaults.DEFAULT_ICON_TINT ) { if (isDarkTheme) Color.Black else Color.White } else toast!!.iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        text = toast?.message ?: "",
                        textAlign = TextAlign.Center,
                        color = if (isDarkTheme) Color.Black else Color.White,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp
                    )
                } else {
                    toast?.composable?.invoke()
                }
            }
        }
    }
}

private fun getAlignmentOrDefault(toast: Toast?): Alignment {
    toast?.let {
        if ( it.alignment != Alignment.Auto) return it.alignment
    }

    val platform = getPlatform()
    return when(platform.name) {
        PlatformName.IOS -> return Alignment.TopCenter
        PlatformName.JVM -> return Alignment.BottomEnd
        else -> Alignment.BottomCenter
    }
}

private fun getPaddingValues(alignment: Alignment) : PaddingValues {
    return when(alignment) {
        Alignment.TopStart -> PaddingValues(top = 24.dp, start = 24.dp)
        Alignment.TopCenter -> PaddingValues(top = 24.dp)
        Alignment.TopEnd -> PaddingValues(top = 24.dp, end = 24.dp)
        Alignment.BottomStart -> PaddingValues(bottom = 24.dp, start = 24.dp)
        Alignment.BottomCenter -> PaddingValues(bottom = 24.dp)
        Alignment.BottomEnd -> PaddingValues(bottom = 24.dp, end = 24.dp)
        Alignment.CenterStart -> PaddingValues(start = 24.dp)
        Alignment.CenterEnd -> PaddingValues(end = 24.dp)
        else -> PaddingValues(0.dp)
    }
}

/**
 * Displays a text-only toast message.
 *
 * @param message The text message to display.
 * @param durationMillis The duration the toast remains visible (defaults to 3000ms).
 * @param alignment The screen alignment for the toast (defaults to platform-specific Auto).
 */
fun showToast(message: String, durationMillis: Long = ToastDefaults.DURATION_DEFAULT, alignment: Alignment = ToastDefaults.DEFAULT_ALIGNMENT) {
    val coroutineScope = CoroutineScope(Dispatchers.Default)
    coroutineScope.launch {
        ToastEvents.publish(Toast(message, durationMillis, alignment = alignment))
    }
}

/**
 * Displays a toast message with a custom Composable content.
 *
 * @param durationMillis The duration the toast remains visible (defaults to 3000ms).
 * @param alignment The screen alignment for the toast (defaults to platform-specific Auto).
 * @param composable The custom UI content to render inside the toast.
 */
fun showToast(durationMillis: Long = ToastDefaults.DURATION_DEFAULT, alignment: Alignment = ToastDefaults.DEFAULT_ALIGNMENT, composable: @Composable () -> Unit = ToastDefaults.DEFAULT_COMPOSABLE) {
    val coroutineScope = CoroutineScope(Dispatchers.Default)
    coroutineScope.launch {
        ToastEvents.publish(Toast(durationMillis = durationMillis, alignment = alignment, composable = composable))
    }
}

/**
 * Displays a toast message with text and an [ImageVector] icon.
 *
 * @param message The text message to display.
 * @param icon The [ImageVector] to display as an icon.
 * @param iconTint The color tint for the icon.
 * @param durationMillis The duration the toast remains visible.
 * @param alignment The screen alignment for the toast.
 */
fun showToast(message: String, icon: ImageVector, iconTint: Color = ToastDefaults.DEFAULT_ICON_TINT, durationMillis: Long = ToastDefaults.DURATION_DEFAULT, alignment: Alignment = ToastDefaults.DEFAULT_ALIGNMENT) {
    val coroutineScope = CoroutineScope(Dispatchers.Default)
    coroutineScope.launch {
        ToastEvents.publish(Toast(message, durationMillis, imageVector = icon, iconTint = iconTint, alignment = alignment))
    }
}

/**
 * Displays a toast message with text and an [ImageBitmap] icon.
 *
 * @param message The text message to display.
 * @param icon The [ImageBitmap] to display as an icon.
 * @param iconTint The color tint for the icon.
 * @param durationMillis The duration the toast remains visible.
 * @param alignment The screen alignment for the toast.
 */
fun showToast(message: String, icon: ImageBitmap, iconTint: Color = ToastDefaults.DEFAULT_ICON_TINT, durationMillis: Long = ToastDefaults.DURATION_DEFAULT, alignment: Alignment = ToastDefaults.DEFAULT_ALIGNMENT) {
    val coroutineScope = CoroutineScope(Dispatchers.Default)
    coroutineScope.launch {
        ToastEvents.publish(Toast(message, durationMillis, imageBitmap = icon, iconTint = iconTint, alignment = alignment))
    }
}

/**
 * Displays a toast message with text and a [Painter] icon.
 *
 * @param message The text message to display.
 * @param icon The [Painter] to display as an icon.
 * @param iconTint The color tint for the icon.
 * @param durationMillis The duration the toast remains visible.
 * @param alignment The screen alignment for the toast.
 */
fun showToast(message: String, icon: Painter, iconTint: Color = ToastDefaults.DEFAULT_ICON_TINT, durationMillis: Long = ToastDefaults.DURATION_DEFAULT, alignment: Alignment = ToastDefaults.DEFAULT_ALIGNMENT) {
    val coroutineScope = CoroutineScope(Dispatchers.Default)
    coroutineScope.launch {
        ToastEvents.publish(Toast(message, durationMillis, painter = icon, iconTint = iconTint, alignment = alignment))
    }
}

private object ToastEvents {
    private val _events = MutableSharedFlow<Any>()
    val events = _events.asSharedFlow()

    suspend fun publish(event: Any) {
        _events.emit(event)
    }

    suspend inline fun <reified T> subscribe(crossinline onEvent: (T) -> Unit) {
        events.filterIsInstance<T>()
            .collectLatest { event ->
                currentCoroutineContext().ensureActive()
                onEvent(event)
            }
    }
}
