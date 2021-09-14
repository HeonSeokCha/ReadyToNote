package com.chs.readytonote

object Constants {
    const val DATA_STORE: String = "DataStore"
    const val UI_STATUS: String = "UiStatus"
    const val WHITE_MODE: String = "WhiteMode"
    const val DARK_MODE: String = "DarkMode"
    const val DEFAULT_MODE: String = "Default"
    const val DATE_PATTERN: String = "yyyy년 MM월 dd일 E"

    const val NOTE_DEFAULT_COLOR: String = "#333333"
    const val NOTE_YELLOW_COLOR: String = "#FDBE3B"
    const val NOTE_RED_COLOR: String = "#FF4842"
    const val NOTE_BLUE_COLOR: String = "#3A52FC"
    const val NOTE_PURPLE_COLOR: String = "#967FFA"

    val NOTE_COLOR_LIST: List<String> = listOf(
        NOTE_DEFAULT_COLOR,
        NOTE_YELLOW_COLOR,
        NOTE_RED_COLOR,
        NOTE_BLUE_COLOR,
        NOTE_PURPLE_COLOR
    )
}