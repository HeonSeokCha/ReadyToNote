package com.chs.readytonote.ui


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chs.readytonote.Preferences
import com.chs.readytonote.R
import com.chs.readytonote.adapter.NoteAdapter
import com.chs.readytonote.entities.Note
import com.chs.readytonote.viewmodel.MainViewModel
import com.chs.readytonote.viewmodel.MainViewModelFactory
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_add_url.view.*
import kotlinx.android.synthetic.main.layout_theme_select.view.*


class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE_ADD_NOTE = 1
        private const val REQUEST_CODE_UPDATE_NOTE = 2
    }
    private lateinit var dialogTheme: AlertDialog
    private lateinit var notesAdapter: NoteAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var checkList: MutableMap<Int,Note>
    private var editMode: Boolean = false
    private var noteClickPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        checkTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this, MainViewModelFactory(application))
            .get(MainViewModel::class.java)
        initClick()
        initRecyclerView()
        initMenu()
    }

    private fun checkTheme() {
        when (Preferences.data) {
            "WhiteMode" ->{
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO)
            }
            "DarkMode" ->{
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES)
            }
            "Default" ->{
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initClick() {
        imgAddNoteMain.setOnClickListener {
            if(editMode && ::checkList.isInitialized) {
                for(i in checkList.values) {
                    viewModel.delete(i)
                }
                Toast.makeText(this,
                    "${checkList.size}개의 노트가 삭제되었습니다.",
                    Toast.LENGTH_SHORT).show()
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
            showThemeDialog()
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
                    checkList = notes
                    imgAddNoteMain.isEnabled = checkList.isNotEmpty()
                }
            )
            this.layoutManager = StaggeredGridLayoutManager(2,1)
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
        checkList = mutableMapOf()
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
            Toast.makeText(this, "노트 추가됨", Toast.LENGTH_SHORT).show()
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
                    imgAddNoteMain.setImageDrawable(
                        resources.getDrawable(R.drawable.ic_delete, null)
                    )
                    bottomAppBar.replaceMenu(R.menu.select_note)
                }
                R.id.main_menu_selectAll -> {
                    click = if(!click &&
                        checkList.size!=notesAdapter.currentList.size) {
                        notesAdapter.selectAll(true)
                        true
                    } else if(checkList.size==notesAdapter.currentList.size) {
                        notesAdapter.selectAll(false)
                        false
                    } else {
                        notesAdapter.selectAll(false)
                        false
                    }
                }
                R.id.main_menu_shortcut_photo -> {
                    val intent = Intent(this@MainActivity,
                        CreateNoteActivity::class.java).apply {
                        putExtra("shortCutImage", true)
                    }
                    startActivityForResult(intent, REQUEST_CODE_ADD_NOTE)
                }
                R.id.main_menu_shortcut_record -> {
                    val intent = Intent(this@MainActivity,
                        CreateNoteActivity::class.java).apply {
                        putExtra("shortCutMic", true)
                    }
                    startActivityForResult(intent, REQUEST_CODE_ADD_NOTE)
                }
            }
            return@setOnMenuItemClickListener false
        }
    }

    private fun showThemeDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_theme_select,
            (findViewById(R.id.layoutThemeContainer)))
        builder.setView(view)
        dialogTheme = builder.create()
        if(dialogTheme.window!=null) {
            dialogTheme.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        when (Preferences.data) {
            "WhiteMode" -> view.rdo_white.isChecked = true
            "DarkMode" -> view.rdo_dark.isChecked = true
            "Default" -> view.rdo_default.isChecked = true
        }
        view.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.rdo_dark -> Preferences.data = "DarkMode"
                R.id.rdo_white -> Preferences.data = "WhiteMode"
                R.id.rdo_default -> Preferences.data = "Default"
            }
        }
        view.btn_ok.setOnClickListener {
            dialogTheme.dismiss()
            this@MainActivity.recreate()
        }
        view.btn_cancel.setOnClickListener {
            dialogTheme.dismiss()
        }
        dialogTheme.show()
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