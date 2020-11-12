package com.chs.readytonote.ui


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chs.readytonote.R
import com.chs.readytonote.adapter.NoteAdapter
import com.chs.readytonote.entities.Note
import com.chs.readytonote.viewmodel.MainViewModel
import com.chs.readytonote.viewmodel.MainViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE_ADD_NOTE = 1
        private const val REQUEST_CODE_UPDATE_NOTE = 2
    }

    private lateinit var notesAdapter: NoteAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var checkList: MutableMap<Int,Note>
    private var editMode: Boolean = false
    private var noteClickPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this, MainViewModelFactory(application))
            .get(MainViewModel::class.java)
        initRecyclerView()
        initMenu()
        initClick()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initClick() {
        imgAddNoteMain.setOnClickListener {
            if(editMode && ::checkList.isInitialized) {
                for(i in checkList.values) {
                    viewModel.delete(i)
                }
                checkList.clear()
                editMode = false
                notesAdapter.editItemMode(false)
                imgAddNoteMain.setImageDrawable(
                        resources.getDrawable(R.drawable.ic_add, null)
                )
                bottomAppBar.replaceMenu(R.menu.main_note)
                getNote()
            } else {
                startActivityForResult(
                    Intent(this,
                        CreateNoteActivity::class.java), REQUEST_CODE_ADD_NOTE)
            }
        }
        btn_darkMode.setOnClickListener {
            Toast.makeText(this, "This is the DarkMode...", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initRecyclerView() {
        Rv_notes.apply {
            notesAdapter = NoteAdapter( clickListener = { note, position ->
                noteClickPosition = position
                val intent = Intent(this@MainActivity,
                    CreateNoteActivity::class.java).apply {
                    putExtra("isViewOrUpdate", true)
                    putExtra("note", note)
                }
                startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE)
            }, longClickListener = { chkState ->
                if (chkState) {
                    editMode = true
                    imgAddNoteMain.isEnabled = false
                    imgAddNoteMain.setImageDrawable(
                        resources.getDrawable(R.drawable.ic_delete, null))
                    bottomAppBar.replaceMenu(R.menu.select_note)
                } else {
                    editMode = false
                    imgAddNoteMain.setImageDrawable(
                resources.getDrawable(R.drawable.ic_add, null))
                }
            }, checkClickListener = { notes ->
                Log.d("checkList","${notes.size}")
                for(i in notes.values) {
                    Log.d("checkList","${notes.keys}")
                    Log.d("checkList","${notes.values}")
                }
                    checkList = notes
                    imgAddNoteMain.isEnabled = checkList.isNotEmpty()
                }
            )
            this.layoutManager = StaggeredGridLayoutManager(
                2,1).apply {
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            }
            notesAdapter.setHasStableIds(true)
            this.adapter = notesAdapter
            getNote()
            searchNote()
        }
    }

    private fun getNote() {
        viewModel.getAllNotes().observe(this, { notes ->
            notesAdapter.submitList(notes)
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
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getNote()
        } else if(requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            getNote()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initMenu(){
        var click = false
        bottomAppBar.replaceMenu(R.menu.main_note)
        bottomAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.main_menu_edit -> {
                    click = false
                    editMode = true
                    notesAdapter.editItemMode(true)
                    imgAddNoteMain.isEnabled = false
                    bottomAppBar.replaceMenu(R.menu.select_note)
                    imgAddNoteMain.setImageDrawable(
                        resources.getDrawable(R.drawable.ic_delete, null)
                    )
                }
                R.id.main_menu_selectAll -> {
                    click = if(!click) {
                        notesAdapter.selectAll(true)
                        true
                    } else {
                        notesAdapter.selectAll(false)
                        false
                    }
                }
            }
            return@setOnMenuItemClickListener false
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBackPressed() {
        if(editMode) {
            editMode = false
            notesAdapter.selectAll(false)
            notesAdapter.editItemMode(false)
            bottomAppBar.replaceMenu(R.menu.main_note)
            imgAddNoteMain.isEnabled = true
            imgAddNoteMain.setImageDrawable(
                resources.getDrawable(R.drawable.ic_add, null)
            )
        } else {
            super.onBackPressed()
        }
    }
}