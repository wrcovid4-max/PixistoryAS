package com.pixistory.util

import java.util.Currency
import java.util.Locale

/**
 * The currencies offered in CashMemer's receipt generator, with flags and the same symbols it prints.
 * IRT is the Iranian Toman (not an ISO code): 1 Toman = 10 Rial.
 */
object Currencies {
    val COMMON = listOf("PKR", "USD", "EUR", "GBP", "SAR", "AED", "INR", "CNY", "IRR", "IRT", "TRY", "RUB", "JPY", "CAD", "AUD")

    fun flagOf(code: String): String {
        if (code == "EUR") return "\uD83C\uDDEA\uD83C\uDDFA"
        if (code == "IRT") return "\uD83C\uDDEE\uD83C\uDDF7"
        if (code.length != 3) return ""
        val country = code.substring(0, 2).uppercase()
        if (country.any { it !in 'A'..'Z' }) return ""
        val base = 0x1F1E6
        return String(Character.toChars(base + (country[0] - 'A'))) + String(Character.toChars(base + (country[1] - 'A')))
    }

    fun symbolOf(code: String): String = when (code) {
        "IRT" -> "تومان"
        "IRR" -> "﷼"
        "PKR" -> "Rs"
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        "INR" -> "₹"
        "AED" -> "درهم"
        "SAR" -> "ريال"
        else -> runCatching { Currency.getInstance(code).symbol }.getOrDefault(code)
    }

    fun nameOf(code: String): String = when (code) {
        "IRT" -> if (Locale.getDefault().language == "ur") "ایرانی تومان" else "Iranian Toman"
        else -> runCatching { Currency.getInstance(code).getDisplayName(Locale.getDefault()) }.getOrDefault(code)
    }
}
