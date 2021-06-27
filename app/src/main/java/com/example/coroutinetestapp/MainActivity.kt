package com.example.coroutinetestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.coroutinetestapp.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.getCancellationException
import java.lang.UnsupportedOperationException

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        runBlocking {
            val dispatcher = newSingleThreadContext(name = "ServiceCall")
            // async는 디퍼드 코루틴 프레임워크에서 제공하는 취소 불가능한 넌 블로킹 퓨처를 반환.
            val task = GlobalScope.async(dispatcher) { // launch로 하면, idea에서는 예외스택만 출력되고 중지는 안됨.
                // 하지만 안드로이드에서는 아예 중지됨.
                doSomething()
            }
            task.join()
            if (task.isCancelled) {
                val exception = task.getCancellationException()
                Log.d("KHJ", exception.cause.toString())
            } else {
                Log.d("KHJ", "success")
            }
        }
    }

    private fun doSomething() {
        throw UnsupportedOperationException("can't do")
    }

}