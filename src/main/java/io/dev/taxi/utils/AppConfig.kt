package io.dev.taxi.utils

import okhttp3.OkHttpClient

class AppConfig {
    companion object {
        val CLIENT = OkHttpClient.Builder().build()
        val BASE_URL = "http://192.168.1.183:8080/api/"
        val TAG_PREFIX = "DEV"
    }
}
