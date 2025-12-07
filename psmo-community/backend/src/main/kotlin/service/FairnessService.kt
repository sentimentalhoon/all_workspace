package com.psmo.service

import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.UUID
import kotlin.math.abs

/**
 * 시드 생성/결합, 해시 공개, RNG 팩토리를 담당하는 공정성 유틸.
 */
class FairnessService(
    private val hashAlgorithm: String = "SHA-256"
) {
    fun generateServerSeed(): String = UUID.randomUUID().toString()

    fun hashSeed(seed: String): String {
        val digest = MessageDigest.getInstance(hashAlgorithm).digest(seed.toByteArray())
        return "${hashAlgorithm.lowercase()}:" + digest.joinToString("") { "%02x".format(it) }
    }

    fun combineSeeds(serverSeed: String, clientSeed: String?): String =
        if (clientSeed.isNullOrBlank()) serverSeed else "$serverSeed:$clientSeed"

    fun rng(seed: String): () -> Double {
        val hashed = MessageDigest.getInstance("SHA-256").digest(seed.toByteArray())
        val seedInt = ByteBuffer.wrap(hashed).int
        return mulberry32(seedInt)
    }

    private fun mulberry32(seed: Int): () -> Double {
        var a = seed
        return {
            var t = a + 0x6D2B79F5
            t = t xor (t ushr 15)
            t = t * (t or 1)
            t = t xor (t + ((t xor (t ushr 7)) * (t or 61)))
            a = t
            abs((t xor (t ushr 14)) / 4294967296.0)
        }
    }
}
