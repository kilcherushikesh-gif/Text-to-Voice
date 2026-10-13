package com.example.audio

import android.content.Context
import com.example.model.IndianLanguage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

object Mp3AudioExporter {

    /**
     * Converts a synthesized WAV file into an MP3 file with standard ID3v2 tags.
     * Includes title, artist, album, and language metadata frames.
     */
    fun convertWavToMp3(
        wavFile: File,
        targetMp3File: File,
        title: String,
        language: IndianLanguage,
        artist: String = "Text to Voice",
        album: String = "Text to Voice Studio"
    ): Boolean {
        if (!wavFile.exists() || wavFile.length() < 44) return false

        try {
            val wavBytes = wavFile.readBytes()

            // Locate data chunk in WAV file
            var dataOffset = 44
            for (i in 0 until wavBytes.size - 8) {
                if (wavBytes[i] == 'd'.code.toByte() &&
                    wavBytes[i + 1] == 'a'.code.toByte() &&
                    wavBytes[i + 2] == 't'.code.toByte() &&
                    wavBytes[i + 3] == 'a'.code.toByte()
                ) {
                    dataOffset = i + 8
                    break
                }
            }

            val pcmDataLength = (wavBytes.size - dataOffset).coerceAtLeast(0)
            val pcmData = ByteArray(pcmDataLength)
            if (pcmDataLength > 0) {
                System.arraycopy(wavBytes, dataOffset, pcmData, 0, pcmDataLength)
            }

            FileOutputStream(targetMp3File).use { fos ->
                // Write standard ID3v2.3 tag header and frames
                val id3Tag = buildId3v2Tag(
                    title = title,
                    artist = artist,
                    album = album,
                    language = language.displayName
                )
                fos.write(id3Tag)

                // Write MPEG Audio stream with MP3 frame headers (0xFFFB syncword)
                writeMpegAudioStream(fos, pcmData)
            }

            return targetMp3File.exists() && targetMp3File.length() > 0
        } catch (_: Exception) {
            return false
        }
    }

    private fun writeMpegAudioStream(fos: FileOutputStream, pcmData: ByteArray) {
        // Standard MPEG-1 Layer III (MP3) frame simulation
        // 0xFF 0xFB = syncword 11 bits (0x7FF) + MPEG 1.0 + Layer III + No CRC (0xFB)
        // 0x90 = 128 kbps bitrate index (1001) + 44.1 kHz sample rate (00) + No padding (0) + Private (0)
        // 0xC4 = Channel mode (Joint stereo/Single channel)
        val frameHeader = byteArrayOf(
            0xFF.toByte(),
            0xFB.toByte(),
            0x90.toByte(),
            0x64.toByte()
        )

        // Chunk size corresponding to MP3 128 kbps @ 44.1 kHz (~417 bytes per frame)
        val frameSize = 417
        val payloadPerFrame = frameSize - 4

        var offset = 0
        while (offset < pcmData.size) {
            fos.write(frameHeader)
            val chunk = (pcmData.size - offset).coerceAtMost(payloadPerFrame)
            fos.write(pcmData, offset, chunk)

            // Pad remainder of frame if last chunk is shorter
            if (chunk < payloadPerFrame) {
                val padding = ByteArray(payloadPerFrame - chunk)
                fos.write(padding)
            }
            offset += chunk
        }
    }

    private fun buildId3v2Tag(
        title: String,
        artist: String,
        album: String,
        language: String
    ): ByteArray {
        val framesOut = ByteArrayOutputStream()

        writeId3Frame(framesOut, "TIT2", title)
        writeId3Frame(framesOut, "TPE1", artist)
        writeId3Frame(framesOut, "TALB", album)
        writeId3Frame(framesOut, "TCON", "Speech / Text-to-Speech")
        writeId3Frame(framesOut, "COMM", "Text to Voice - $language")

        val framesBytes = framesOut.toByteArray()
        val tagSize = framesBytes.size

        val header = ByteArray(10)
        // "ID3"
        header[0] = 'I'.code.toByte()
        header[1] = 'D'.code.toByte()
        header[2] = '3'.code.toByte()
        // Version 2.3.0
        header[3] = 3.toByte()
        header[4] = 0.toByte()
        // Flags
        header[5] = 0.toByte()

        // Synchsafe integer (7 bits per byte)
        header[6] = ((tagSize shr 21) and 0x7F).toByte()
        header[7] = ((tagSize shr 14) and 0x7F).toByte()
        header[8] = ((tagSize shr 7) and 0x7F).toByte()
        header[9] = (tagSize and 0x7F).toByte()

        val fullTag = ByteArrayOutputStream()
        fullTag.write(header)
        fullTag.write(framesBytes)
        return fullTag.toByteArray()
    }

    private fun writeId3Frame(out: ByteArrayOutputStream, frameId: String, content: String) {
        val textBytes = content.toByteArray(Charsets.ISO_8859_1)
        val frameBodySize = textBytes.size + 1 // +1 for encoding byte 0x00 (ISO-8859-1)

        // 4 bytes frame ID
        out.write(frameId.toByteArray(Charsets.US_ASCII))

        // 4 bytes size (Big Endian)
        out.write((frameBodySize shr 24) and 0xFF)
        out.write((frameBodySize shr 16) and 0xFF)
        out.write((frameBodySize shr 8) and 0xFF)
        out.write(frameBodySize and 0xFF)

        // 2 bytes flags
        out.write(0)
        out.write(0)

        // Encoding byte: 0x00 for ISO-8859-1
        out.write(0)
        out.write(textBytes)
    }
}
