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
import kotlinx.android.synthetic.main.activity_create_note.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.ContextCompat.checkSelfPermission
import com.bumptech.glide.Glide
import com.chs.readytonote.calcRotate
import com.chs.readytonote.getRealPathFromURI
import com.chs.readytonote.viewmodel.MainViewModel
import com.chs.readytonote.viewmodel.MainViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.layout_add_url.view.*
import kotlinx.android.synthetic.main.layout_delete_note.view.*
import kotlinx.android.synthetic.main.layout_miscellaneous.*
import kotlinx.android.synthetic.main.layout_miscellaneous.view.*

class CreateNoteActivity : AppCompatActivity() {
    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var noteColor: String
    private lateinit var imagePath: String
    private lateinit var webLink: String
    private lateinit var dialogUrlAdd: AlertDialog
    private lateinit var dialogDelete: AlertDialog
    private lateinit var alreadyAvailableNote: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)
        viewModel = ViewModelProvider(this,MainViewModelFactory(application))
            .get(MainViewModel::class.java)
        initView()
        initClick()
    }

    private fun initView() {
        noteColor = "#333333"
        imagePath = ""
        txtDateTime.text = SimpleDateFormat("yyyy년 MM월 dd일 E요일",
            Locale.KOREA).format(Date())
        bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous)

        if(intent.getBooleanExtra("isViewOrUpdate",false)) {
            alreadyAvailableNote = intent.getParcelableExtra<Note>("note")
            setViewOrUpdateNote()
        }
        layoutMiscellaneous.findViewById<TextView>(R.id.textMiscellaneous)
            .setOnClickListener {
            if(bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        initMiscellaneous()
        setSubtitleIndicator()
    }

    private fun initClick(){
        imgSave.setOnClickListener { saveNote() }
        imgBack.setOnClickListener { onBackPressed() }
        layoutAddImage.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                if (checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                } else pickImageFromGallery()
            } else pickImageFromGallery()
        }

        layoutAddUrl.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            showAddUrlDialog()
        }

        if(::alreadyAvailableNote.isInitialized) {
            layoutDeleteNote.visibility = View.VISIBLE
            layoutDeleteNote.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                showDeleteDialog()
            }
        }

        imageDelete.setOnClickListener {
            imagePath = ""
            imageNote.visibility = View.GONE
            imageDelete.visibility = View.GONE
        }

        imageDeleteUrl.setOnClickListener {
            webLink = ""
            txtWebUrl.visibility = View.GONE
            imageDeleteUrl.visibility = View.GONE
        }
    }

    private fun saveNote() {
        if(inputNoteTitle.text.trim().isNullOrEmpty()) {
            Toast.makeText(this, "Note title can't be empty!", Toast.LENGTH_SHORT).show()
        } else if(inputNoteSubtitle.text.trim().isNullOrEmpty()
            && inputNoteText.text.trim().isNullOrEmpty()) {
            Toast.makeText(this, "Note can't be empty!", Toast.LENGTH_SHORT).show()
        } else {
            webLink = if(txtWebUrl.visibility == View.VISIBLE) {
                txtWebUrl.text.toString()
            } else ""

            val note = Note(
                title = inputNoteTitle.text.toString(),
                dateTime = txtDateTime.text.toString(),
                subtitle = inputNoteSubtitle.text.toString(),
                noteText = inputNoteText.text.toString(),
                imgPath = imagePath,
                color = noteColor,
                webLink = webLink,
            )
            if(::alreadyAvailableNote.isInitialized
                && alreadyAvailableNote != null) {
                note.id = alreadyAvailableNote.id
            }
            viewModel.insert(note)
            closeKeyboard()
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }
    }

    private fun deleteNote(note:Note) {
        viewModel.delete(note)
        val intent = Intent()
        intent.putExtra("isNoteDelete",true)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    private fun setViewOrUpdateNote() {
        inputNoteTitle.setText(alreadyAvailableNote.title)
        inputNoteSubtitle.setText(alreadyAvailableNote.subtitle)
        inputNoteText.setText(alreadyAvailableNote.noteText)

        if(alreadyAvailableNote.imgPath!!.isNotEmpty()) {
            imageNote.visibility = View.VISIBLE
            Glide.with(this).load(alreadyAvailableNote.imgPath)
                .error(R.drawable.ic_done)
                .into(imageNote)
            imagePath = alreadyAvailableNote.imgPath!!
            imageDelete.visibility = View.VISIBLE
        }
        if(alreadyAvailableNote.webLink!!.isNotEmpty()) {
            txtWebUrl.text = alreadyAvailableNote.webLink
            txtWebUrl.visibility = View.VISIBLE
            imageDeleteUrl.visibility = View.VISIBLE
        }
    }

    private fun setSubtitleIndicator() {
        var gradientDrawable= (viewSubtitleIndicator.background as GradientDrawable)
        gradientDrawable.setColor(Color.parseColor(noteColor))
    }

    private fun initMiscellaneous() {
        with(layoutMiscellaneous) {
            imageColorDefault.setOnClickListener {
                noteColor = "#333333"
                imageColorDefault.setImageResource(R.drawable.ic_done)
                imageColorYellow.setImageResource(0)
                imageColorRed.setImageResource(0)
                imageColorBlue.setImageResource(0)
                imageColorBlack.setImageResource(0)
                setSubtitleIndicator()
            }

            imageColorYellow.setOnClickListener {
                noteColor = "#FDBE3B"
                imageColorDefault.setImageResource(0)
                imageColorYellow.setImageResource(R.drawable.ic_done)
                imageColorRed.setImageResource(0)
                imageColorBlue.setImageResource(0)
                imageColorBlack.setImageResource(0)
                setSubtitleIndicator()
            }

            imageColorRed.setOnClickListener {
                noteColor = "#FF4842"
                imageColorDefault.setImageResource(0)
                imageColorYellow.setImageResource(0)
                imageColorRed.setImageResource(R.drawable.ic_done)
                imageColorBlue.setImageResource(0)
                imageColorBlack.setImageResource(0)
                setSubtitleIndicator()
            }

            imageColorBlue.setOnClickListener {
                noteColor = "#3A52FC"
                imageColorDefault.setImageResource(0)
                imageColorYellow.setImageResource(0)
                imageColorRed.setImageResource(0)
                imageColorBlue.setImageResource(R.drawable.ic_done)
                imageColorBlack.setImageResource(0)
                setSubtitleIndicator()
            }

            imageColorBlack.setOnClickListener {
                noteColor = "#000000"
                imageColorDefault.setImageResource(0)
                imageColorYellow.setImageResource(0)
                imageColorRed.setImageResource(0)
                imageColorBlue.setImageResource(0)
                imageColorBlack.setImageResource(R.drawable.ic_done)
                setSubtitleIndicator()
            }
        }

        if(::alreadyAvailableNote.isInitialized
            && alreadyAvailableNote.color!!.isNotEmpty()) {
            when(alreadyAvailableNote.color) {
                "#333333"->layoutMiscellaneous.imageColorDefault.performClick()
                "#FDBE3B"->layoutMiscellaneous.imageColorYellow.performClick()
                "#FF4842"->layoutMiscellaneous.imageColorRed.performClick()
                "#3A52FC"->layoutMiscellaneous.imageColorBlue.performClick()
                "#000000"->layoutMiscellaneous.imageColorBlack.performClick()
            }
        }
    }

    private fun showAddUrlDialog() {
        val builder:AlertDialog.Builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_add_url,
            (findViewById(R.id.layoutAddUrlContainer)))
        builder.setView(view)
        dialogUrlAdd = builder.create()
        if(dialogUrlAdd.window!=null) {
            dialogUrlAdd.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        view.inputUrl.requestFocus()
        view.textAdd.setOnClickListener {
            when {
                view.inputUrl.text.toString().trim().isEmpty() -> {
                    Toast.makeText(this@CreateNoteActivity,
                        "Enter URL", Toast.LENGTH_SHORT).show()
                }
                !Patterns.WEB_URL.matcher(view.inputUrl.text.toString()).matches() -> {
                    Toast.makeText(this@CreateNoteActivity,
                        "Enter valid URL", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    closeKeyboard()
                    txtWebUrl.text = view.inputUrl.text
                    txtWebUrl.visibility = View.VISIBLE
                    dialogUrlAdd.dismiss()
                    imageDeleteUrl.visibility = View.VISIBLE
                }
            }
        }
        view.textCancel.setOnClickListener {
            dialogUrlAdd.dismiss()
        }
        dialogUrlAdd.show()
    }

    private fun showDeleteDialog() {
        val builder:AlertDialog.Builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_delete_note,
            (findViewById(R.id.layoutDeleteNoteContainer)))
        builder.setView(view)
        dialogDelete = builder.create()
        if(dialogDelete.window != null) {
            dialogDelete.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        view.txtDeleteNote.setOnClickListener {
            dialogDelete.dismiss()
            deleteNote(alreadyAvailableNote)
        }
        view.textDeleteCancel.setOnClickListener {
            dialogDelete.dismiss()
        }
        dialogDelete.show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if(intent.resolveActivity(packageManager)!= null) {
            intent.type = "image/*"
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }
    }

    private fun closeKeyboard() {
        if(this.currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_CODE -> {
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
            imageNote.visibility = View.VISIBLE
            Glide.with(this).load(data!!.data)
                .into(imageNote)
            imagePath = getRealPathFromURI(this, data.data!!)!!
            imageDelete.visibility = View.VISIBLE
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