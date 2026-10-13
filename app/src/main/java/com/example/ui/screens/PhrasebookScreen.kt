package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IndianLanguage
import com.example.model.IndianPhrase
import com.example.model.IndianPhraseData
import com.example.model.PhraseCategory
import com.example.ui.IndianVoiceViewModel
import com.example.ui.theme.SaffronPrimary

@Composable
fun PhrasebookScreen(
    viewModel: IndianVoiceViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val filteredPhrases = IndianPhraseData.phrases.filter { phrase ->
        val matchesLang = phrase.language == uiState.phrasebookLanguage
        val matchesCategory = uiState.selectedPhraseCategory == null || phrase.category == uiState.selectedPhraseCategory
        val matchesSearch = uiState.phraseSearchQuery.isBlank() ||
                phrase.scriptText.contains(uiState.phraseSearchQuery, ignoreCase = true) ||
                phrase.transliteration.contains(uiState.phraseSearchQuery, ignoreCase = true) ||
                phrase.englishMeaning.contains(uiState.phraseSearchQuery, ignoreCase = true)

        matchesLang && matchesCategory && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("phrasebook_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Language Selector Row
        LanguageSelectorCard(
            selectedLanguage = uiState.phrasebookLanguage,
            onSelectLanguage = { viewModel.setPhrasebookLanguage(it) }
        )

        // Search Bar
        OutlinedTextField(
            value = uiState.phraseSearchQuery,
            onValueChange = { viewModel.setPhraseSearch(it) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("phrase_search_field"),
            placeholder = { Text("Search Indian phrases...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.outline
                )
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )
        )

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.selectedPhraseCategory == null,
                onClick = { viewModel.setPhraseCategory(null) },
                label = { Text("All Categories") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("category_all")
            )

            PhraseCategory.values().forEach { category ->
                val isSelected = uiState.selectedPhraseCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setPhraseCategory(category) },
                    label = { Text("${category.icon} ${category.displayName}") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("category_${category.name.lowercase()}")
                )
            }
        }

        // Phrase List
        if (filteredPhrases.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No phrases matching \"${uiState.phraseSearchQuery}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("phrases_list"),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredPhrases, key = { it.id }) { phrase ->
                    PhraseItemCard(
                        phrase = phrase,
                        onSpeak = { viewModel.speakPhrase(phrase) },
                        onShareMp3 = { viewModel.sharePhraseAsMp3(phrase) },
                        onLoadToStudio = { viewModel.loadPhraseIntoStudio(phrase) },
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Indian Phrase", phrase.scriptText)
                            clipboard.setPrimaryClip(clip)
                            viewModel.showSnackbar("Copied phrase to clipboard!")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PhraseItemCard(
    phrase: IndianPhrase,
    onSpeak: () -> Unit,
    onShareMp3: () -> Unit,
    onLoadToStudio: () -> Unit,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("phrase_card_${phrase.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Category Badge & Actions Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "${phrase.category.icon} ${phrase.category.displayName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy phrase",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onLoadToStudio,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Open in Studio",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Main Script Text
            Text(
                text = phrase.scriptText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
            )

            // Transliteration
            if (phrase.transliteration.isNotBlank() && phrase.transliteration != phrase.scriptText) {
                Text(
                    text = phrase.transliteration,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = SaffronPrimary
                )
            }

            // Meaning in English
            Text(
                text = "Meaning: \"${phrase.englishMeaning}\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            // Action Buttons: Listen & Share MP3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onSpeak,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(42.dp)
                        .testTag("speak_btn_${phrase.id}"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Listen (${phrase.language.displayName})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }

                androidx.compose.material3.OutlinedButton(
                    onClick = onShareMp3,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("share_mp3_btn_${phrase.id}"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Share,
                        contentDescription = "Share MP3",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Share MP3",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
