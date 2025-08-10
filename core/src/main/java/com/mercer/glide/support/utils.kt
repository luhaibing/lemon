package com.mercer.glide.support

import java.math.BigInteger
import java.security.MessageDigest
import kotlin.text.padStart
import kotlin.text.toByteArray

/**
 * @author      Mercer
 * @Created     2025/08/04.
 * @Description:
 *
 */
internal val String.md5: String
    get() {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }

