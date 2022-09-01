package com.draw.drawlingandroid.ui.drawing

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.draw.drawlingandroid.databinding.ActivityDrawingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DrawingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawingBinding

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityDrawingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}