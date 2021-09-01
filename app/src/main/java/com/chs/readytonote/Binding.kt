package com.chs.readytonote

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.load

object Binding {
    @BindingAdapter("imageSrc")
    @JvmStatic
    fun loadImage(imageView: ImageView, path: String) {
        imageView.load(path) {
            crossfade(true)
            crossfade(400)
            error(R.drawable.ic_delete)
        }
    }

    @BindingAdapter("dateSplit")
    @JvmStatic
    fun setDate(textView: TextView, date: String) {
        textView.text = date.split("년 ")[1]
    }

    @BindingAdapter("setCardColor")
    @JvmStatic
    fun setCardColor(cardView: CardView, color: String) {
        if (color == "#333333") {
            when (cardView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    cardView.setCardBackgroundColor(Color.parseColor("#ECECEC"))
                }
                Configuration.UI_MODE_NIGHT_YES -> {
                    cardView.setCardBackgroundColor(Color.parseColor("#333333"))
                }
            }
        } else {
            cardView.setCardBackgroundColor(Color.parseColor(color))
        }
    }

    @SuppressLint("ResourceAsColor")
    @BindingAdapter("textColor")
    @JvmStatic
    fun setTextColor(textView: TextView, color: String) {
        if (color == "#FDBE3B") {
            textView.setTextColor(
                ContextCompat.getColor(textView.context, R.color.colorBlack)
            )
        } else {
            textView.setTextColor(
                ContextCompat.getColor(textView.context, R.color.colorText)
            )
        }
    }

    @SuppressLint("ResourceAsColor")
    @BindingAdapter("labelColor")
    @JvmStatic
    fun setLabelColor(textView: TextView, color: String) {
        if (color == "#FDBE3B") {
            textView.setBackgroundResource(R.drawable.background_note_label_black)
        } else {
            textView.setBackgroundResource(R.drawable.background_note_label)
        }
    }
}

