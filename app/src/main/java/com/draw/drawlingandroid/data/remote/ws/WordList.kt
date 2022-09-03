package com.draw.drawlingandroid.data.remote.ws

enum class WordList(val uiValue: String) {
    PROGRAMMERS_WORDLIST("Programmers Word List"),
    DEFAULT_WORDLIST("Default Word List");

    companion object {
        fun fromUiValue(str: String) = when (str) {
            PROGRAMMERS_WORDLIST.uiValue -> PROGRAMMERS_WORDLIST
            else -> DEFAULT_WORDLIST
        }
    }
}
