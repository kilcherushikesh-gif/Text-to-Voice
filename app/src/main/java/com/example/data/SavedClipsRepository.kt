package com.example.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.model.IndianLanguage
import com.example.model.SavedClip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class SavedClipsRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("desi_saved_clips_pref", Context.MODE_PRIVATE)
    private val keyClips = "saved_audio_clips"

    private val _clips = MutableStateFlow<List<SavedClip>>(emptyList())
    val clips: StateFlow<List<SavedClip>> = _clips.asStateFlow()

    init {
        loadClips()
    }

    private fun loadClips() {
        val jsonString = prefs.getString(keyClips, null) ?: return
        try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<SavedClip>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val langStr = obj.optString("language", "MARATHI")
                val lang = try {
                    IndianLanguage.valueOf(langStr)
                } catch (_: Exception) {
                    IndianLanguage.MARATHI
                }

                val clip = SavedClip(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    text = obj.getString("text"),
                    language = lang,
                    voiceName = obj.optString("voiceName", "Default"),
                    speechRate = obj.optDouble("speechRate", 1.0).toFloat(),
                    pitch = obj.optDouble("pitch", 1.0).toFloat(),
                    filePath = obj.getString("filePath"),
                    createdAtMillis = obj.getLong("createdAtMillis"),
                    durationSeconds = obj.optDouble("durationSeconds", 0.0).toFloat(),
                    format = obj.optString("format", "MP3"),
                    persona = obj.optString("persona", "Natural")
                )
                if (File(clip.filePath).exists()) {
                    list.add(clip)
                }
            }
            _clips.value = list.sortedByDescending { it.createdAtMillis }
        } catch (_: Exception) {
            _clips.value = emptyList()
        }
    }

    private fun persistClips(list: List<SavedClip>) {
        val jsonArray = JSONArray()
        list.forEach { clip ->
            val obj = JSONObject().apply {
                put("id", clip.id)
                put("title", clip.title)
                put("text", clip.text)
                put("language", clip.language.name)
                put("voiceName", clip.voiceName)
                put("speechRate", clip.speechRate.toDouble())
                put("pitch", clip.pitch.toDouble())
                put("filePath", clip.filePath)
                put("createdAtMillis", clip.createdAtMillis)
                put("durationSeconds", clip.durationSeconds.toDouble())
                put("format", clip.format)
                put("persona", clip.persona)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(keyClips, jsonArray.toString()).apply()
        _clips.value = list
    }

    fun addClip(clip: SavedClip) {
        val updated = listOf(clip) + _clips.value.filter { it.id != clip.id }
        persistClips(updated)
    }

    fun deleteClip(clipId: String) {
        val clip = _clips.value.find { it.id == clipId }
        clip?.let {
            try {
                val file = File(it.filePath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (_: Exception) { }
        }
        val updated = _clips.value.filter { it.id != clipId }
        persistClips(updated)
    }

    fun createAudioOutputFile(prefix: String = "desi_voice", extension: String = "mp3"): File {
        val dir = File(context.filesDir, "saved_voices")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, "${prefix}_${System.currentTimeMillis()}.$extension")
    }

    fun shareAudioClip(clip: SavedClip) {
        val file = File(clip.filePath)
        if (!file.exists()) return

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val mimeType = if (clip.filePath.endsWith(".mp3", ignoreCase = true)) "audio/mp3" else "audio/wav"

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "${clip.title} (Text to Voice ${clip.language.displayName})")
            putExtra(
                Intent.EXTRA_TEXT,
                "${clip.title}\n\n\"${clip.text}\"\n\n— Generated with Text to Voice [${clip.language.displayName} • ${clip.format} Audio]"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, "Share MP3 Audio Clip via...").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
