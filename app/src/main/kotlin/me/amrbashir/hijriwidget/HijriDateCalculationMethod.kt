package me.amrbashir.hijriwidget

import androidx.annotation.StringRes

enum class HijriDateCalculationMethod(
    val id: String,
    @param:StringRes val label: Int,
    @param:StringRes val description: Int
) {
    ISLAMIC(
        "islamic",
        R.string.hijri_date_calculation_method_islamic_label,
        R.string.hijri_date_calculation_method_islamic_description,
    ),
    ISLAMIC_UMALQURA(
        "islamic-umalqura",
        R.string.hijri_date_calculation_method_islamic_umalqura_label,
        R.string.hijri_date_calculation_method_islamic_umalqura_description,
    ),
    ISLAMIC_CIVIL(
        "islamic-civil",
        R.string.hijri_date_calculation_method_islamic_civil_label,
        R.string.hijri_date_calculation_method_islamic_civil_description,
    );

    companion object {
        fun fromId(id: String): HijriDateCalculationMethod {
            return when (id) {
                "islamic" -> ISLAMIC
                "islamic-umalqura" -> ISLAMIC_UMALQURA
                "islamic-civil" -> ISLAMIC_CIVIL
                else -> ISLAMIC_UMALQURA
            }
        }
    }
}
