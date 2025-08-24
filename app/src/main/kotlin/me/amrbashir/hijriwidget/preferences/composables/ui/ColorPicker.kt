package me.amrbashir.hijriwidget.preferences.composables.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.drawColorIndicator
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import me.amrbashir.hijriwidget.Preferences

@Composable
fun ColorPicker(
    initialColor: Int,
    onColorChanged: (Color) -> Unit,
) {
    val controller = rememberColorPickerController()
    LaunchedEffect(Unit) {
        controller.setBrightness(1f, fromUser = false)
        controller.selectByColor(Color(initialColor), fromUser = false)
    }

    var hexCode by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HsvColorPicker(
            modifier = Modifier.height(300.dp),
            initialColor = Color(Preferences.textCustomColor.value),
            drawOnPosSelected = {
                drawColorIndicator(
                    controller.selectedPoint.value,
                    controller.selectedColor.value,
                )
            },
            controller = controller,
            onColorChanged = {
                hexCode = it.hexCode
                onColorChanged(it.color)
            }
        )

        BrightnessSlider(
            modifier = Modifier
                .height(35.dp)
                .fillMaxWidth(),
            controller = controller
        )


        AlphaSlider(
            modifier = Modifier
                .height(35.dp)
                .fillMaxWidth(),
            controller = controller
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            prefix = { Text("#") },
            value = hexCode,
            onValueChange = {
                hexCode = it.take(8)
                if (hexCode.length == 8) {
                    try {
                        val parsedColor = hexCode.toColorInt()
                        val color = Color(parsedColor)
                        controller.selectByColor(color, fromUser = false)
                    } catch (_: Exception) {
                    }
                }
            },
        )
    }

}
