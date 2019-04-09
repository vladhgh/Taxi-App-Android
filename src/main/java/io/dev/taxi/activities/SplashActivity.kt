package io.dev.taxi.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.orhanobut.hawk.Hawk

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Hawk.init(this).build()
        if (Hawk.get("isFirstStart") ?: true) {
            startActivity(Intent(this, IntroActivity::class.java))
            Hawk.put("isFirstStart", false)
            finish()
        } else {
            if (Hawk.contains("jsonToken")) {
                startActivity(Intent(this, TaxiActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}
