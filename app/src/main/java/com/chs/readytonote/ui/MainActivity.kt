package com.chs.readytonote.ui


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chs.readytonote.R
import com.chs.readytonote.adapter.NoteAdapter
import com.chs.readytonote.adapter.NoteDetailsLookup
import com.chs.readytonote.entities.Note
import com.chs.readytonote.viewmodel.MainViewModel
import com.chs.readytonote.viewmodel.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE_SHOW_NOTE = 1
    }

    private lateinit var notesAdapter: NoteAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var noteSelectionTracker: SelectionTracker<Long>
    private var noteClickPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this,MainViewModelFactory(application))
            .get(MainViewModel::class.java)
        initRecyclerView()
        initView()
        initClick()
        searchNote()
    }

    private fun initClick() {
        imgAddNoteMain.setOnClickListener {
            startActivityForResult(Intent(this,
                CreateNoteActivity::class.java),REQUEST_CODE_SHOW_NOTE)
        }
    }
    private fun initView(){
        bottomAppBar.replaceMenu(R.menu.create_note)

        bottomAppBar.setOnMenuItemClickListener {
            viewModel.allDelete()
            getNote()
            return@setOnMenuItemClickListener false
        }
    }

    private fun initRecyclerView() {
        Rv_notes.apply {
            notesAdapter = NoteAdapter(clickListener = { note, position ->
                noteClickPosition = position
                val intent = Intent(this@MainActivity, CreateNoteActivity::class.java)
                intent.putExtra("isViewOrUpdate", true)
                intent.putExtra("note", note)
                startActivityForResult(intent,REQUEST_CODE_SHOW_NOTE)
            })
            this.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            this.adapter = notesAdapter
            getNote()
            // still selectionTracker Problem..
            noteSelectionTracker = SelectionTracker.Builder<Long>(
                "note-content",
                Rv_notes,
                StableIdKeyProvider(Rv_notes),
                NoteDetailsLookup(Rv_notes),
                StorageStrategy.createLongStorage()
            ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
            ).build()
            notesAdapter.setTracker(noteSelectionTracker)

            noteSelectionTracker.addObserver(
                object : SelectionTracker.SelectionObserver<Long>() {
                    override fun onSelectionChanged() {
                        super.onSelectionChanged()
                        bottomAppBar.replaceMenu(R.menu.main_note)
                        Log.d("SelectionSize",noteSelectionTracker.selection.size().toString())
                    }
                }
            )
        }
    }

    private fun getNote() {
        viewModel.getAllNotes().observe(this, Observer { note ->
            if(note.isNotEmpty())
                notesAdapter.submitList(note)
        })
    }

    private fun searchNote() {
        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                notesAdapter.search(p0.toString())
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                notesAdapter.cancelTimer()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getNote()
    }

    override fun onBackPressed() {
        if(::noteSelectionTracker.isLateinit &&
                noteSelectionTracker.hasSelection()) {
            
        }
        super.onBackPressed()
    }
}