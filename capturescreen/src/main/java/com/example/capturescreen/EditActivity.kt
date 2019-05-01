package com.example.capturescreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.example.capturescreen.brush.PropertiesDialogFragment
import com.example.capturescreen.text.TextEditorDialogFragment
import com.example.capturescreen.utils.Constants.Companion.FILE_PATH
import com.example.capturescreen.utils.Constants.Companion.SCREENSHOT_FILE_NAME
import com.example.capturescreen.utils.Constants.Companion.STORAGE_REQUEST_CODE
import com.example.capturescreen.utils.saveImage
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.ViewType
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity(), PropertiesDialogFragment.InteractionListener,
        TextEditorDialogFragment.InteractionListener, OnPhotoEditorListener {
    private lateinit var bitmap: Bitmap
    private lateinit var photoEditor: PhotoEditor
    private lateinit var propertiesDialogFragment: PropertiesDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        init()

        photoEditor = PhotoEditor.Builder(this, editView).build()
        photoEditor.setOnPhotoEditorListener(this)
        propertiesDialogFragment = PropertiesDialogFragment()

        setupListeners()
    }

    private fun setupListeners() {
        closeBtn.setOnClickListener {
            onBackPressed()
        }
        undoBtn.setOnClickListener {
            photoEditor.undo()
        }

        redoBtn.setOnClickListener {
            photoEditor.redo()
        }

        brushBtn.setOnClickListener {
            photoEditor.setBrushDrawingMode(true)
            propertiesDialogFragment.show(supportFragmentManager, "")
        }

        saveBtn.setOnClickListener {
            if (hasStoragePermission()) {
                saveImageToStorage()
            }
        }

        sendBtn.setOnClickListener {
            photoEditor.saveAsBitmap(object : OnSaveBitmap {
                override fun onFailure(e: java.lang.Exception?) {
                    Toast.makeText(this@EditActivity, "Failed sending image", Toast.LENGTH_SHORT).show()
                }

                override fun onBitmapReady(bitmap: Bitmap?) {
                    val fileName = this@EditActivity.saveImage(bitmap!!)
                    val emailIntent = Intent(Intent.ACTION_SEND)
                    emailIntent.data = Uri.parse("mailto:")
                    emailIntent.type = "image/*"
                    val screenshotUri = this@EditActivity.getFileStreamPath(fileName!!).run {
                        FileProvider.getUriForFile(this@EditActivity, applicationContext.packageName, this)

                    }
                    emailIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
                    startActivity(emailIntent)
                }
            })
        }

        textBtn.setOnClickListener {
            TextEditorDialogFragment.show(this).apply {
                setOnEditListener(this@EditActivity)
            }
        }
    }

    override fun onColorChanged(colorCode: Int) {
        photoEditor.brushColor = colorCode
    }

    override fun onOpacityChanged(opacity: Int) {
        photoEditor.setOpacity(opacity)
    }

    override fun onBrushSizeChanged(brushSize: Int) {
        photoEditor.brushSize = brushSize.toFloat()
    }

    @SuppressLint("MissingPermission")
    private fun saveImageToStorage() {
        photoEditor.saveAsFile(FILE_PATH, object : PhotoEditor.OnSaveListener {
            override fun onSuccess(imagePath: String) {
                Toast.makeText(this@EditActivity, "Image saved in Pictures !", Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onFailure(exception: java.lang.Exception) {
                Toast.makeText(this@EditActivity, "Failed to save image, check logs", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            true
        } else {
            if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                requestStoragePermission()
                false
            }
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveImageToStorage()
        } else {
            Toast.makeText(this@EditActivity, "Failed to Save: Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDone(text: String, colorId: Int) {
        photoEditor.addText(text, colorId)
    }

    private fun init() {
        val fileName = intent.getStringExtra(SCREENSHOT_FILE_NAME)
        try {
            val inStream = openFileInput(fileName)
            bitmap = BitmapFactory.decodeStream(inStream)
            inStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val maxHeight = editView.height.toDouble()
        val resizeRatio = maxHeight / bitmap.height
        editView.layoutParams.width = (bitmap.width * resizeRatio).toInt()
        editView.layoutParams.height = maxHeight.toInt()
        editView.source.setImageBitmap(bitmap)
    }

    override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {
        TextEditorDialogFragment.show(this, text!!, colorCode)
                .setOnEditListener(object : TextEditorDialogFragment.InteractionListener {
                    override fun onDone(text: String, colorId: Int) {
                        photoEditor.editText(rootView, text, colorId)
                    }
                })
    }

    override fun onBackPressed() {
        if (photoEditor.isCacheEmpty) {
            super.onBackPressed()
        } else {
            showSaveDialog()
        }

    }

    private fun showSaveDialog() {
        val saveDialog = AlertDialog.Builder(this)
        saveDialog.setTitle(R.string.save_changes)
        saveDialog.setMessage(R.string.save_changes_message)
        saveDialog.setPositiveButton(R.string.yes) { _, _ ->
            if (hasStoragePermission()) {
                saveImageToStorage()
            } else {
                requestStoragePermission()
            }
        }
        saveDialog.setNegativeButton(R.string.discard) { _, _ ->
            super.onBackPressed()
        }
        saveDialog.setNeutralButton(R.string.cancel, null)
        saveDialog.show()
    }

    override fun onStartViewChangeListener(viewType: ViewType?) {}

    override fun onRemoveViewListener(numberOfAddedViews: Int) {}

    override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {}

    override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {}

    override fun onStopViewChangeListener(viewType: ViewType?) {}
}
