package com.example.capturescreen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import com.example.capturescreen.utils.Constants.Companion.SCREENSHOT_FILE_NAME
import com.example.capturescreen.utils.saveImage
import com.example.capturescreen.utils.takeScreenShot
import com.squareup.seismic.ShakeDetector

@SuppressLint("StaticFieldLeak")
object BugManager : ShakeDetector.Listener, Application.ActivityLifecycleCallbacks {
    private lateinit var application: Application
    private lateinit var currentActivity: Activity
    private lateinit var shakeDetector: ShakeDetector
    private lateinit var sensorManager: SensorManager
    private lateinit var alertDialog: AlertDialog

    fun init(application: Application) {
        this.application = application

        application.registerActivityLifecycleCallbacks(this)
        sensorManager = application.getSystemService(SENSOR_SERVICE) as SensorManager
        shakeDetector = ShakeDetector(this)
        shakeDetector.start(sensorManager)
    }

    private fun getAlertDialog(): AlertDialog {
        val shakeDialog = AlertDialog.Builder(currentActivity)
        shakeDialog.setMessage(R.string.shake_dialog_message)
        shakeDialog.setPositiveButton(R.string.yes) { _, _ ->
            val editIntent = Intent(currentActivity, EditActivity::class.java)
            val screenShot = currentActivity.takeScreenShot()
            editIntent.putExtra(SCREENSHOT_FILE_NAME, currentActivity.saveImage(screenShot!!))
            currentActivity.startActivity(editIntent)
        }
        return shakeDialog.create()
    }


    private fun isValidActivity(activity: Activity): Boolean {
        return currentActivity.localClassName == activity.localClassName
    }

    override fun hearShake() {
        if (!alertDialog.isShowing) {
            alertDialog.show()
            Log.e("Shake", "Shake detected !")
        }
    }

    override fun onActivityPaused(activity: Activity?) {
        // Do Nothing
    }

    override fun onActivityResumed(activity: Activity?) {
        currentActivity = activity!!
        alertDialog = getAlertDialog()
        if (activity is EditActivity) {
            shakeDetector.stop()
        } else {
            shakeDetector.start(sensorManager)
        }
        Log.e("Shake", activity.localClassName + " resumed !")
    }

    override fun onActivityStarted(activity: Activity?) {
        // Do Nothing
    }

    override fun onActivityDestroyed(activity: Activity?) {
        // Do Nothing
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        // Do Nothing
    }

    override fun onActivityStopped(activity: Activity?) {
        Log.e("Shake", activity!!.localClassName + " stopped !")
        if (isValidActivity(activity)) {
            shakeDetector.stop()
        }
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        // Do Nothing
    }
}