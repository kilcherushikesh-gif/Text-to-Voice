package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class IndianSoundEffect(
    val id: String,
    val title: String,
    val hindiTitle: String,
    val marathiTitle: String,
    val emoji: String,
    val description: String,
    val isLoopable: Boolean
) {
    TEMPLE_BELL(
        id = "bell",
        title = "Temple Bell",
        hindiTitle = "मंदिर की घंटी",
        marathiTitle = "मंदिराची घंटी",
        emoji = "🔔",
        description = "Resonant copper temple bell chime with lingering harmonic echo",
        isLoopable = false
    ),
    SHANKH(
        id = "shankh",
        title = "Holy Shankh",
        hindiTitle = "शंखनाद",
        marathiTitle = "शंखनाद",
        emoji = "🐚",
        description = "Spiritual deep conch shell invocation vibration",
        isLoopable = false
    ),
    TANPURA(
        id = "tanpura",
        title = "Tanpura Drone",
        hindiTitle = "तानपुरा सुर",
        marathiTitle = "तानपुरा सुर",
        emoji = "🪕",
        description = "Continuous meditative classical Indian string drone (Sa-Pa-Sa)",
        isLoopable = true
    ),
    DHOLAK_BEAT(
        id = "dholak",
        title = "Dholak / Tabla",
        hindiTitle = "ढोलक व तबला थाप",
        marathiTitle = "ढोलक व तबला ताल",
        emoji = "🥁",
        description = "Rhythmic Indian folk percussive bass & snap beats",
        isLoopable = true
    ),
    OM_RESONANCE(
        id = "om",
        title = "Om Vibration",
        hindiTitle = "ॐ ओंकार ध्यान",
        marathiTitle = "ॐ ओंकार नाद",
        emoji = "🕉️",
        description = "Sacred 136.1 Hz meditative calming frequency",
        isLoopable = false
    )
}

class IndianSoundSynthesizer(
    private val scope: CoroutineScope
) {
    private val sampleRate = 44100

    private val _currentlyPlayingId = MutableStateFlow<String?>(null)
    val currentlyPlayingId: StateFlow<String?> = _currentlyPlayingId.asStateFlow()

    private val _tanpuraAmbianceEnabled = MutableStateFlow(false)
    val tanpuraAmbianceEnabled: StateFlow<Boolean> = _tanpuraAmbianceEnabled.asStateFlow()

    private var activeJob: Job? = null
    private var activeAudioTrack: AudioTrack? = null

    private var backgroundDroneJob: Job? = null
    private var backgroundDroneTrack: AudioTrack? = null

    fun playSound(effect: IndianSoundEffect) {
        if (_currentlyPlayingId.value == effect.id) {
            stopSound()
            return
        }

        stopSound()
        _currentlyPlayingId.value = effect.id

        activeJob = scope.launch(Dispatchers.Default) {
            try {
                when (effect) {
                    IndianSoundEffect.TEMPLE_BELL -> playTempleBell()
                    IndianSoundEffect.SHANKH -> playShankh()
                    IndianSoundEffect.TANPURA -> playTanpuraLoop()
                    IndianSoundEffect.DHOLAK_BEAT -> playDholakLoop()
                    IndianSoundEffect.OM_RESONANCE -> playOmResonance()
                }
            } finally {
                if (_currentlyPlayingId.value == effect.id) {
                    _currentlyPlayingId.value = null
                }
            }
        }
    }

    fun stopSound() {
        activeJob?.cancel()
        activeJob = null
        try {
            activeAudioTrack?.pause()
            activeAudioTrack?.flush()
            activeAudioTrack?.release()
        } catch (_: Exception) { }
        activeAudioTrack = null
        _currentlyPlayingId.value = null
    }

    fun setTanpuraAmbiance(enabled: Boolean) {
        _tanpuraAmbianceEnabled.value = enabled
        if (enabled) {
            startBackgroundDrone()
        } else {
            stopBackgroundDrone()
        }
    }

    private fun startBackgroundDrone() {
        stopBackgroundDrone()
        backgroundDroneJob = scope.launch(Dispatchers.Default) {
            try {
                val durationSeconds = 4.0
                val numSamples = (sampleRate * durationSeconds).toInt()
                val samples = ShortArray(numSamples)

                // Tanpura Sa-Pa-Sa gentle drone with 4 string plucks staggered
                val strings = listOf(
                    146.83 to 0.0,  // Low Sa
                    220.00 to 1.0,  // Pa (Fifth)
                    293.66 to 2.0,  // Middle Sa
                    146.83 to 3.0   // Bass Sa
                )

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    var total = 0.0

                    for ((freq, offset) in strings) {
                        val relT = (t + offset) % durationSeconds
                        val envelope = exp(-relT * 0.9) * (0.8 + 0.2 * sin(2 * PI * 0.5 * relT))
                        val tone = sin(2 * PI * freq * t) +
                                0.45 * sin(2 * PI * freq * 2 * t) +
                                0.25 * sin(2 * PI * freq * 3 * t) +
                                0.12 * sin(2 * PI * freq * 4 * t)
                        total += tone * envelope
                    }

                    val sampleVal = (total * 3500.0).coerceIn(-32767.0, 32767.0).toInt().toShort()
                    samples[i] = sampleVal
                }

                val bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                ).coerceAtLeast(samples.size * 2)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                backgroundDroneTrack = track
                track.play()

                while (isActive) {
                    track.write(samples, 0, samples.size)
                }
            } catch (_: Exception) {
            } finally {
                try {
                    backgroundDroneTrack?.pause()
                    backgroundDroneTrack?.release()
                } catch (_: Exception) { }
                backgroundDroneTrack = null
            }
        }
    }

    private fun stopBackgroundDrone() {
        backgroundDroneJob?.cancel()
        backgroundDroneJob = null
        try {
            backgroundDroneTrack?.pause()
            backgroundDroneTrack?.release()
        } catch (_: Exception) { }
        backgroundDroneTrack = null
    }

    private fun playTempleBell() {
        val durationSeconds = 3.2
        val numSamples = (sampleRate * durationSeconds).toInt()
        val samples = ShortArray(numSamples)

        val baseFreq = 2100.0
        val overtones = listOf(
            2100.0 to 1.0,
            4200.0 to 0.55,
            6300.0 to 0.35,
            1050.0 to 0.25
        )

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t * 1.8)
            var sample = 0.0

            for ((freq, weight) in overtones) {
                // Slight vibrato / harmonic resonance
                val vibrato = sin(2 * PI * 6.0 * t) * 4.0
                sample += weight * sin(2 * PI * (freq + vibrato) * t) * decay
            }

            // Initial attack chime
            if (t < 0.05) {
                sample += (1.0 - t / 0.05) * 0.4 * sin(2 * PI * 3500.0 * t)
            }

            samples[i] = (sample * 18000.0).coerceIn(-32767.0, 32767.0).toInt().toShort()
        }

        playRawPcm(samples)
    }

    private fun playShankh() {
        val durationSeconds = 3.6
        val numSamples = (sampleRate * durationSeconds).toInt()
        val samples = ShortArray(numSamples)

        val fundamental = 392.0 // G4 spiritual conch fundamental
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate

            // Wind attack & gentle sustain envelope
            val env = when {
                t < 0.4 -> (t / 0.4)
                t < 2.6 -> 1.0
                else -> exp(-(t - 2.6) * 3.0)
            }

            // Natural lip flutter / breath turbulence
            val flutter = 1.0 + 0.04 * sin(2 * PI * 7.5 * t)
            val pitchBend = fundamental * (1.0 + 0.03 * sin(PI * t / 3.0))

            val sample = env * (
                sin(2 * PI * pitchBend * t * flutter) +
                0.65 * sin(2 * PI * (pitchBend * 2) * t) +
                0.40 * sin(2 * PI * (pitchBend * 3) * t) +
                0.20 * sin(2 * PI * (pitchBend * 4) * t)
            )

            samples[i] = (sample * 14000.0).coerceIn(-32767.0, 32767.0).toInt().toShort()
        }

        playRawPcm(samples)
    }

    private suspend fun playTanpuraLoop() {
        val durationSeconds = 3.5
        val numSamples = (sampleRate * durationSeconds).toInt()
        val samples = ShortArray(numSamples)

        val notes = listOf(146.83, 220.00, 293.66, 146.83)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            var sum = 0.0

            for (idx in notes.indices) {
                val noteTime = (t - idx * 0.85).coerceAtLeast(0.0)
                if (t >= idx * 0.85) {
                    val decay = exp(-noteTime * 1.2)
                    val freq = notes[idx]
                    val wave = sin(2 * PI * freq * t) +
                            0.5 * sin(2 * PI * freq * 2 * t) +
                            0.3 * sin(2 * PI * freq * 3 * t)
                    sum += wave * decay
                }
            }

            samples[i] = (sum * 8000.0).coerceIn(-32767.0, 32767.0).toInt().toShort()
        }

        playStreamUntilCancelled(samples, loops = 5)
    }

    private suspend fun playDholakLoop() {
        val beatDuration = 0.6
        val numSamples = (sampleRate * beatDuration).toInt()
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            var sample = 0.0

            // Bass thump ("Ghe") in first half
            if (t < 0.3) {
                val bassFreq = 120.0 * exp(-t * 12.0) + 55.0
                val bassEnv = exp(-t * 9.0)
                sample += sin(2 * PI * bassFreq * t) * bassEnv * 1.4
            }

            // Treble slap ("Na / Ta") at 0.3s
            if (t >= 0.3) {
                val slapT = t - 0.3
                val slapFreq = 620.0 * exp(-slapT * 20.0) + 240.0
                val slapEnv = exp(-slapT * 18.0)
                sample += sin(2 * PI * slapFreq * slapT) * slapEnv * 1.1
            }

            samples[i] = (sample * 16000.0).coerceIn(-32767.0, 32767.0).toInt().toShort()
        }

        playStreamUntilCancelled(samples, loops = 12)
    }

    private fun playOmResonance() {
        val durationSeconds = 4.2
        val numSamples = (sampleRate * durationSeconds).toInt()
        val samples = ShortArray(numSamples)

        val omFreq = 136.1 // Planetary Earth year Om tone

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val env = when {
                t < 0.6 -> (t / 0.6)
                t < 3.2 -> 1.0
                else -> exp(-(t - 3.2) * 2.5)
            }

            val sample = env * (
                sin(2 * PI * omFreq * t) +
                0.55 * sin(2 * PI * omFreq * 2 * t) +
                0.25 * sin(2 * PI * omFreq * 3 * t) +
                0.15 * sin(2 * PI * omFreq * 4 * t)
            )

            samples[i] = (sample * 16000.0).coerceIn(-32767.0, 32767.0).toInt().toShort()
        }

        playRawPcm(samples)
    }

    private fun playRawPcm(samples: ShortArray) {
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(samples.size * 2)

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        activeAudioTrack = track
        track.play()
        track.write(samples, 0, samples.size)
        // Let it finish playing before releasing
        val playMillis = (samples.size.toDouble() / sampleRate * 1000).toLong()
        Thread.sleep(playMillis)
        try {
            track.stop()
            track.release()
        } catch (_: Exception) { }
    }

    private suspend fun playStreamUntilCancelled(samples: ShortArray, loops: Int) {
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(samples.size * 2)

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        activeAudioTrack = track
        track.play()

        var count = 0
        while (count < loops) {
            track.write(samples, 0, samples.size)
            count++
            delay(50)
        }

        try {
            track.stop()
            track.release()
        } catch (_: Exception) { }
    }

    fun release() {
        stopSound()
        stopBackgroundDrone()
    }
}
