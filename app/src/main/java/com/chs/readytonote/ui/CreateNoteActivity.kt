package com.chs.readytonote.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chs.readytonote.R
import kotlinx.android.synthetic.main.activity_create_note.*
import java.text.SimpleDateFormat
import java.util.*

class CreateNoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)
        initView()
        initClick()
    }

    private fun initClick(){
        imgBack.setOnClickListener {
            finish()
        }
    }

    private fun initView(){
        txtDateTime.text = SimpleDateFormat("yyyy MMMM dd HH:MM", Locale.getDefault()).format(Date())
    }
}