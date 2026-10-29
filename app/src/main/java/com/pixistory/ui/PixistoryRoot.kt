@file:OptIn(ExperimentalMaterial3Api::class)

package com.pixistory.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pixistory.AppContainer
import com.pixistory.R
import com.pixistory.ui.ai.AskScreen
import com.pixistory.ui.analysis.AnalysisScreen
import com.pixistory.ui.common.rememberFoldState
import com.pixistory.ui.devices.DeviceEditorScreen
import com.pixistory.ui.devices.DevicesScreen
import com.pixistory.ui.drops.PixelDropsScreen
import com.pixistory.ui.settings.AboutScreen
import com.pixistory.ui.settings.SettingsScreen
import com.pixistory.ui.shelf.ShelfScreen

enum class MainTab(@StringRes val label: Int, val icon: ImageVector) {
    DEVICES(R.string.tab_devices, Icons.Filled.Devices),
    DROPS(R.string.tab_drops, Icons.Filled.NewReleases),
    ANALYSIS(R.string.tab_analysis, Icons.Filled.Analytics),
    ASK(R.string.tab_ask, Icons.Filled.AutoAwesome),
    SETTINGS(R.string.tab_settings, Icons.Filled.Settings),
}

@Composable
fun PixistoryRoot(container: AppContainer, pendingAction: String?, onActionConsumed: () -> Unit) {
    val vm: AppViewModel = viewModel(factory = AppViewModel.factory(container))
    val fold = rememberFoldState()
    var tabName by rememberSaveable { mutableStateOf(MainTab.DEVICES.name) }
    val tab = MainTab.valueOf(tabName)
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    var shelfOpen by rememberSaveable { mutableStateOf(false) }
    var aboutOpen by rememberSaveable { mutableStateOf(false) }
    var showLaunch by rememberSaveable { mutableStateOf(true) }
    val editing by vm.editing.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { vm.messages.collect { snackbar.showSnackbar(it) } }
    LaunchedEffect(pendingAction) {
        when (pendingAction) {
            "com.pixistory.ADD_DEVICE" -> { tabName = MainTab.DEVICES.name; vm.startNew() }
            "com.pixistory.ASK_AI" -> tabName = MainTab.ASK.name
        }
        if (pendingAction != null) onActionConsumed()
    }
    BackHandler(enabled = tab != MainTab.DEVICES && editing == null && !shelfOpen && !aboutOpen) {
        tabName = MainTab.DEVICES.name
    }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbar) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                // Cover screen / phone: bottom bar. Unfolded Pixel Fold or tablet: side rail.
                if (!fold.isWide) {
                    NavigationBar {
                        MainTab.entries.forEach { t ->
                            NavigationBarItem(
                                selected = t == tab,
                                onClick = { tabName = t.name },
                                icon = { Icon(t.icon, contentDescription = null) },
                                label = { Text(stringResource(t.label), maxLines = 1) },
                            )
                        }
                    }
                }
            },
        ) { padding ->
            Row(Modifier.padding(padding).fillMaxSize()) {
                if (fold.isWide) {
                    NavigationRail {
                        Spacer(Modifier.weight(1f))
                        MainTab.entries.forEach { t ->
                            NavigationRailItem(
                                selected = t == tab,
                                onClick = { tabName = t.name },
                                icon = { Icon(t.icon, contentDescription = null) },
                                label = { Text(stringResource(t.label), maxLines = 1) },
                            )
                        }
                        Spacer(Modifier.weight(1f))
                    }
                }
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .then(if (fold.isWide) Modifier.navigationBarsPadding() else Modifier),
                ) {
                    when (tab) {
                        MainTab.DEVICES -> DevicesScreen(vm, fold, selectedId, { selectedId = it }, { shelfOpen = true })
                        MainTab.DROPS -> PixelDropsScreen(vm, fold)
                        MainTab.ANALYSIS -> AnalysisScreen(vm, fold)
                        MainTab.ASK -> AskScreen(vm, fold)
                        MainTab.SETTINGS -> SettingsScreen(vm, onOpenShelf = { shelfOpen = true }, onOpenAbout = { aboutOpen = true })
                    }
                }
            }
        }

        editing?.let { device ->
            Surface(Modifier.fillMaxSize()) {
                DeviceEditorScreen(
                    initial = device,
                    isNew = vm.editingIsNew,
                    catalogFirst = vm.editorStartsWithCatalog,
                    vm = vm,
                    onSaved = { selectedId = it.id; tabName = MainTab.DEVICES.name },
                    onClose = vm::closeEditor,
                )
            }
        }
        if (shelfOpen) ShelfScreen(vm, fold) { shelfOpen = false }
        if (aboutOpen) AboutScreen { aboutOpen = false }
        if (showLaunch) LaunchPopup { showLaunch = false }
    }
}
