package me.amrbashir.hijriwidget

enum class HijriDateCalculationMethod(val id: String, val label: String, val description: String) {
    ISLAMIC(
        "islamic",
        "Isalmic",
        "Uses pure astronomical calculations to determine when the new moon occurs"
    ),
    ISLAMIC_UMALQURA(
        "islamic-umalqura",
        "Islamic Umm al-Qura",
        "Based on the calculations used by the Umm al-Qura University in Mecca"
    ),
    ISLAMIC_CIVIL(
        "islamic-civil",
        "Islamic Civil",
        "Uses a fixed algorithmic approach with a 30-year cycle"
    ),
}
