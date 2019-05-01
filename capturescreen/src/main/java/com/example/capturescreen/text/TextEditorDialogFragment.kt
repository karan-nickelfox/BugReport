package com.example.capturescreen.text

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.capturescreen.R
import com.example.capturescreen.brush.ColorPickerAdapter
import com.example.capturescreen.utils.Constants.Companion.EXTRA_COLOR_CODE
import com.example.capturescreen.utils.Constants.Companion.EXTRA_INPUT_TEXT
import kotlinx.android.synthetic.main.add_text_dialog.view.*

class TextEditorDialogFragment : DialogFragment() {
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var mListener: InteractionListener

    companion object {
        val TAG = "text_edit"

        fun show(appCompatActivity: AppCompatActivity,
                 inputText: String,
                 @ColorInt colorCode: Int): TextEditorDialogFragment {
            val args = Bundle()
            args.putString(EXTRA_INPUT_TEXT, inputText)
            args.putInt(EXTRA_COLOR_CODE, colorCode)
            val fragment = TextEditorDialogFragment()
            fragment.arguments = args
            fragment.show(appCompatActivity.supportFragmentManager, TAG)
            return fragment
        }

        fun show(appCompatActivity: AppCompatActivity): TextEditorDialogFragment {
            return show(appCompatActivity,
                    "", ContextCompat.getColor(appCompatActivity, R.color.white))
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is InteractionListener) {
            mListener = context
        } else {
            throw RuntimeException("must implement Interaction Listener")
        }
    }

    override fun onStart() {
        super.onStart()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_text_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var mColorCode = arguments?.getInt(EXTRA_COLOR_CODE)!!
        view.add_text_edit_text.setBackgroundColor(mColorCode)

        inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        view.add_text_color_picker_recycler_view.adapter = ColorPickerAdapter(context!!, object : ColorPickerAdapter.InteractionListener {
            override fun onColorSelected(colorId: Int) {
                mColorCode = colorId
                view.add_text_edit_text.setTextColor(colorId)
            }
        })

        view.add_text_done_tv.setOnClickListener {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            dismiss()
            val inputText = view.add_text_edit_text.text.toString()
            if (inputText.isNotBlank()) {
                mListener.onDone(inputText, mColorCode)
            }
        }
    }

    interface InteractionListener {
        fun onDone(text: String, colorId: Int)
    }
}