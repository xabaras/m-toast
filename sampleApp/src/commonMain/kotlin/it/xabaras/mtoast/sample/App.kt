package it.xabaras.mtoast.sample

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.xabaras.mtoast.ToastContainer
import it.xabaras.mtoast.showToast
import mtoast.sampleapp.generated.resources.Res
import mtoast.sampleapp.generated.resources.mtoast
import org.jetbrains.compose.resources.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning

@Composable
@Preview(showBackground = true)
fun App() {
    val isDarkTheme = isSystemInDarkTheme()
    val colorScheme = if(isDarkTheme) darkColorScheme() else lightColorScheme()
    var text by remember { mutableStateOf("This is a toast message!") }
    val options = listOf("Simple Text", "With Icon", "Custom composable")
    var selectedOption by remember { mutableStateOf(options[0]) }

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        ToastContainer(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .safeContentPadding()
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier.size(256.dp),
                        painter = painterResource(Res.drawable.mtoast),
                        contentDescription = "mToast"
                    )
                }

                Text(
                    text = "Type the toast text",
                    fontWeight = FontWeight.Bold,
                )

                Spacer(Modifier.height(8.dp))

                TextField(
                    value = text,
                    onValueChange = {
                        text = it
                    },
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Select a toast type",
                    fontWeight = FontWeight.Bold,
                )

                Column {
                    options.forEach { text ->
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = { selectedOption = text }
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null
                            )
                            Text(
                                text = text,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            when (selectedOption) {
                                options[0] -> {
                                    // Show toast with simple text
                                    showToast(text)
                                }

                                options[1] -> {
                                    // Show toast with icon
                                    showToast(
                                        message = text,
                                        icon = Icons.Default.Warning
                                    )
                                }

                                else -> {
                                    // show toast with custom composable and duration
                                    showToast(4000L) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Image(
                                                painter = painterResource(Res.drawable.mtoast),
                                                contentDescription = "mToast logo",
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.Cyan)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                text = text,
                                                textAlign = TextAlign.Center,
                                                color = if (isDarkTheme) Color.Black else Color.White,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }) {
                        Text("Show Toast!")
                    }
                }
            }
        }
    }
}