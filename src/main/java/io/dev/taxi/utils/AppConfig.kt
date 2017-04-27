package io.dev.taxi.utils

import okhttp3.OkHttpClient

class AppConfig {
    companion object {
        val CLIENT = OkHttpClient.Builder().build()
        val BASE_URL = "http://taxi-api.eu-1.evennode.com/api/"
        val TAG_PREFIX = "DEV"
    }
}
