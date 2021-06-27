package com.example.coroutinetestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coroutinetestapp.databinding.ActivityMainBinding
import kotlinx.coroutines.newSingleThreadContext

class MainActivity : AppCompatActivity() {
    private val binding by lazy {ActivityMainBinding.inflate(layoutInflater)}
    val netDispatcher = newSingleThreadContext(name = "ServiceCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}