package me.amrbashir.hijriwidget.preferences.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.drawColorIndicator
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import me.amrbashir.hijriwidget.Preferences

@Composable
fun ColorPickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Color) -> Unit,
) {
    val controller = rememberColorPickerController()
    controller.setAlpha(1f, fromUser = false)
    LaunchedEffect(Unit) {
        controller.setBrightness(1f, fromUser = false)
        controller.setWheelColor(Color.Blue)
    }

    var hexCode by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(
                    onClick = {
                        onConfirm(controller.selectedColor.value)
                    }
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
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
                }
            )

            BrightnessSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(35.dp)
                    .align(Alignment.CenterHorizontally),
                controller = controller
            )

            OutlinedTextField(
                prefix = { Text("#") },
                value = hexCode,

                onValueChange = {
                    hexCode = it.take(6)
                    if (hexCode.length == 6) {
                        try {
                            val parsedColor = android.graphics.Color.parseColor("#FF$hexCode")
                            controller.selectByColor(Color(parsedColor), fromUser = false)
                        } catch (_: Exception) {
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}