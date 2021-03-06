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
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chs.readytonote.Preferences
import com.chs.readytonote.R
import com.chs.readytonote.adapter.NoteAdapter
import com.chs.readytonote.databinding.ActivityMainBinding
import com.chs.readytonote.databinding.LayoutThemeSelectBinding
import com.chs.readytonote.entities.Note
import com.chs.readytonote.viewmodel.MainViewModel
import com.chs.readytonote.viewmodel.MainViewModelFactory


class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE_ADD_NOTE = 1
        private const val REQUEST_CODE_UPDATE_NOTE = 2
    }

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialogTheme: AlertDialog
    private lateinit var notesAdapter: NoteAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var checkList: MutableMap<Int, Note>
    private var editMode: Boolean = false
    private var noteClickPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        checkTheme()
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, MainViewModelFactory(application))
                .get(MainViewModel::class.java)
        initClick()
        initRecyclerView()
        initMenu()
    }

    private fun checkTheme() {
        when (Preferences.data) {
            "WhiteMode" -> {
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO)
            }
            "DarkMode" -> {
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES)
            }
            "Default" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
        binding.imgAddNoteMain.setOnClickListener {
            if (editMode && ::checkList.isInitialized) {
                for (i in checkList.values) {
                    viewModel.deleteNote(i)
                }
                showToast("${checkList.size}개의 노트가 삭제되었습니다.")
                checkList.clear()
                editMode = false
                notesAdapter.editItemMode(false)
                binding.imgAddNoteMain.setImageDrawable(
                        resources.getDrawable(R.drawable.ic_add, null)
                )
                binding.bottomAppBar.replaceMenu(R.menu.main_note)
                getNote()
            } else {
                startActivityForResult(
                        Intent(this,
                                CreateNoteActivity::class.java), REQUEST_CODE_ADD_NOTE)
                overridePendingTransition(R.anim.slide_in_right, R.anim.hold)
            }
        }
        binding.btnDarkMode.setOnClickListener {
            showThemeDialog()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initRecyclerView() {
        binding.RvNotes.apply {
            notesAdapter = NoteAdapter(clickListener = { note, position ->
                noteClickPosition = position
                val intent = Intent(this@MainActivity,
                        CreateNoteActivity::class.java).apply {
                    putExtra("isViewOrUpdate", true)
                    putExtra("note", note)
                }
                startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE)
                overridePendingTransition(R.anim.slide_in_right, R.anim.hold)
            }, longClickListener = { chkState ->
                if (chkState) {
                    editMode = true
                    binding.imgAddNoteMain.isEnabled = false
                    binding.imgAddNoteMain.setImageDrawable(
                            resources.getDrawable(R.drawable.ic_delete, null))
                    binding.bottomAppBar.replaceMenu(R.menu.select_note)
                } else {
                    editMode = false
                    binding.imgAddNoteMain.setImageDrawable(
                            resources.getDrawable(R.drawable.ic_add, null))
                }
            }, checkClickListener = { notes ->
                checkList = notes
                binding.imgAddNoteMain.isEnabled = checkList.isNotEmpty()
            }
            )
            this.layoutManager = StaggeredGridLayoutManager(2, 1)
            notesAdapter.setHasStableIds(true)
            this.adapter = notesAdapter
            getNote()
            searchNote()
        }
    }

    private fun getNote() {
        checkList = mutableMapOf()
        viewModel.getAllNotes().observe(this, { notes ->
            notesAdapter.submitList(notes)
            emptyList(notes.isEmpty())
        })
    }

    private fun emptyList(empty: Boolean) = if (empty) {
        binding.layoutEmptyNote.emptyNote.visibility = View.VISIBLE
    } else {
        binding.layoutEmptyNote.emptyNote.visibility = View.GONE
    }

    private fun searchNote() {
        binding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                viewModel.searchNotes(p0.toString()).observe(this@MainActivity, {
                    if (p0!!.isNotEmpty()) {
                        notesAdapter.submitList(it)
                    } else {
                        getNote()
                    }
                    notesAdapter.notifyDataSetChanged()
                })
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initMenu() {
        var click = false
        binding.bottomAppBar.replaceMenu(R.menu.main_note)
        binding.bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.main_menu_edit -> {
                    click = false
                    editMode = true
                    notesAdapter.editItemMode(true)
                    binding.imgAddNoteMain.isEnabled = false
                    binding.imgAddNoteMain.setImageDrawable(
                            resources.getDrawable(R.drawable.ic_delete, null)
                    )
                    binding.bottomAppBar.replaceMenu(R.menu.select_note)
                }
                R.id.main_menu_selectAll -> {
                    click = if (!click &&
                            checkList.size != notesAdapter.currentList.size) {
                        notesAdapter.selectAll(true)
                        true
                    } else if (checkList.size == notesAdapter.currentList.size) {
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
                    overridePendingTransition(R.anim.slide_in_right, R.anim.hold)
                }
            }
            return@setOnMenuItemClickListener false
        }
    }

    private fun showThemeDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val dialogView = LayoutThemeSelectBinding.inflate(LayoutInflater.from(this))
        builder.setView(dialogView.root)
        dialogTheme = builder.create()
        if (dialogTheme.window != null) {
            dialogTheme.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        when (Preferences.data) {
            "WhiteMode" -> dialogView.rdoWhite.isChecked = true
            "DarkMode" -> dialogView.rdoDark.isChecked = true
            "Default" -> dialogView.rdoDefault.isChecked = true
        }
        dialogView.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rdo_dark -> Preferences.data = "DarkMode"
                R.id.rdo_white -> Preferences.data = "WhiteMode"
                R.id.rdo_default -> Preferences.data = "Default"
            }
        }
        dialogView.btnOk.setOnClickListener {
            dialogTheme.dismiss()
            this@MainActivity.recreate()
        }
        dialogView.btnCancel.setOnClickListener {
            dialogTheme.dismiss()
        }
        dialogTheme.show()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getNote()
            showToast("노트 추가됨")
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            getNote()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBackPressed() {
        if (editMode) {
            editMode = false
            notesAdapter.selectAll(false)
            notesAdapter.editItemMode(false)
            binding.bottomAppBar.replaceMenu(R.menu.main_note)
            binding.imgAddNoteMain.isEnabled = true
            binding.imgAddNoteMain.setImageDrawable(
                    resources.getDrawable(R.drawable.ic_add, null)
            )
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}