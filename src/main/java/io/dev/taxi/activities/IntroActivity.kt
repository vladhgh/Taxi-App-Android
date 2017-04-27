package io.dev.taxi.activities

import android.os.Bundle
import android.os.PersistableBundle
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntro2
import android.content.Intent
import android.graphics.Color
import io.dev.taxi.R
import io.dev.taxi.slides.FinalSlide
import io.dev.taxi.slides.SampleSlide
import io.dev.taxi.slides.SecondSlide
import io.dev.taxi.slides.WalletSlide


class IntroActivity: AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSeparatorColor(resources.getColor(R.color.yellow50))
        setSkipText(resources.getText(R.string.intro_skip))
        setDoneText(resources.getText(R.string.intro_done))
    }

    override fun init(savedInstanceState: Bundle?) {
        addSlide(SampleSlide())
        addSlide(SecondSlide())
        addSlide(WalletSlide())
        addSlide(FinalSlide())

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
