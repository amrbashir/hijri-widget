package me.amrbashir.hijriwidget.preference_activity.composables.ui

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.core.graphics.toColorInt
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import kotlinx.coroutines.launch
import me.amrbashir.hijriwidget.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ColorPicker(
    initialColor: Int,
    onColorChanged: (Color) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current

    var initColor by remember { mutableStateOf(Color(initialColor)) }
    var hexColor by remember { mutableStateOf(initColor.toHex()) }

    val updateColorFromPickerAction = { newHsvColor: HsvColor ->
        val newColor = newHsvColor.toColor()
        hexColor = newColor.toHex()
        onColorChanged(newColor)
    }

    val updateColorFromHexCodeAction = { hexStr: String ->
        hexColor = hexStr.removePrefix("#").take(8)
        val parsedColor = runCatching { Color(hexColor.toColorInt()) }.getOrNull()
        if (parsedColor != null) {
            initColor = parsedColor
            onColorChanged(parsedColor)
        }
    }

    val pasteColorFromClipboardAction: () -> Unit = {
        coroutineScope.launch {
            clipboard.getClipEntry()?.clipData?.getItemAt(0)?.let {
                updateColorFromHexCodeAction(it.text.toString())
            }
        }
    }


    key(initColor) {
        ClassicColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1F),
            color = HsvColor.from(initColor),
            onColorChanged = updateColorFromPickerAction
        )
    }

    OutlinedTextField(
        label = { Text("Hex Color (#AARRGGBB)") },
        modifier = Modifier.fillMaxWidth(),
        prefix = { Text("#") },
        value = hexColor,
        onValueChange = updateColorFromHexCodeAction,
        singleLine = true,
        suffix = {
            IconButton(onClick = pasteColorFromClipboardAction) {
                Icon(
                    modifier = Modifier.fillMaxHeight(),
                    painter = painterResource(R.drawable.outline_content_paste_24),
                    contentDescription = null,
                )
            }
        }
    )
}

private fun Color.toHex(): String {
    val alpha = (this.alpha * 255).toInt()
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()

    return String.format("%02x%02x%02x%02x", alpha, red, green, blue)
}
