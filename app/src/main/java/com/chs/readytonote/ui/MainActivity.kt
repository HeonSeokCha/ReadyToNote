package com.chs.readytonote.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chs.readytonote.R
import com.chs.readytonote.adapter.NoteAdapter
import com.chs.readytonote.entities.Note
import com.chs.readytonote.viewmodel.MainViewModel
import com.chs.readytonote.viewmodel.MainViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {
    companion object{
        private const val REQUEST_CODE_ADD_NOTE = 1
        private const val REQUEST_CODE_UPDATE_NOTE = 2
        private const val REQUST_CODE_SHOW_NOTE = 3
    }

    private lateinit var notesAdapter: NoteAdapter
    private lateinit var noteList: MutableList<Note>
    private lateinit var viewModel: MainViewModel
    private var noteClickPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this,MainViewModelFactory(application))
            .get(MainViewModel::class.java)
        initRecyclerView()
        initClick()
        searchNote()
    }

    private fun initClick() {
        imgAddNoteMain.setOnClickListener {
            startActivityForResult(Intent(this,
                CreateNoteActivity::class.java),REQUEST_CODE_ADD_NOTE)
        }
    }

    private fun getNote(requestCode: Int,isNoteDelete:Boolean) {
        viewModel.getAllNotes().observe(this, Observer {
            when (requestCode) {
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
                    noteList.removeAt(noteClickPosition)
                    if(isNoteDelete){
                        notesAdapter.notifyItemRemoved(noteClickPosition)
                    } else{
                        noteList.add(noteClickPosition,it[noteClickPosition])
                        notesAdapter.notifyItemChanged(noteClickPosition)
                    }
                }
            }
        })
    }

    private fun initRecyclerView() {
        Rv_notes.apply {
            this.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            noteList = mutableListOf()
            notesAdapter = NoteAdapter(noteList){ note, position ->
                noteClickPosition = position
                val intent = Intent(this@MainActivity,CreateNoteActivity::class.java)
                intent.putExtra("isViewOrUpdate",true)
                intent.putExtra("note",note)
                startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE)
            }
            this.adapter = notesAdapter
            this.setHasFixedSize(true)
        }
        getNote(REQUST_CODE_SHOW_NOTE,false)
    }

    private fun searchNote() {
        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(noteList.isNotEmpty()){
                    notesAdapter.search(p0.toString())
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                notesAdapter.cancelTimer()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == Activity.RESULT_OK){
            getNote(REQUEST_CODE_ADD_NOTE,false)
        } else if(requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK){
            if(data!=null){
                getNote(REQUEST_CODE_UPDATE_NOTE,data!!.getBooleanExtra("isNoteDelete",false))
            }
        }
    }
}