package me.amrbashir.hijriwidget.preferences.composables

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.drawColorIndicator
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.addIf

@Composable
fun ColorPicker(
    scrollState: ScrollState?,
    onColorChanged: (Color) -> Unit,
) {
    val controller = rememberColorPickerController()
    controller.setAlpha(1f, fromUser = false)
    LaunchedEffect(Unit) {
        controller.setBrightness(1f, fromUser = false)
        controller.setWheelColor(Color.Blue)
    }

    var hexCode by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(Modifier.padding(16.dp)) {
        HsvColorPicker(
            modifier = Modifier
                .height(300.dp)
                .padding(16.dp),
            initialColor = Color(Preferences.customColor.value),
            drawOnPosSelected = {
                drawColorIndicator(
                    controller.selectedPoint.value,
                    controller.selectedColor.value,
                )
            },
            controller = controller,
            onColorChanged = {
                hexCode = it.hexCode.substring(2)
                onColorChanged(it.color)
            }
        )

        BrightnessSlider(
            modifier = Modifier
                .height(35.dp)
                .fillMaxWidth(),
            controller = controller
        )

        Spacer(modifier = Modifier.requiredHeight(16.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .addIf(scrollState != null) {
                    this.onFocusChanged {
                        if (it.isFocused) {
                            coroutineScope.launch {
                                delay(100)
                                scrollState?.animateScrollTo(5000)
                            }
                        }
                    }
                },
            singleLine = true,
            prefix = { Text("#") },
            value = hexCode,
            onValueChange = {
                hexCode = it.take(6)
                if (hexCode.length == 6) {
                    try {
                        val parsedColor = android.graphics.Color.parseColor("#FF$hexCode")
                        val color = Color(parsedColor)
                        controller.selectByColor(color, fromUser = false)
                    } catch (_: Exception) {
                    }
                }
            },
        )

        Spacer(modifier = Modifier.requiredHeight(16.dp))
    }

}