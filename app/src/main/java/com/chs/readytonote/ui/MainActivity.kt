package com.chs.readytonote.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chs.readytonote.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initClikc()
    }

    private fun initClikc(){
        imgAddNoteMain.setOnClickListener {
            startActivity(Intent(this,
                CreateNoteActivity::class.java))
        }
    }
}