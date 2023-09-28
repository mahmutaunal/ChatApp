package com.mahmutalperenunal.chatapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.mahmutalperenunal.chatapp.R
import com.mahmutalperenunal.chatapp.databinding.ActivityViewFullImageBinding

class ViewFullImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewFullImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewFullImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val imageUrl = intent.getStringExtra("url")
        Glide.with(applicationContext).load(imageUrl).into(binding.viewFullImageImageView)
    }
}