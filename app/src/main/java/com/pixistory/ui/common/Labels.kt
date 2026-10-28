package com.pixistory.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.pixistory.R
import com.pixistory.data.model.Condition
import com.pixistory.data.model.DeviceStatus
import com.pixistory.data.model.DeviceType
import com.pixistory.util.PatchStatus

/** "en" or "ur", following the language the app is currently shown in. */
@Composable
fun currentLang(): String {
    val tag = LocalConfiguration.current.locales[0].language
    return if (tag == "ur") "ur" else "en"
}

@Composable
fun DeviceType.text(): String = stringResource(
    when (this) {
        DeviceType.PHONE -> R.string.type_phone
        DeviceType.FOLDABLE -> R.string.type_foldable
        DeviceType.TABLET -> R.string.type_tablet
        DeviceType.WATCH -> R.string.type_watch
        DeviceType.EARBUDS -> R.string.type_earbuds
        DeviceType.TV -> R.string.type_tv
        DeviceType.LAPTOP -> R.string.type_laptop
        DeviceType.CAMERA -> R.string.type_camera
        DeviceType.ACCESSORY -> R.string.type_accessory
        DeviceType.OTHER -> R.string.type_other
    },
)

@Composable
fun DeviceStatus.text(): String = stringResource(
    when (this) {
        DeviceStatus.OWNED -> R.string.status_owned
        DeviceStatus.WISHLIST -> R.string.status_wishlist
        DeviceStatus.SOLD -> R.string.status_sold
    },
)

@Composable
fun Condition.text(): String = stringResource(
    when (this) {
        Condition.NEW -> R.string.cond_new
        Condition.EXCELLENT -> R.string.cond_excellent
        Condition.GOOD -> R.string.cond_good
        Condition.FAIR -> R.string.cond_fair
        Condition.POOR -> R.string.cond_poor
        Condition.BROKEN -> R.string.cond_broken
    },
)

@Composable
fun PatchStatus.text(): String = stringResource(
    when (this) {
        PatchStatus.CURRENT -> R.string.patch_current
        PatchStatus.AGING -> R.string.patch_aging
        PatchStatus.OLD -> R.string.patch_old
        PatchStatus.STALE -> R.string.patch_stale
        PatchStatus.UNKNOWN -> R.string.patch_unknown
    },
)
