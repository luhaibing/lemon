package com.mercer.glide.support.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.mercer.glide.support.app.databinding.ActivitySplashBinding
import kotlin.jvm.java

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.apply {
            btPreload.setOnClickListener {
                Glide.with(this@SplashActivity).load(Data.SVGA).preload()
                Glide.with(this@SplashActivity).load(Data.LOTTIE).preload()
                Glide.with(this@SplashActivity).load(Data.SVG).preload()
                Glide.with(this@SplashActivity).load(Data.PNG).preload()
                Glide.with(this@SplashActivity).load(Data.JPEG).preload()
            }
            btNext.setOnClickListener {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java).apply {
                    putExtra("k1",1753336179000)
                })
            }
        }
    }
}