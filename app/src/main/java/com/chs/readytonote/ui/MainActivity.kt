package com.chs.readytonote.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chs.readytonote.NoteRepository
import com.chs.readytonote.R
import com.chs.readytonote.adapter.NoteAdapter
import com.chs.readytonote.dao.NoteDao
import com.chs.readytonote.database.NotesDatabases
import com.chs.readytonote.entities.Note
import com.chs.readytonote.viewmodel.MainViewModel
import com.chs.readytonote.viewmodel.MainViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    companion object{
        private const val REQUEST_CODE_ADD_NOTE:Int = 1
        private const val REQUEST_CODE_UPDATE_NOTE:Int = 2
        private const val REQUST_CODE_SHOW_NOTE = 3
    }

    private lateinit var notesAdapter:NoteAdapter
    private lateinit var noteList:MutableList<Note>
    private lateinit var viewModel:MainViewModel
    private var noteClickPosition:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this,MainViewModelFactory(application))
            .get(MainViewModel::class.java)
        initRecyclerView()
        initClick()
    }

    private fun initClick(){
        imgAddNoteMain.setOnClickListener {
            startActivityForResult(Intent(this,
                CreateNoteActivity::class.java),REQUEST_CODE_ADD_NOTE)
        }
    }

    private fun getNote(requestCode: Int){
        Log.d("ReqCode",requestCode.toString())
        viewModel.getAllNotes().observe(this, Observer {
            when(requestCode) {
                REQUST_CODE_SHOW_NOTE -> {
                    noteList.addAll(it)
                    notesAdapter.notifyDataSetChanged()
                }
                REQUEST_CODE_ADD_NOTE -> {
                    noteList.add(0,it[0])
                    notesAdapter.notifyItemInserted(0)
                    Rv_notes.smoothScrollToPosition(0)
                }
                REQUEST_CODE_UPDATE_NOTE -> {
                    Log.d("noteClickPosition",noteClickPosition.toString())
                    noteList.removeAt(noteClickPosition)
                    noteList.add(noteClickPosition,it[noteClickPosition])
                    notesAdapter.notifyItemChanged(noteClickPosition)
                    Log.d("NoteTlqkfdk",noteList[noteClickPosition].imgPath)
                }
            }
        })
    }

    private fun initRecyclerView(){
        Rv_notes.apply {
            this.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            noteList = mutableListOf()
            notesAdapter = NoteAdapter(noteList){ note, position ->
                noteClickPosition = position
                val intent:Intent = Intent(this@MainActivity,CreateNoteActivity::class.java)
                intent.putExtra("isViewOrUpdate",true)
                intent.putExtra("note",note)
                startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE)
            }
            this.adapter = notesAdapter
            this.setHasFixedSize(true)
            getNote(REQUST_CODE_SHOW_NOTE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == Activity.RESULT_OK){
            getNote(REQUEST_CODE_ADD_NOTE)
        } else if(requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK){
            getNote(REQUEST_CODE_UPDATE_NOTE)
        }
    }
}