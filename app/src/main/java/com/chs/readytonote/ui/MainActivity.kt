package com.chs.readytonote.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chs.readytonote.R
import com.chs.readytonote.adapter.NoteAdapter
import com.chs.readytonote.dto.Note
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var notesAdapter:NoteAdapter
    private var lstDummy = ArrayList<Note>(10)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDummy()
        initRecyclerView()
        initClick()
    }

    private fun initClick(){
        imgAddNoteMain.setOnClickListener {
            startActivity(Intent(this,
                CreateNoteActivity::class.java))
        }
    }

    private fun initRecyclerView(){
        Rv_notes.apply {
            this.setHasFixedSize(true)
            this.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            this.adapter = notesAdapter
        }
    }

    private fun initDummy(){
        lstDummy.add(Note("장보기","우유,에센뽀득,드빈치 치즈,올리고당,클라우드 드래프트 한박스","2020 7월 14 19:07"))
        lstDummy.add(Note("Test2","테스트입니다2\n 아이고난!","2020 7월 16 19:52"))
        lstDummy.add(Note("Test1","테스트입니다3\n 아이고난!","1996 8월 29 19:07"))
        lstDummy.add(Note("Test2","테스트입니다4","2020 7월 16 19:52"))
        notesAdapter = NoteAdapter(lstDummy)
        notesAdapter.notifyDataSetChanged()
    }
}