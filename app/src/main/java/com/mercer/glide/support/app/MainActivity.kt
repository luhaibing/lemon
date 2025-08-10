package com.mercer.glide.support.app

import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mercer.glide.support.app.databinding.ActivityMainBinding
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAImageView
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.apply {
            val radius = resources.getDimension(R.dimen.radius)
            Glide.with(iv1)
                .`as`(SVGADrawable::class.java)
                .load(Data.SVGA)
                // .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(LoadingDrawable(iv1, getColor(R.color.color_EEEEEE), radius))
                .error(R.drawable.loaded_failure_placeholder)
                .into(
                    SimpleImageViewTarget<SVGAImageView, SVGADrawable>(
                        iv1, "svga", {
                            startAnimation()
                        })
                )
            Glide.with(iv2)
                .`as`(LottieDrawable::class.java)
                .load(Data.LOTTIE)
                // .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(LoadingDrawable(iv2, getColor(R.color.color_EEEEEE), radius))
                .error(R.drawable.loaded_failure_placeholder)
                .into(
                    SimpleImageViewTarget<LottieAnimationView, LottieDrawable>(
                        iv2, "lottie", {
                            playAnimation()
                        })
                )
            Glide.with(iv3)
                .`as`(PictureDrawable::class.java)
                .load(Data.SVG)
                // .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(LoadingDrawable(iv3, getColor(R.color.color_EEEEEE), radius))
                .error(R.drawable.loaded_failure_placeholder)
                .into(SimpleImageViewTarget<ImageView, PictureDrawable>(iv3, "svg"))

            Glide.with(iv4)
                .load(Data.PNG)
                .placeholder(LoadingDrawable(iv4, getColor(R.color.color_EEEEEE), radius))
                .error(R.drawable.loaded_failure_placeholder)
                .into(SimpleImageViewTarget<ImageView, Drawable>(iv4, "png"))
        }
    }

}