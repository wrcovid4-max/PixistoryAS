package com.pixistory.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/** English and Urdu. The choice is stored by AppCompat (and survives restarts); empty means "follow the phone". */
object AppLanguage {
    /** "en", "ur" or "" for the phone's own language. */
    fun saved(): String = AppCompatDelegate.getApplicationLocales().toLanguageTags().substringBefore(',').substringBefore('-')

    fun set(tag: String) {
        AppCompatDelegate.setApplicationLocales(
            if (tag.isBlank()) LocaleListCompat.getEmptyLocaleList() else LocaleListCompat.forLanguageTags(tag),
        )
    }
}
