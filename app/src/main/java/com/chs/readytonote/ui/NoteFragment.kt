package com.chs.readytonote.ui

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.chs.readytonote.R
import com.chs.readytonote.Util
import com.chs.readytonote.databinding.FragmentNoteBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class NoteFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {

        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    val requestActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.data != null) {
            Glide.with(this).load(activityResult.data).into(binding.imageNote)
            binding.imageNote.isVisible = true
            binding.imageDelete.isVisible = true
            Util.getRealPathFromURI(requireContext(), activityResult!!.data!!.data!!)!!
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        // action to do something
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initView() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutMiscellaneous.root)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}