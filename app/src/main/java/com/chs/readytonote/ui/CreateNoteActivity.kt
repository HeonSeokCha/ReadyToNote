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
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.chs.readytonote.R
import com.chs.readytonote.entities.Note
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.chs.readytonote.GlideApp
import com.chs.readytonote.adapter.LabelAdapter
import com.chs.readytonote.databinding.*
import com.chs.readytonote.entities.Label
import com.chs.readytonote.entities.LabelCheck
import com.chs.readytonote.getFileName
import com.chs.readytonote.getRealPathFromURI
import com.chs.readytonote.viewmodel.MainViewModel
import com.chs.readytonote.viewmodel.MainViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior

class CreateNoteActivity : AppCompatActivity() {
    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE_IMAGE = 1001
    }

    private var noteId = 0
    private var imagePath: String = ""
    private var noteColor: String = "#333333"
    private var label: String = ""
    private lateinit var alreadyAvailableNote: Note
    private lateinit var binding: ActivityCreateNoteBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var checkLabel:LabelCheck
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
        viewModel = ViewModelProvider(this,MainViewModelFactory(application))
            .get(MainViewModel::class.java)
        initView()
        initClick()
    }

    private fun initView() {
        noteColor = "#333333"
        imagePath = ""
        binding.txtDateTime.text = SimpleDateFormat("yyyy년 MM월 dd일 E",
            Locale.KOREA).format(Date())
        bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutMiscellaneous.root)

        if(intent.getBooleanExtra("isViewOrUpdate",false)) {
            alreadyAvailableNote = intent.getParcelableExtra("note")
            setViewOrUpdateNote()
        }
        if(intent.getBooleanExtra("shortCutImage",false)) {
            checkPermImage()
        }
        binding.layoutMiscellaneous.textMiscellaneous.setOnClickListener {
                bottomSheetBehavior.apply {
                    if(this.state != BottomSheetBehavior.STATE_EXPANDED) {
                        this.state = BottomSheetBehavior.STATE_EXPANDED
                    } else {
                        this.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
            }
        initMiscellaneous()
        setSubtitleIndicator(noteColor)
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

        if(::alreadyAvailableNote.isInitialized) {
            binding.layoutMiscellaneous.layoutDeleteNote.visibility = View.VISIBLE
            binding.layoutMiscellaneous.layoutDeleteNote.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                showDeleteDialog()
            }
        }
    }

    private fun saveNote() {
        if(binding.inputNoteTitle.text.trim().isNullOrEmpty()) {
            Toast.makeText(this,
                "Note title can't be empty!", Toast.LENGTH_SHORT).show()
        } else if(binding.inputNoteSubtitle.text.trim().isNullOrEmpty()
            && binding.inputNoteText.text.trim().isNullOrEmpty()) {
            Toast.makeText(this,
                "Note can't be empty!", Toast.LENGTH_SHORT).show()
        } else {
            webLink = if(binding.txtWebUrl.text.trim().isNotEmpty()) {
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
            if(::checkLabel.isInitialized) {
                checkLabel.copy(note_id = noteId)
                viewModel.updateCheckLabel(checkLabel)
            }
            if(::alreadyAvailableNote.isInitialized) {
                note.id = alreadyAvailableNote.id
            }
            viewModel.insertNote(note)
            closeKeyboard()
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }
    }

    private fun deleteNote(note:Note) {
        viewModel.deleteNote(note)
        val intent = Intent()
        intent.putExtra("isNoteDelete",true)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    private fun setViewOrUpdateNote() {
        noteId = alreadyAvailableNote.id
        label = alreadyAvailableNote.label.toString()
        binding.inputNoteTitle.setText(alreadyAvailableNote.title)
        binding.inputNoteSubtitle.setText(alreadyAvailableNote.subtitle)
        binding.inputNoteText.setText(alreadyAvailableNote.noteText)

        if (alreadyAvailableNote.imgPath!!.isNotEmpty()) {
            binding.imageNote.visibility = View.VISIBLE
            GlideApp.with(this).load(alreadyAvailableNote.imgPath)
                .error(R.drawable.ic_done)
                .into(binding.imageNote)
            imagePath = alreadyAvailableNote.imgPath!!
            binding.imageDelete.visibility = View.VISIBLE
        }
        if (alreadyAvailableNote.webLink!!.isNotEmpty()) {
            binding.txtWebUrl.text = alreadyAvailableNote.webLink
            binding.txtWebUrl.visibility = View.VISIBLE
            binding.imageDeleteUrl.visibility = View.VISIBLE
        }
    }

    private fun setSubtitleIndicator(color:String) {
        var gradientDrawable = binding.viewSubtitleIndicator.background as GradientDrawable
        if(noteColor=="#333333") {
            gradientDrawable.setColor(resources.getColor(R.color.colorNoteDefaultColor,null))
        } else {
            gradientDrawable.setColor(Color.parseColor(noteColor))
        }
    }

    private fun initMiscellaneous() {
        with(binding.layoutMiscellaneous) {
            imageColorDefault.setOnClickListener {
                noteColor = "#333333"
                imageColorDefault.setImageResource(R.drawable.ic_done)
                imageColorYellow.setImageResource(0)
                imageColorRed.setImageResource(0)
                imageColorBlue.setImageResource(0)
                imageColorPurple.setImageResource(0)
                setSubtitleIndicator(noteColor)
            }

            imageColorYellow.setOnClickListener {
                noteColor = "#FDBE3B"
                imageColorDefault.setImageResource(0)
                imageColorYellow.setImageResource(R.drawable.ic_done)
                imageColorRed.setImageResource(0)
                imageColorBlue.setImageResource(0)
                imageColorPurple.setImageResource(0)
                setSubtitleIndicator(noteColor)
            }

            imageColorRed.setOnClickListener {
                noteColor = "#FF4842"
                imageColorDefault.setImageResource(0)
                imageColorYellow.setImageResource(0)
                imageColorRed.setImageResource(R.drawable.ic_done)
                imageColorBlue.setImageResource(0)
                imageColorPurple.setImageResource(0)
                setSubtitleIndicator(noteColor)
            }

            imageColorBlue.setOnClickListener {
                noteColor = "#3A52FC"
                imageColorDefault.setImageResource(0)
                imageColorYellow.setImageResource(0)
                imageColorRed.setImageResource(0)
                imageColorBlue.setImageResource(R.drawable.ic_done)
                imageColorPurple.setImageResource(0)
                setSubtitleIndicator(noteColor)
            }

            imageColorPurple.setOnClickListener {
                noteColor = "#967FFA"
                imageColorDefault.setImageResource(0)
                imageColorYellow.setImageResource(0)
                imageColorRed.setImageResource(0)
                imageColorBlue.setImageResource(0)
                imageColorPurple.setImageResource(R.drawable.ic_done)
                setSubtitleIndicator(noteColor)
            }
        }

        if(::alreadyAvailableNote.isInitialized
            && alreadyAvailableNote.color!!.isNotEmpty()) {
            when(alreadyAvailableNote.color) {
                "#333333"-> {
                    binding.layoutMiscellaneous.imageColorDefault.performClick()
                }
                "#FDBE3B"->binding.layoutMiscellaneous.imageColorYellow.performClick()
                "#FF4842"->binding.layoutMiscellaneous.imageColorRed.performClick()
                "#3A52FC"->binding.layoutMiscellaneous.imageColorBlue.performClick()
                "#967FFA"->binding.layoutMiscellaneous.imageColorPurple.performClick()
            }
        }
    }

    private fun showAddUrlDialog() {
        val builder:AlertDialog.Builder = AlertDialog.Builder(this)
        val dialogView = LayoutAddUrlBinding.inflate(LayoutInflater.from(this))
        builder.setView(dialogView.root)
        dialogUrlAdd = builder.create()
        if(dialogUrlAdd.window!=null) {
            dialogUrlAdd.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialogView.inputUrl.requestFocus()
        dialogView.textAdd.setOnClickListener {
            when {
                dialogView.inputUrl.text.toString().trim().isEmpty() -> {
                    Toast.makeText(this@CreateNoteActivity,
                        "Enter URL", Toast.LENGTH_SHORT).show()
                }
                !Patterns.WEB_URL.matcher(dialogView.inputUrl.text.toString()).matches() -> {
                    Toast.makeText(this@CreateNoteActivity,
                        "Enter valid URL", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    closeKeyboard()
                    binding.txtWebUrl.text = dialogView.inputUrl.text
                    binding.txtWebUrl.visibility = View.VISIBLE
                    dialogUrlAdd.dismiss()
                    binding.imageDeleteUrl.visibility = View.VISIBLE
                }
            }
        }
        dialogView.textCancel.setOnClickListener {
            dialogUrlAdd.dismiss()
        }
        dialogUrlAdd.show()
    }

    private fun showDeleteDialog() {
        val builder:AlertDialog.Builder = AlertDialog.Builder(this)
        val dialogView = LayoutDeleteNoteBinding.inflate(LayoutInflater.from(this))
        builder.setView(dialogView.root)
        dialogDelete = builder.create()
        if(dialogDelete.window != null) {
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
        val builder:AlertDialog.Builder = AlertDialog.Builder(this)
        val dialogView = LayoutLabelBinding.inflate(LayoutInflater.from(this))
        builder.setView(dialogView.root)
        dialogLabelAdd = builder.create()
        if(dialogLabelAdd.window != null) {
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
        getCheckLabel()
        initLabelRecyclerview(dialogView)
        dialogLabelAdd.show()
    }

    private fun initLabelRecyclerview(view: LayoutLabelBinding) {
        view.RvLabel.apply {
            labelAdapter = LabelAdapter( clickListener = {
                if(it.checked) {
                    checkLabel.checkedLabelId = it.id
                } else {
                    checkLabel.checkedLabelId = 0
                }
                Log.d("checkLabel","$checkLabel")
            },
            addClickListener = { labelTitle ->
                viewModel.insertLabel(Label(labelTitle,false))
                viewModel.insertCheckLabel(checkLabel)
                view.inputLabel.text.clear()
                getLabel()
            })
            labelAdapter.setHasStableIds(true)
            this.layoutManager = LinearLayoutManager(this@CreateNoteActivity)
            this.adapter = labelAdapter
            getLabel()
            searchLabel(view)
        }
    }

    private fun getLabel() {
        viewModel.getAllLabel().observe(this@CreateNoteActivity,{
            for(i in it.indices) {
                if(::checkLabel.isInitialized && it[i].id == checkLabel.checkedLabelId) {
                    it[i].checked = true
                }
            }
            labelAdapter.submitList(it)
        })
    }

    private fun getCheckLabel() {
        TODO("get coroutine return value..")
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
        if(this.currentFocus != null) {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                this.currentFocus!!.windowToken, 0)
        }
    }

    private fun checkPermImage() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE_IMAGE)
            } else pickImageFromGallery()
        } else pickImageFromGallery()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if(intent.resolveActivity(packageManager)!= null) {
            intent.type = "image/*"
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_CODE_IMAGE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                }
                else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            binding.imageNote.visibility = View.VISIBLE
            DownsampleStrategy.SampleSizeRounding.QUALITY
            GlideApp.with(this).load(data!!.data)
                .into(binding.imageNote)
            imagePath = getRealPathFromURI(this, data.data!!)!!
            Log.d("ImagePath","${contentResolver.getFileName(data.data!!)}")
            binding.imageDelete.visibility = View.VISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun onBackPressed() {
        if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }
}