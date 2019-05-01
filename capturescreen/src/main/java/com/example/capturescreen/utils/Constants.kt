package com.example.capturescreen.utils

import android.os.Environment

interface Constants {
    companion object {
        const val SCREENSHOT_FILE_NAME = "screenshot"
        const val STORAGE_REQUEST_CODE = 1001
        val FILE_PATH = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/" + System.currentTimeMillis().toString() + ".png"
        const val EXTRA_INPUT_TEXT = "extra_input_text"
        const val EXTRA_COLOR_CODE = "extra_color_code"
    }
}