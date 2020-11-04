package com.chs.readytonote.ui


import android.app.Activity
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chs.readytonote.R
import com.chs.readytonote.adapter.NoteAdapter
import com.chs.readytonote.entities.Note
import com.chs.readytonote.viewmodel.MainViewModel
import com.chs.readytonote.viewmodel.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE_ADD_NOTE = 1
        private const val REQUEST_CODE_UPDATE_NOTE = 2
        private const val REQUEST_CODE_SHOW_NOTE = 3
        private const val REQUEST_CODE_REMOVE_NOTES = 4
    }

    private lateinit var notesAdapter: NoteAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var checkList: MutableMap<Int,Note>
    private var checkMode: Boolean = false
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

    private fun initClick() {
        imgAddNoteMain.setOnClickListener {
            if(checkMode) {
                var delList = checkList.map { it.value }.toList()
                for(i in checkList.values.indices) {
                    viewModel.delete(delList[i])
                }
                checkList.clear()
                checkMode = false
                notesAdapter.editItemMode(false)
                imgAddNoteMain.setImageDrawable(
                        resources.getDrawable(R.drawable.ic_add, null))
                getNote()
            } else {
                startActivityForResult(
                    Intent(this,
                        CreateNoteActivity::class.java), REQUEST_CODE_ADD_NOTE)
            }
        }
    }
    private fun initMenu(){
        bottomAppBar.replaceMenu(R.menu.create_note)
        bottomAppBar.setOnMenuItemClickListener {
//            when(it.itemId) {
//                R.id.main_menu_edit -> {
//                    notesAdapter.editItem(true)
//                }
//            }
            return@setOnMenuItemClickListener false
        }
    }

    private fun initRecyclerView() {
        Rv_notes.apply {
            notesAdapter = NoteAdapter( clickListener = { note, position ->
                noteClickPosition = position
                val intent = Intent(
                    this@MainActivity,
                    CreateNoteActivity::class.java
                )
                intent.putExtra("isViewOrUpdate", true)
                intent.putExtra("note", note)
                startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE)
            }, longClickListener = { chkState ->
                if (chkState) {
                    checkMode = true
                    imgAddNoteMain.setImageDrawable(
                        resources.getDrawable(R.drawable.ic_delete, null))
                } else {
                    checkMode = false
                    imgAddNoteMain.setImageDrawable(
                resources.getDrawable(R.drawable.ic_add, null))
                }
            }, checkClickListener = { notes ->
                    checkList = notes
                    Log.d("스파게티","체크리스트 바뀜, ${notes.size}")
                }
            )
            this.layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            this.adapter = notesAdapter
            getNote()
            searchNote()
        }
    }

    private fun getNote() {
        viewModel.getAllNotes().observe(this, {
            notesAdapter.submitList(it)
//            when (reqCode) {
//                REQUEST_CODE_SHOW_NOTE -> {
//                    notesAdapter.setData(notes)
//                    notesAdapter.notifyDataSetChanged()
//                }
//                REQUEST_CODE_ADD_NOTE -> {
//                    if (notes.isNotEmpty()) {
//                        notesAdapter.setData(notes)
//                        notesAdapter.notifyItemInserted(0)
//                        Rv_notes.smoothScrollToPosition(0)
//                    } else {
//                        getNote(REQUEST_CODE_SHOW_NOTE, false)
//                    }
//                }
//                REQUEST_CODE_UPDATE_NOTE -> {
//                    notesAdapter.setData(notes)
//                    if (isNoteDelete) {
//                        notesAdapter.notifyItemRemoved(noteClickPosition)
//
//                    } else {
//                        notesAdapter.notifyItemChanged(noteClickPosition)
//                    }
//                }
//                REQUEST_CODE_REMOVE_NOTES -> {
//                    var delList = checkList.map { it.value }.toList()
//                    for(i in checkList.values.indices) {
//                        viewModel.delete(delList[i])
//                    }
//                    notesAdapter.editItemMode(false)
//                    for(i in checkList.values.indices) {
//                        notesAdapter.notifyItemRemoved(i)
//                    }
//                    checkList.clear()
//                    notesAdapter.setData(notes)
//                    checkMode = false
//                    imgAddNoteMain.setImageDrawable(
//                        resources.getDrawable(R.drawable.ic_add, null))
//                }
//            }
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
        Log.d("호출죔",requestCode.toString())
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == Activity.RESULT_OK) {
            getNote()
        } else if(requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            getNote()
        }
    }

    override fun onBackPressed() {
        if(checkMode) {
            notesAdapter.editItemMode(false)
            checkMode = false
            imgAddNoteMain.setImageDrawable(
                resources.getDrawable(R.drawable.ic_add, null))
        } else {
            super.onBackPressed()
        }
    }
}