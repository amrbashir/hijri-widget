package me.amrbashir.hijriwidget

enum class CalendarCalculationMethod {
    ISLAMIC {
        override fun toString(): String  = "islamic"
        override fun label(): String  = "Islamic"
        override fun desc(): String  = "Uses pure astronomical calculations to determine when the new moon occurs"
    },
    ISLAMIC_UMALQURA {
        override fun toString(): String  = "islamic-umalqura"
        override fun label(): String  = "Islamic Umm al-Qura"
        override fun desc(): String  = "Uses a fixed algorithmic approach with a 30-year cycle"
    },
    ISLAMIC_CIVIL {
        override fun toString(): String  = "islamic-civil"
        override fun label(): String  = "Islamic Civil"
        override fun desc(): String  = "Based on the calculations used by the Umm al-Qura University in Mecca"
    };

    abstract fun label(): String
    abstract fun desc(): String
}
