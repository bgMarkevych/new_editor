package com.example.zoomtest.editor.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

internal class MainThreadExecutor : Executor {

    private val handler = Handler(Looper.getMainLooper())

    override fun execute(command: Runnable) {
        handler.post(command)
    }

}

fun getMainThreadExecutor(): Executor {
    return MainThreadExecutor()
}

fun getSingleThreadExecutor(): Executor {
    return Executors.newSingleThreadExecutor()
}

fun getThreadExecutorsPool(poolSize: Int): Executor {
    return Executors.newFixedThreadPool(poolSize)
}

