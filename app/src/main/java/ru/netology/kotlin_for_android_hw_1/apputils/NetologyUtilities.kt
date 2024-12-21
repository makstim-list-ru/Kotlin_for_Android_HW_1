package ru.netology.kotlin_for_android_hw_1.apputils

import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow

object NetologyUtilities {
    fun getFormatedNumber(count: Long): String {
        if (count < 1000) return "" + count
        val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
        if (count / 1000.0.pow(exp.toDouble()) >= 10) {
            return String.format("%.0f%c", count / 1000.0.pow(exp.toDouble()), "kMGTPE"[exp - 1])
        } else {
            return String.format(
                "%.1f%c",
                floor(count / 1000.0.pow(exp.toDouble()) * 10) / 10,
                "kMGTPE"[exp - 1]
            )
        }
    }
}