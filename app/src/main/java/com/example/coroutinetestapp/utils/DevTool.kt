package com.example.coroutinetestapp.utils

import android.util.Log

object DevTool {
    fun logD(msg: String) {
        Log.d("KHJ", msg)
    }

    fun logE(msg: String) {
        Log.e("KHJ", msg)
    }

    fun printCurrentThread(tag: String = "default") {
        println("Running in tag:$tag ${Thread.currentThread().name}")
    }
    fun logCurrentThread(tag: String = "default") {
        logD("Running in tag:$tag Thread[${Thread.currentThread().name}]")
    }
}