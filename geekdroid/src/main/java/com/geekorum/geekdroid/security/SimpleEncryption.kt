/**
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2020 by Frederic-Charles Barthelery.
 *
 * This file is part of Geekdroid.
 *
 * Geekdroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Geekdroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Geekdroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.geekorum.geekdroid.security

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.BLOCK_MODE_GCM
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE
import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import androidx.annotation.RequiresApi
import java.security.Key
import java.security.KeyStore
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.Cipher.getInstance
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

/**
 * Simplify usage of best practices for secret encryption on Android.
 */
@RequiresApi(Build.VERSION_CODES.M)
class SecretEncryption @Inject constructor() {
    companion object {
        const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
        const val CIPHER_TRANSFORMATION = "$KEY_ALGORITHM_AES/$BLOCK_MODE_GCM/$ENCRYPTION_PADDING_NONE"
    }

    private val keystore = KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER).apply { load(null) }

    fun getKey(alias: String) = keystore.getKey(alias, null)

    fun getOrCreateKey(alias: String): Key {
        return if (keystore.containsAlias(alias)) {
            getKey(alias)
        } else {
            val params = defaultKeyGenParameterSpec(alias)
            generateKey(KeyProperties.KEY_ALGORITHM_AES, params)
        }
    }

    fun hasKey(alias: String) = keystore.isKeyEntry(alias)

    fun deleteKey(alias: String) = keystore.deleteEntry(alias)

    private fun defaultKeyGenParameterSpec(keyAlias: String): KeyGenParameterSpec {
        return KeyGenParameterSpec.Builder(keyAlias, (PURPOSE_ENCRYPT or PURPOSE_DECRYPT))
            .setBlockModes(BLOCK_MODE_GCM)
            .setEncryptionPaddings(ENCRYPTION_PADDING_NONE)
            .build()
    }

    private fun generateKey(algorithm: String, params: AlgorithmParameterSpec): Key {
        val keyGenerator = KeyGenerator.getInstance(algorithm, ANDROID_KEYSTORE_PROVIDER)
        keyGenerator.init(params)
        return keyGenerator.generateKey()
    }

    fun getSecretCipher(key: Key): SecretCipher = SecretCipher(key)

    fun getSecretCipher(keyAlias: String): SecretCipher {
        val key = getOrCreateKey(keyAlias)
        return getSecretCipher(key)
    }
}

@RequiresApi(Build.VERSION_CODES.M)
class SecretCipher(
    private val key: Key
) {

    private val cipher = getInstance(SecretEncryption.CIPHER_TRANSFORMATION)

    fun encrypt(input: ByteArray): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(input)
    }

    val parametersSpec: GCMParameterSpec get() = cipher.parameters.getParameterSpec(GCMParameterSpec::class.java)

    fun decrypt(input: ByteArray, gcmParameterSpec: GCMParameterSpec): ByteArray {
        cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec)
        return cipher.doFinal(input)
    }
}
