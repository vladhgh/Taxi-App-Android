package io.dev.taxi.activities

import android.os.Bundle
import android.os.PersistableBundle
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntro2
import android.content.Intent
import io.dev.taxi.R
import io.dev.taxi.slides.SampleSlide
import io.dev.taxi.slides.SecondSlide


class IntroActivity: AppIntro() {
    override fun init(savedInstanceState: Bundle?) {
        addSlide(SampleSlide())
        addSlide(SecondSlide())
    }

    private fun loadMainActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


    override fun onNextPressed() {
    }

    override fun onDonePressed() {
        loadMainActivity()
        finish()
    }

    override fun onSlideChanged() {
        // Do something here
    }

}
