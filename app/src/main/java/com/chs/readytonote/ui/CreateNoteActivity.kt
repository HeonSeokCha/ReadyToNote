package com.chs.readytonote.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.chs.readytonote.R
import com.chs.readytonote.entities.Note
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.chs.readytonote.Util
import com.chs.readytonote.adapter.LabelAdapter
import com.chs.readytonote.databinding.*
import com.chs.readytonote.entities.Label
import com.chs.readytonote.entities.LabelCheck
import com.google.android.material.bottomsheet.BottomSheetBehavior

class CreateNoteActivity : AppCompatActivity() {
    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE_IMAGE = 1001
    }

    private var noteId: Int = 0
    private var imagePath: String = ""
    private var noteColor: String = "#333333"
    private var label: String = ""
    private lateinit var alreadyAvailableNote: Note
    private lateinit var binding: ActivityCreateNoteBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var checkLabel: LabelCheck
    private lateinit var dialogUrlAdd: AlertDialog
    private lateinit var dialogDelete: AlertDialog
    private lateinit var dialogLabelAdd: AlertDialog
    private lateinit var labelAdapter: LabelAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var webLink: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = MainViewModel(application)
        initView()
        initClick()
        getCheckLabel()
    }

    private fun initView() {
        if (intent.getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailableNote = intent.getParcelableExtra("note")!!
            binding.layoutMiscellaneous.layoutDeleteNote.visibility = View.VISIBLE
            setViewOrUpdateNote()
        }
        if (intent.getBooleanExtra("shortCutImage", false)) {
            checkPermImage()
        }
//        initMiscellaneous()
        setSubtitleIndicator()
        bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutMiscellaneous.root)
        binding.txtDateTime.text = SimpleDateFormat(
            "yyyy년 MM월 dd일 E",
            Locale.KOREA
        ).format(Date())
        binding.layoutMiscellaneous.textMiscellaneous.setOnClickListener {
            bottomSheetBehavior.apply {
                if (this.state != BottomSheetBehavior.STATE_EXPANDED) {
                    this.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    this.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }
    }

    private fun initClick() {
        binding.layoutMiscellaneous.layoutAddImage.setOnClickListener {
            checkPermImage()
        }

        binding.imgSave.setOnClickListener {
            saveNote()
        }

        binding.imgBack.setOnClickListener {
            closeKeyboard()
            onBackPressed()
        }

        binding.layoutMiscellaneous.layoutAddUrl.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            showAddUrlDialog()
        }

        binding.layoutMiscellaneous.layoutAddLabel.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            showLabelDialog()
        }

        binding.imageDelete.setOnClickListener {
            imagePath = ""
            binding.imageNote.visibility = View.GONE
            binding.imageDelete.visibility = View.GONE
        }

        binding.imageDeleteUrl.setOnClickListener {
            webLink = ""
            binding.txtWebUrl.visibility = View.GONE
            binding.imageDeleteUrl.visibility = View.GONE
        }

        binding.layoutMiscellaneous.layoutDeleteNote.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            showDeleteDialog()
        }
    }

    private fun saveNote() {
        if (binding.inputNoteTitle.text.trim().isNullOrEmpty()) {
            Toast.makeText(
                this,
                "Note title can't be empty!", Toast.LENGTH_SHORT
            ).show()
        } else if (binding.inputNoteSubtitle.text.trim().isNullOrEmpty()
            && binding.inputNoteText.text.trim().isNullOrEmpty()
        ) {
            Toast.makeText(
                this,
                "Note can't be empty!", Toast.LENGTH_SHORT
            ).show()
        } else {
            webLink = if (binding.txtWebUrl.text.trim().isNotEmpty()) {
                binding.txtWebUrl.text.toString()
            } else ""

            val note = Note(
                title = binding.inputNoteTitle.text.toString(),
                label = label,
                dateTime = binding.txtDateTime.text.toString(),
                subtitle = binding.inputNoteSubtitle.text.toString(),
                noteText = binding.inputNoteText.text.toString(),
                imgPath = imagePath,
                color = noteColor,
                webLink = webLink,
            )
            if (::alreadyAvailableNote.isInitialized) {
                note.id = alreadyAvailableNote.id
            }
            viewModel.insertNote(note).observe(this, { insertId ->
                if (::checkLabel.isInitialized && note.id == 0) {
                    checkLabel.note_id = insertId.toInt()
                    viewModel.updateCheckLabel(checkLabel)
                } else {
                    viewModel.updateCheckLabel(checkLabel)
                }
            })
            closeKeyboard()
            viewModel.insertNote(note)
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }
    }

    private fun deleteNote(note: Note) {
        viewModel.deleteNote(note)
        viewModel.deleteCheckLabel(note.id)
        val intent = Intent()
        intent.putExtra("isNoteDelete", true)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun setViewOrUpdateNote() {
        noteId = alreadyAvailableNote.id
        label = alreadyAvailableNote.label.toString()
        noteColor = alreadyAvailableNote.color.toString()
        binding.inputNoteTitle.setText(alreadyAvailableNote.title)
        binding.inputNoteSubtitle.setText(alreadyAvailableNote.subtitle)
        binding.inputNoteText.setText(alreadyAvailableNote.noteText)

        if (alreadyAvailableNote.imgPath!!.isNotEmpty()) {
            imagePath = alreadyAvailableNote.imgPath!!
            binding.imageNote.visibility = View.VISIBLE
            binding.imageDelete.visibility = View.VISIBLE
            binding.imageNote.load(alreadyAvailableNote.imgPath) {
                error(R.drawable.ic_done)
            }
        }
        if (alreadyAvailableNote.webLink!!.isNotEmpty()) {
            binding.txtWebUrl.text = alreadyAvailableNote.webLink
            binding.txtWebUrl.visibility = View.VISIBLE
            binding.imageDeleteUrl.visibility = View.VISIBLE
        }
    }

    private fun setSubtitleIndicator() {
        var gradientDrawable = binding.viewSubtitleIndicator.background as GradientDrawable
        if (noteColor == "#333333") {
            gradientDrawable.setColor(resources.getColor(R.color.colorNoteDefaultColor, null))
        } else {
            gradientDrawable.setColor(Color.parseColor(noteColor))
        }
    }

//    private fun initMiscellaneous() {
//        with(binding.layoutMiscellaneous) {
//            imageColorDefault.setOnClickListener {
//                noteColor = "#333333"
//                imageColorDefault.setImageResource(R.drawable.ic_done)
//                imageColorYellow.setImageResource(0)
//                imageColorRed.setImageResource(0)
//                imageColorBlue.setImageResource(0)
//                imageColorPurple.setImageResource(0)
//                setSubtitleIndicator()
//            }
//
//            imageColorYellow.setOnClickListener {
//                noteColor = "#FDBE3B"
//                imageColorDefault.setImageResource(0)
//                imageColorYellow.setImageResource(R.drawable.ic_done)
//                imageColorRed.setImageResource(0)
//                imageColorBlue.setImageResource(0)
//                imageColorPurple.setImageResource(0)
//                setSubtitleIndicator()
//            }
//
//            imageColorRed.setOnClickListener {
//                noteColor = "#FF4842"
//                imageColorDefault.setImageResource(0)
//                imageColorYellow.setImageResource(0)
//                imageColorRed.setImageResource(R.drawable.ic_done)
//                imageColorBlue.setImageResource(0)
//                imageColorPurple.setImageResource(0)
//                setSubtitleIndicator()
//            }
//
//            imageColorBlue.setOnClickListener {
//                noteColor = "#3A52FC"
//                imageColorDefault.setImageResource(0)
//                imageColorYellow.setImageResource(0)
//                imageColorRed.setImageResource(0)
//                imageColorBlue.setImageResource(R.drawable.ic_done)
//                imageColorPurple.setImageResource(0)
//                setSubtitleIndicator()
//            }
//
//            imageColorPurple.setOnClickListener {
//                noteColor = "#967FFA"
//                imageColorDefault.setImageResource(0)
//                imageColorYellow.setImageResource(0)
//                imageColorRed.setImageResource(0)
//                imageColorBlue.setImageResource(0)
//                imageColorPurple.setImageResource(R.drawable.ic_done)
//                setSubtitleIndicator()
//            }
//
//            if (::alreadyAvailableNote.isInitialized
//                && alreadyAvailableNote.color!!.isNotEmpty()
//            ) {
//                when (alreadyAvailableNote.color) {
//                    "#333333" -> imageColorDefault.performClick()
//                    "#FDBE3B" -> imageColorYellow.performClick()
//                    "#FF4842" -> imageColorRed.performClick()
//                    "#3A52FC" -> imageColorBlue.performClick()
//                    "#967FFA" -> imageColorPurple.performClick()
//                }
//            }
//        }
//    }

    private fun showAddUrlDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val dialogView = LayoutAddUrlBinding.inflate(LayoutInflater.from(this))
        builder.setView(dialogView.root)
        dialogUrlAdd = builder.create()
        if (dialogUrlAdd.window != null) {
            dialogUrlAdd.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialogView.inputUrl.requestFocus()
        dialogView.textAdd.setOnClickListener {
            when {
                dialogView.inputUrl.text.toString().trim().isEmpty() -> {
                    Toast.makeText(
                        this@CreateNoteActivity,
                        "Enter URL", Toast.LENGTH_SHORT
                    ).show()
                }
                !Patterns.WEB_URL.matcher(dialogView.inputUrl.text.toString()).matches() -> {
                    Toast.makeText(
                        this@CreateNoteActivity,
                        "Enter valid URL", Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    closeKeyboard()
                    binding.txtWebUrl.text = dialogView.inputUrl.text
                    binding.txtWebUrl.visibility = View.VISIBLE
                    binding.imageDeleteUrl.visibility = View.VISIBLE
                    dialogUrlAdd.dismiss()
                }
            }
        }
        dialogView.textCancel.setOnClickListener {
            dialogUrlAdd.dismiss()
        }
        dialogUrlAdd.show()
    }

    private fun showDeleteDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val dialogView = LayoutDeleteNoteBinding.inflate(LayoutInflater.from(this))
        builder.setView(dialogView.root)
        dialogDelete = builder.create()
        if (dialogDelete.window != null) {
            dialogDelete.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialogView.txtDeleteNote.setOnClickListener {
            dialogDelete.dismiss()
            deleteNote(alreadyAvailableNote)
        }
        dialogView.textDeleteCancel.setOnClickListener {
            dialogDelete.dismiss()
        }
        dialogDelete.show()
    }

    private fun showLabelDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val dialogView = LayoutLabelBinding.inflate(LayoutInflater.from(this))
        builder.setView(dialogView.root)
        dialogLabelAdd = builder.create()
        if (dialogLabelAdd.window != null) {
            dialogLabelAdd.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialogView.textAdd.setOnClickListener {
            viewModel.insertCheckLabel(checkLabel)
            closeKeyboard()
            dialogLabelAdd.dismiss()
        }

        dialogView.textCancel.setOnClickListener {
            closeKeyboard()
            dialogLabelAdd.dismiss()
        }
        initLabelRecyclerview(dialogView)
        dialogLabelAdd.show()
    }

    private fun initLabelRecyclerview(view: LayoutLabelBinding) {
        view.RvLabel.apply {
            labelAdapter = LabelAdapter(clickListener = {
                if (it.checked) {
                    checkLabel.checkedLabelId = it.id
                    label = it.title.toString()
                } else {
                    checkLabel.checkedLabelId = 0
                    label = ""
                }
            },
                addClickListener = { labelTitle ->
                    viewModel.insertLabel(Label(labelTitle, false))
                    viewModel.insertCheckLabel(checkLabel)
                    view.inputLabel.text.clear()
                    getLabel()
                })
            labelAdapter.setHasStableIds(true)
            layoutManager = LinearLayoutManager(this@CreateNoteActivity)
            adapter = labelAdapter
            getLabel()
            searchLabel(view)
        }
    }

    private fun getLabel() {
        viewModel.getAllLabel().observe(this@CreateNoteActivity, { labelList ->
            labelList.filter { it.id == checkLabel.checkedLabelId }
                .forEach { it.checked = true }
            labelAdapter.submitList(labelList)
        })
    }

    private fun getCheckLabel() {
        viewModel.getCheckLabel(noteId).observe(this, {
            checkLabel = it ?: LabelCheck(noteId, 0)
        })
    }

    private fun searchLabel(view: LayoutLabelBinding) {
        view.inputLabel.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelAdapter.searchLabel(s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelAdapter.cancelTimer()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })
    }

    private fun closeKeyboard() {
        if (this.currentFocus != null) {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                this.currentFocus!!.windowToken, 0
            )
        }
    }

    private fun checkPermImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE_IMAGE)
            } else pickImageFromGallery()
        } else pickImageFromGallery()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager) != null) {
            intent.apply {
                type = "image/*"
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE_IMAGE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    pickImageFromGallery()
                } else Toast.makeText(
                    this,
                    "Permission denied", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            if (data != null) {
                binding.imageNote.isVisible = true
                binding.imageDelete.isVisible = true
                imagePath = Util.getRealPathFromURI(this, data.data!!)!!
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
            overridePendingTransition(
                R.anim.hold,
                R.anim.slide_out_left
            )
        }
    }
}