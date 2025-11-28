package ru.myitschool.work.core

import ru.myitschool.work.core.OurConstants.SHABLON

class Utils {
    companion object {
        fun CheckCodeInput(text : String) : Boolean{
            return !text.isEmpty() && text.length == 4 && text.matches(Regex(SHABLON))
        }
    }
}