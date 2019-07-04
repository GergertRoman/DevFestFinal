package ru.grv.retrofittest

import android.os.Handler
import android.os.HandlerThread

class DbWorkerThread (threadName: String) : HandlerThread(threadName) {

    private lateinit var mWorkerHandler: Handler

    private var isLooperPrepared:Boolean = false

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        mWorkerHandler = Handler(looper)
        isLooperPrepared = true
    }

    fun postTask(task: Runnable) {
        if (looper != null && !isLooperPrepared){
            mWorkerHandler = Handler(looper)
        }
        mWorkerHandler.post(task)
        isLooperPrepared = false
    }

}