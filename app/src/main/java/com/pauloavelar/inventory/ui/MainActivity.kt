package com.pauloavelar.inventory.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler().postDelayed({
            startActivity(Intent(this@MainActivity, ItemsActivity::class.java))
            finish()
        }, SPLASH_DELAY)
    }

    companion object {
        private val SPLASH_DELAY = TimeUnit.MILLISECONDS.toMillis(300)
    }

}
