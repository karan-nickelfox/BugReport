package com.example.capturescreen.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.View

fun Activity.takeScreenShot(): Bitmap? {
    return takeScreenShotForView(window.decorView.rootView)
}

private fun takeScreenShotForView(view: View): Bitmap? {
    view.isDrawingCacheEnabled = true
    view.buildDrawingCache(true)

    val bitmap = Bitmap.createBitmap(view.drawingCache)
    view.isDrawingCacheEnabled = false
    return bitmap
}

fun Activity.saveImage(bitmap: Bitmap): String? {
    val fileName = System.currentTimeMillis().toString() + ".png"
    try {
        val outStream = openFileOutput(fileName, Context.MODE_PRIVATE)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
        outStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return fileName
}