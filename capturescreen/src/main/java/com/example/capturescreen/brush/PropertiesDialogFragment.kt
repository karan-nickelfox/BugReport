package com.example.capturescreen.brush

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.capturescreen.R
import kotlinx.android.synthetic.main.fragment_bottom_properties_dialog.*

class PropertiesDialogFragment : BottomSheetDialogFragment(), SeekBar.OnSeekBarChangeListener, ColorPickerAdapter.InteractionListener {
    private lateinit var mListener: InteractionListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom_properties_dialog, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is InteractionListener) {
            mListener = context
        } else {
            throw RuntimeException("Must implement Properties Dialog Fragment Listener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sbOpacity.setOnSeekBarChangeListener(this)
        sbSize.setOnSeekBarChangeListener(this)

        rvColors.adapter = ColorPickerAdapter(context!!, this)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (seekBar?.id) {
            R.id.sbOpacity -> mListener.onOpacityChanged(progress)
            R.id.sbSize -> mListener.onBrushSizeChanged(progress)
        }
    }

    override fun onColorSelected(colorId: Int) = mListener.onColorChanged(colorId)

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    interface InteractionListener {
        fun onColorChanged(colorCode: Int)

        fun onOpacityChanged(opacity: Int)

        fun onBrushSizeChanged(brushSize: Int)
    }
}