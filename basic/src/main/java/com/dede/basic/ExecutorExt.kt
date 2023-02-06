package com.dede.basic

import android.os.Handler
import android.os.Looper
import androidx.core.os.ExecutorCompat

/**
 * Created by shhu on 2023/2/2 16:44.
 *
 * @author shhu
 * @since 2023/2/2
 */

val uiHandler = Handler(Looper.getMainLooper())

val uiExecutor = ExecutorCompat.create(uiHandler)