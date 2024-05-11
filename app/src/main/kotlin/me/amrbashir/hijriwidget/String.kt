package me.amrbashir.hijriwidget

import me.amrbashir.hijriwidget.settings.SupportedLanguage


fun String.convertNumbersToLang(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.English -> this
        else -> this.replace("0", "٠")
            .replace("1", "١")
            .replace("2", "٢")
            .replace("3", "٣")
            .replace("4", "٤")
            .replace("5", "٥")
            .replace("6", "٦")
            .replace("7", "٧")
            .replace("8", "٨")
            .replace("9", "٩")
    }

}
