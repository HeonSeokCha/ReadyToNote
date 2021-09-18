package com.chs.readytonote.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.MediaStore import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import coil.load
import com.chs.readytonote.Constants
import com.chs.readytonote.R
import com.chs.readytonote.Util
import com.chs.readytonote.databinding.FragmentNoteBinding
import com.chs.readytonote.entities.Note
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class NoteFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private var imgPath: String = ""
    private var webLink: String = ""
    private var noteColor: String = Constants.NOTE_DEFAULT_COLOR
    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                    type = "image/*"
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
            requestActivity.launch(intent)
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK && activityResult.data != null) {
            binding.imageNote.isVisible = true
            binding.imageDelete.isVisible = true
            imgPath = Util.getRealPathFromURI(requireContext(), activityResult!!.data!!.data!!)!!
            binding.imageNote.load(File(imgPath))
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = MainViewModel(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_binding == null) {
            _binding =
                FragmentNoteBinding.bind(inflater.inflate(R.layout.fragment_note, container, false))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClick()
    }

    private fun initView() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutMiscellaneous.root)
        binding.txtDateTime.text =
            SimpleDateFormat(Constants.DATE_PATTERN, Locale.KOREA).format(Date())

        if (NoteFragmentArgs.fromBundle(requireArguments()).note != null) {
            with(NoteFragmentArgs.fromBundle(requireArguments()).note) {
                binding.model = this
                imgPath = this!!.imgPath.toString()
                noteColor = this.color!!
                binding.layoutMiscellaneous.radioGroup2.check(
                    binding.layoutMiscellaneous.radioGroup2.getChildAt(
                        Constants.NOTE_COLOR_LIST.indexOf(noteColor)
                    ).id
                )
            }
            binding.layoutMiscellaneous.layoutDeleteNote.isVisible = true
        }
    }

    private fun initClick() {
        binding.layoutMiscellaneous.textMiscellaneous.setOnClickListener {
            bottomSheetBehavior.apply {
                if (this.state != BottomSheetBehavior.STATE_EXPANDED) {
                    this.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    this.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }

        binding.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.imgSave.setOnClickListener {
            saveNote()
            findNavController().navigateUp()
        }

        binding.layoutMiscellaneous.layoutAddImage.setOnClickListener {
            requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        binding.layoutMiscellaneous.layoutDeleteNote.setOnClickListener {
            deleteNote()
        }

        binding.imageDelete.setOnClickListener {
            imgPath = ""
            binding.imageNote.isVisible = false
            binding.imageDelete.isVisible = false
        }

        binding.imageDeleteUrl.setOnClickListener {
            webLink = ""
            binding.txtWebUrl.isVisible = false
            binding.imageDeleteUrl.isVisible = false
        }

        binding.layoutMiscellaneous.rdoNoteDefault.setOnClickListener {
            noteColor = Constants.NOTE_DEFAULT_COLOR
            setIndicatorColor(noteColor)
        }

        binding.layoutMiscellaneous.rdoNoteYellow.setOnClickListener {
            noteColor = Constants.NOTE_YELLOW_COLOR
            setIndicatorColor(noteColor)
        }
        binding.layoutMiscellaneous.rdoNoteRed.setOnClickListener {
            noteColor = Constants.NOTE_RED_COLOR
            setIndicatorColor(noteColor)
        }
        binding.layoutMiscellaneous.rdoNoteBlue.setOnClickListener {
            noteColor = Constants.NOTE_BLUE_COLOR
            setIndicatorColor(noteColor)
        }
        binding.layoutMiscellaneous.rdoNotePurple.setOnClickListener {
            noteColor = Constants.NOTE_PURPLE_COLOR
            setIndicatorColor(noteColor)
        }
    }

    @SuppressLint("ResourceType")
    private fun setIndicatorColor(noteColor: String) {
        if (noteColor == Constants.NOTE_DEFAULT_COLOR) {
            (binding.viewSubtitleIndicator.background as GradientDrawable).setColor(
                binding.viewSubtitleIndicator.context.getColor(R.color.colorNoteDefaultColor)
            )
        } else {
            (binding.viewSubtitleIndicator.background as GradientDrawable).setColor(
                Color.parseColor(noteColor)
            )
        }
    }

    private fun saveNote() {
        val note: Note = Note(
            binding.inputNoteTitle.text.trim().toString(),
            "",
            binding.txtDateTime.text.trim().toString(),
            binding.inputNoteSubtitle.text.trim().toString(),
            binding.inputNoteText.text.trim().toString(),
            imgPath,
            noteColor,
            ""
        )
        if (NoteFragmentArgs.fromBundle(requireArguments()).note != null) {
            note.id = NoteFragmentArgs.fromBundle(requireArguments()).note!!.id
        }
        viewModel.insertNote(note)
    }

    private fun deleteNote() {
        if (NoteFragmentArgs.fromBundle(requireArguments()).note != null) {
            viewModel.deleteNote(NoteFragmentArgs.fromBundle(requireArguments()).note!!)
            findNavController().navigateUp()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}