package com.chs.readytonote

import android.annotation.SuppressLint
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.chs.readytonote.viewmodel.GlideApp

object Binding {
    @BindingAdapter("imageSrc")
    @JvmStatic
    fun loadImage(imageView: ImageView,path: String) {
        GlideApp.with(imageView.context).load(path)
            .placeholder(R.color.colorNoteDefaultColor)
            .error(R.drawable.ic_delete)
            .into(imageView)
    }

    @BindingAdapter("cardColor")
    @JvmStatic
    fun loadCardColor(cardView: CardView,color: String) {
        cardView.setCardBackgroundColor(
            Color.parseColor(color))
    }

    @BindingAdapter("dateSplit")
    @JvmStatic
    fun setDate(textView: TextView, date: String) {
        textView.text = date.split("년 ")[1]
    }

    @SuppressLint("ResourceAsColor")
    @BindingAdapter("textColor")
    @JvmStatic
    fun setTextColor(textView: TextView, color: String) {
        if(color == "#FDBE3B") {
            textView.setTextColor(
                ContextCompat.getColor(textView.context,R.color.colorWhite))
        } else {
            textView.setTextColor(
                ContextCompat.getColor(textView.context,R.color.colorText))
        }
    }
}