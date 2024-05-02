package com.example.bicapplication.certify

import android.util.Base64
import com.google.android.gms.common.util.Base64Utils
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptImageActivity() {
    private lateinit var myIv: IvParameterSpec
    private lateinit var myKey: SecretKeySpec


    fun setIv(): String {
        var arr = ByteArray(16)
        var str = ""
        while(str.length != 16){
            SecureRandom().nextBytes(arr)
            str = String(arr)
        }
        return str
    }

    fun getIv(input: String): IvParameterSpec {
        var ivHash = MessageDigest.getInstance("SHA1")
            .digest(input.toByteArray())
        var ivBytes = Arrays.copyOf(ivHash, 16)
        var iv = IvParameterSpec(ivBytes)
        return iv
    }

    fun encrypt(input: String, key: ByteArray, iv: String): String {
        val myIv = getIv(iv)
        val keySpec = SecretKeySpec(key, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, myIv)
        val encrypt = cipher.doFinal(input.toByteArray())
        val enc = Base64.encode(encrypt, Base64.DEFAULT)
        return String(enc)
    }

    fun decrypt(input: String, key: ByteArray, iv: String): String {
//        var iv = getIv(input.substring(0, 16))
//        var cryptText = input.substring(16, input.length)
        val myIv = getIv(iv)
        val keySpec = SecretKeySpec(key, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, myIv)
        val decrypt = cipher.doFinal(Base64.decode(input, Base64.DEFAULT))
        return String(decrypt)
    }

}




