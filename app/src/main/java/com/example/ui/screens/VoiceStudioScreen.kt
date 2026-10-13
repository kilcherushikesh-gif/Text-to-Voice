package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IndianLanguage
import com.example.model.IndianVoiceItem
import com.example.model.IndianVoicePersona
import com.example.model.VoiceGenderFilter
import com.example.tts.TtsLanguageStatus
import com.example.ui.IndianVoiceViewModel
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PeacockTeal
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.SaffronPrimaryLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceStudioScreen(
    viewModel: IndianVoiceViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val ttsState by viewModel.ttsManager.state.collectAsState()
    val amplitudes by viewModel.ttsManager.audioAmplitudes.collectAsState()
    val context = LocalContext.current

    val scrollState = rememberScrollState()
    var showTuneSliders by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("voice_studio_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Language Selector Section (All 6 Indian Languages)
        LanguageSelectorCard(
            selectedLanguage = uiState.selectedLanguage,
            onSelectLanguage = { viewModel.selectLanguage(it) }
        )

        // Engine Status Banner if missing data
        EngineStatusBanner(
            language = uiState.selectedLanguage,
            ttsState = ttsState,
            onOpenDiagnostics = { viewModel.showDiagnosticDialog() }
        )

        // Text Input Editor Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("text_input_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Enter Text in ${uiState.selectedLanguage.displayName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${uiState.selectedLanguage.nativeName} (${uiState.selectedLanguage.regionTag})",
                            style = MaterialTheme.typography.labelSmall,
                            color = SaffronPrimary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Paste button
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = clipboard.primaryClip
                                if (clip != null && clip.itemCount > 0) {
                                    val text = clip.getItemAt(0).text?.toString() ?: ""
                                    if (text.isNotBlank()) {
                                        viewModel.updateTextInput(text)
                                    }
                                }
                            },
                            modifier = Modifier.testTag("paste_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = "Paste text",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Clear button
                        if (uiState.textInput.isNotBlank()) {
                            IconButton(
                                onClick = { viewModel.updateTextInput("") },
                                modifier = Modifier.testTag("clear_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear text",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.textInput,
                    onValueChange = { viewModel.updateTextInput(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("tts_text_input"),
                    placeholder = {
                        Text(
                            text = "Type text in ${uiState.selectedLanguage.displayName} (${uiState.selectedLanguage.nativeName})...",
                            color = MaterialTheme.colorScheme.outline
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.textInput.length} characters",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    // Quick reset to sample text
                    Text(
                        text = "Reset Sample",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                viewModel.updateTextInput(uiState.selectedLanguage.defaultText)
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .testTag("reset_sample_button")
                    )
                }

                // Quick Starter Chips
                Spacer(modifier = Modifier.height(10.dp))
                QuickPhraseStartersRow(
                    language = uiState.selectedLanguage,
                    onSelect = { starterText ->
                        viewModel.updateTextInput(starterText)
                    }
                )
            }
        }

        // Voice Persona & Customization Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("voice_customization_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = SaffronPrimary
                        )
                        Text(
                            text = "Voice Persona & Tone",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = if (showTuneSliders) "Hide Sliders" else "Fine Tune",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable { showTuneSliders = !showTuneSliders }
                            .padding(4.dp)
                    )
                }

                // Voice Personas / Age Groups
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Voice Persona / Age Profile",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.outline
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IndianVoicePersona.values().forEach { persona ->
                            val isSelected = uiState.selectedPersona == persona
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectPersona(persona) },
                                label = {
                                    Text(
                                        text = "${persona.icon} ${persona.displayName}",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.testTag("persona_${persona.id}")
                            )
                        }
                    }
                }

                // Gender Filter and Installed Voice Selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Voice Gender & Variant",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )

                        // Gender filter buttons (All, Female, Male)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            VoiceGenderFilter.values().forEach { filter ->
                                val isSelected = uiState.selectedGenderFilter == filter
                                Text(
                                    text = "${filter.icon} ${filter.displayName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) SaffronPrimary else MaterialTheme.colorScheme.outline,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                        .clickable { viewModel.selectGenderFilter(filter) }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    val allVoices = ttsState.availableVoices[uiState.selectedLanguage] ?: emptyList()
                    val filteredVoices = when (uiState.selectedGenderFilter) {
                        VoiceGenderFilter.ALL -> allVoices
                        VoiceGenderFilter.FEMALE -> allVoices.filter { it.genderHint == "Female" }
                        VoiceGenderFilter.MALE -> allVoices.filter { it.genderHint == "Male" }
                    }

                    if (filteredVoices.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            filteredVoices.forEach { voiceItem ->
                                val isSelected = uiState.selectedVoice?.id == voiceItem.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.selectVoice(voiceItem) },
                                    label = {
                                        Text(
                                            text = when (voiceItem.genderHint) {
                                                "Female" -> "👩 ${voiceItem.displayName}"
                                                "Male" -> "👨 ${voiceItem.displayName}"
                                                else -> "🗣️ ${voiceItem.displayName}"
                                            },
                                            fontSize = 12.sp
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier.testTag("voice_chip_${voiceItem.id}")
                                )
                            }
                        }
                    }
                }

                // Expandable detailed Pitch & Speed sliders
                AnimatedVisibility(visible = showTuneSliders) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Speech Rate / Speed slider
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Speech Speed",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = String.format("%.2fx", uiState.speechRate),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Slider(
                                value = uiState.speechRate,
                                onValueChange = { viewModel.setSpeechRate(it) },
                                valueRange = 0.5f..2.0f,
                                steps = 14,
                                colors = SliderDefaults.colors(
                                    thumbColor = SaffronPrimary,
                                    activeTrackColor = SaffronPrimary
                                ),
                                modifier = Modifier.testTag("speed_slider")
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                QuickPresetTag("Slow (0.8x)", uiState.speechRate == 0.8f) {
                                    viewModel.setSpeechRate(0.8f)
                                }
                                QuickPresetTag("Normal (1.0x)", uiState.speechRate == 1.0f) {
                                    viewModel.setSpeechRate(1.0f)
                                }
                                QuickPresetTag("Fast (1.3x)", uiState.speechRate == 1.3f) {
                                    viewModel.setSpeechRate(1.3f)
                                }
                            }
                        }

                        // Pitch slider
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Voice Pitch",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = String.format("%.2fx", uiState.pitch),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Slider(
                                value = uiState.pitch,
                                onValueChange = { viewModel.setPitch(it) },
                                valueRange = 0.5f..2.0f,
                                steps = 14,
                                colors = SliderDefaults.colors(
                                    thumbColor = AmberSecondary,
                                    activeTrackColor = AmberSecondary
                                ),
                                modifier = Modifier.testTag("pitch_slider")
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                QuickPresetTag("Deep (0.80x)", uiState.pitch == 0.80f) {
                                    viewModel.setPitch(0.80f)
                                }
                                QuickPresetTag("Natural (1.0x)", uiState.pitch == 1.0f) {
                                    viewModel.setPitch(1.0f)
                                }
                                QuickPresetTag("High (1.30x)", uiState.pitch == 1.30f) {
                                    viewModel.setPitch(1.30f)
                                }
                            }
                        }
                    }
                }

                // Tanpura Ambiance Drone Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪕 ", fontSize = 16.sp)
                            Text(
                                text = "Indian Tanpura Drone",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Play peaceful background tanpura string drone while speaking",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Switch(
                        checked = uiState.isTanpuraDroneEnabled,
                        onCheckedChange = { viewModel.toggleTanpuraDrone() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SaffronPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("tanpura_toggle")
                    )
                }
            }
        }

        // Live Audio Waveform Visualizer & Action Center
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("waveform_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (ttsState.isSpeaking) "Speaking ${uiState.selectedLanguage.displayName} (${uiState.selectedPersona.displayName})..." else "Engine: Ready",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (ttsState.isSpeaking) SaffronPrimary else MaterialTheme.colorScheme.outline
                    )

                    if (ttsState.isSpeaking) {
                        Surface(
                            shape = CircleShape,
                            color = SaffronPrimaryLight.copy(alpha = 0.2f),
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(4.dp)
                                    .background(SaffronPrimary, CircleShape)
                            )
                        }
                    }
                }

                WaveformVisualizer(
                    amplitudes = amplitudes,
                    isSpeaking = ttsState.isSpeaking,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                // Large Main Action Speak / Stop Button
                Button(
                    onClick = {
                        if (ttsState.isSpeaking) {
                            viewModel.stopSpeaking()
                        } else {
                            viewModel.speakCurrentText()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("speak_main_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (ttsState.isSpeaking) MaterialTheme.colorScheme.error else SaffronPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = if (ttsState.isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (ttsState.isSpeaking) "Stop voice" else "Speak voice",
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (ttsState.isSpeaking) "Stop Speaking" else "Speak / Listen (${uiState.selectedLanguage.displayName})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Audio Export Buttons: Save as MP3 & Share MP3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.openSaveDialog() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_mp3_clip_button"),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !uiState.isSynthesizingFile
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save voice clip as MP3",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Save as MP3", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { viewModel.shareCurrentSpeech() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_mp3_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PeacockTeal
                        ),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !uiState.isSynthesizingFile
                    ) {
                        if (uiState.isSynthesizingFile) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share MP3",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Share MP3", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageSelectorCard(
    selectedLanguage: IndianLanguage,
    onSelectLanguage: (IndianLanguage) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("language_selector_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Indian Language (6 Available)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "🇮🇳 Bharat",
                    style = MaterialTheme.typography.labelSmall,
                    color = SaffronPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Scrollable 6 languages row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IndianLanguage.values().forEach { lang ->
                    val isSelected = selectedLanguage == lang
                    LanguageOptionButton(
                        language = lang,
                        isSelected = isSelected,
                        onClick = { onSelectLanguage(lang) },
                        modifier = Modifier.width(110.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageOptionButton(
    language: IndianLanguage,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val borderColor = if (isSelected) SaffronPrimary else Color.Transparent

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(width = 1.5.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp)
            .testTag("lang_btn_${language.languageCode}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🇮🇳", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = language.nativeName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
        Text(
            text = language.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) SaffronPrimary else MaterialTheme.colorScheme.outline,
            maxLines = 1
        )
    }
}

@Composable
fun QuickPresetTag(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = if (isSelected) SaffronPrimary else MaterialTheme.colorScheme.outline,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun QuickPhraseStartersRow(
    language: IndianLanguage,
    onSelect: (String) -> Unit
) {
    val starters = when (language) {
        IndianLanguage.MARATHI -> listOf(
            "नमस्कार! कसे आहात?",
            "चला, चहा पिऊया!",
            "गणपती बाप्पा मोरया!",
            "काळजी घ्या मंडळी."
        )
        IndianLanguage.HINDI -> listOf(
            "नमस्ते! आप कैसे हैं?",
            "चाय पीने का समय हो गया!",
            "जय हिन्द, वन्दे मातरम्!",
            "चिंता मत करो सब ठीक होगा."
        )
        IndianLanguage.INDIAN_ENGLISH -> listOf(
            "Namaste! Warm greetings.",
            "Let's grab a cutting chai!",
            "Everything is first class!",
            "Wishing you great success."
        )
        IndianLanguage.TAMIL -> listOf(
            "வணக்கம்! எப்படி இருக்கீங்க?",
            "வாங்க, காபி குடிக்கலாம்!",
            "பொங்கலோ பொங்கல்!",
            "எல்லாம் நல்லபடியா நடக்கும்."
        )
        IndianLanguage.TELUGU -> listOf(
            "నమస్కారం! బాగున్నారా?",
            "రండి, టీ తాగుదాం!",
            "సంక్రాంతి శుభాకాంక్షలు!",
            "ధైర్యంగా ముందుకు సాగండి."
        )
        IndianLanguage.BENGALI -> listOf(
            "নমস্কার! কেমন আছেন?",
            "চলুন, এক কাপ চা খাওয়া যাক!",
            "শুভ নববর্ষের শুভেচ্ছা!",
            "সব ঠিক হয়ে যাবে।"
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        starters.forEach { phrase ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSelect(phrase) }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = phrase,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EngineStatusBanner(
    language: IndianLanguage,
    ttsState: com.example.tts.IndianTtsState,
    onOpenDiagnostics: () -> Unit
) {
    val status = ttsState.languageStatuses[language] ?: TtsLanguageStatus.Checking

    if (status is TtsLanguageStatus.MissingData || status is TtsLanguageStatus.NotSupported) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenDiagnostics() }
                .testTag("engine_warning_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${language.displayName} Voice Pack Recommended",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "Tap here to check Google Text-to-Speech settings for ${language.displayName}.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}
