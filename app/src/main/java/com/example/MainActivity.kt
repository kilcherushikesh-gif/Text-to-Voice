package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.IndianLanguage
import com.example.tts.TtsLanguageStatus
import com.example.ui.AppNavTab
import com.example.ui.IndianVoiceViewModel
import com.example.ui.dialogs.SaveClipDialog
import com.example.ui.dialogs.TtsDiagnosticDialog
import com.example.ui.screens.IndianSoundboardScreen
import com.example.ui.screens.PhrasebookScreen
import com.example.ui.screens.SavedClipsScreen
import com.example.ui.screens.VoiceStudioScreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.SaffronPrimaryLight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DesiVoiceApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesiVoiceApp(
    viewModel: IndianVoiceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val ttsState by viewModel.ttsManager.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Display snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.dismissSnackbar()
        }
    }

    // BackHandler for sub-tabs to return to Studio
    if (uiState.currentTab != AppNavTab.STUDIO) {
        BackHandler {
            viewModel.selectTab(AppNavTab.STUDIO)
        }
    }

    // Check if any language needs voice pack download
    val needsAttention = ttsState.languageStatuses.values.any { it is TtsLanguageStatus.MissingData }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SaffronPrimary,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🇮🇳", fontSize = 20.sp)
                            }
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Text to Voice",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SaffronPrimary
                                )
                            }
                            Text(
                                text = "मराठी • हिंदी • English • தமிழ் • తెలుగు • বাংলা",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                },
                actions = {
                    // Diagnostics / TTS Settings Button
                    IconButton(
                        onClick = { viewModel.showDiagnosticDialog() },
                        modifier = Modifier.testTag("appbar_diagnostic_btn")
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Voice Engine Diagnostics",
                                tint = if (needsAttention) SaffronPrimary else MaterialTheme.colorScheme.outline
                            )
                            if (needsAttention) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.error)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                AppNavTab.values().forEach { tab ->
                    val isSelected = uiState.currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Text(
                                text = tab.icon,
                                fontSize = if (isSelected) 22.sp else 18.sp
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronPrimary,
                            selectedTextColor = SaffronPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                AppNavTab.STUDIO -> VoiceStudioScreen(viewModel = viewModel)
                AppNavTab.PHRASEBOOK -> PhrasebookScreen(viewModel = viewModel)
                AppNavTab.SOUNDBOARD -> IndianSoundboardScreen(viewModel = viewModel)
                AppNavTab.SAVED_CLIPS -> SavedClipsScreen(viewModel = viewModel)
            }
        }

        // Save Clip Dialog
        SaveClipDialog(
            visible = uiState.saveDialogVisible,
            title = uiState.saveClipTitle,
            language = uiState.selectedLanguage,
            textExcerpt = uiState.textInput,
            onTitleChange = { viewModel.updateSaveTitle(it) },
            onConfirm = { viewModel.confirmSaveClip() },
            onDismiss = { viewModel.dismissSaveDialog() }
        )

        // TTS Diagnostic Dialog
        TtsDiagnosticDialog(
            visible = uiState.diagnosticDialogVisible,
            ttsState = ttsState,
            onOpenSettings = { viewModel.openAndroidTtsSettings() },
            onInstallData = { viewModel.openInstallTtsData() },
            onDismiss = { viewModel.dismissDiagnosticDialog() }
        )
    }
}
