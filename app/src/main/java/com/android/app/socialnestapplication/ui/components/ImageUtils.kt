package com.android.app.socialnestapplication.ui.components

import android.util.Base64

fun parseImageUrl(imageUrl: String?): Any? {
    if (imageUrl == null) return null
    return if (imageUrl.startsWith("data:image")) {
        val base64 = imageUrl.substringAfter(",")
        try {
            Base64.decode(base64, Base64.DEFAULT)
        } catch (e: Exception) {
            imageUrl
        }
    } else {
        imageUrl
    }
}
