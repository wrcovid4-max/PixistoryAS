@file:OptIn(ExperimentalMaterial3Api::class)

package com.pixistory.ui.features

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixistory.R
import com.pixistory.ui.AppViewModel
import com.pixistory.ui.common.FoldState
import com.pixistory.ui.common.PixiChip
import com.pixistory.ui.common.PixiTopBar
import com.pixistory.ui.drops.PixelDropsScreen

/** Discover: everything about Google hardware and software in one place, switched with quiet tabs. */
@Composable
fun DiscoverScreen(vm: AppViewModel, fold: FoldState) {
    val data = vm.container.features
    var section by rememberSaveable { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize()) {
        PixiTopBar(title = { Text(stringResource(R.string.tab_discover)) })
        Row(
            Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PixiChip(selected = section == 0, onClick = { section = 0 }, label = { Text(stringResource(R.string.tab_drops)) })
            PixiChip(selected = section == 1, onClick = { section = 1 }, label = { Text(stringResource(R.string.feat_availability)) })
            PixiChip(selected = section == 2, onClick = { section = 2 }, label = { Text(stringResource(R.string.feat_graveyard)) })
            PixiChip(selected = section == 3, onClick = { section = 3 }, label = { Text(stringResource(R.string.feat_fold)) })
        }
        Spacer(Modifier.height(4.dp))
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (section) {
                0 -> PixelDropsScreen(vm, fold, embedded = true)
                1 -> AvailabilitySection(data, fold)
                2 -> GraveyardSection(data)
                else -> FoldAppsSection(data)
            }
        }
    }
}
