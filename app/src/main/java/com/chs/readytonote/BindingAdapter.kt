package com.chs.readytonote

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

object BindingAdapter {
    @BindingAdapter("createNoteImageView")
    @JvmStatic
    fun createNoteImageView(imageView: ImageView, path: String) {
        imageView.load(path)
    }
}