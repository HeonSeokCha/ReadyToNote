package com.chs.readytonote.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chs.readytonote.util.Constants
import com.chs.readytonote.R
import com.chs.readytonote.util.Util.dataStore
import com.chs.readytonote.adapter.NoteAdapter
import com.chs.readytonote.databinding.FragmentHomeBinding
import com.chs.readytonote.databinding.LayoutThemeSelectBinding
import com.chs.readytonote.entities.Note
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import kotlin.concurrent.timer

class HomeFragment : Fragment() {
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainViewModel(requireActivity().application) as T
            }
        }
    }
    private var _binding: FragmentHomeBinding? = null
    private var notesAdapter: NoteAdapter? = null
    private val editTextLiveData: MutableLiveData<String> = MutableLiveData()
    private lateinit var callback: OnBackPressedCallback
    private lateinit var dialogTheme: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAllNote()
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        }
//        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_binding == null) {
            _binding =
                FragmentHomeBinding.bind(inflater.inflate(R.layout.fragment_home, container, false))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClick()
        initRecyclerView()
        initObserver()
    }

    private fun initView() {
        binding.inputSearch.doAfterTextChanged {
            viewModel.searchNotes(it!!.trim().toString())
        }
    }

    private fun initClick() {
        binding.imgAddNoteMain.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment(null)
            findNavController().navigate(action)
            binding.inputSearch.text.clear()
        }

        binding.btnSelectTheme.setOnClickListener {
            showThemeDialog()
        }
    }

    private fun initRecyclerView() {
        binding.RvNotes.apply {
            notesAdapter = NoteAdapter(object : NoteAdapter.ClickListener {
                override fun clickListener(note: Note, position: Int) {
                    val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment(note)
                    findNavController().navigate(action)
                }

                override fun checkClickListener(checkList: MutableMap<Int, Note>) {
                    TODO("Not yet implemented")
                }

                override fun longClickListener(chkState: Boolean) {
                    TODO("Not yet implemented")
                }
            })
            this.layoutManager = StaggeredGridLayoutManager(2, 1)
            this.adapter = notesAdapter
        }
    }

    private fun initObserver() {
        viewModel.noteLiveData.observe(viewLifecycleOwner, {
            binding.layoutEmptyNote.root.isVisible = it.isEmpty()
            notesAdapter?.submitList(it)
        })
    }

    private fun showThemeDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val dialogView = LayoutThemeSelectBinding.inflate(LayoutInflater.from(requireContext()))
        builder.setView(dialogView.root)
        dialogTheme = builder.create()
        if (dialogTheme.window != null) {
            dialogTheme.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        when (viewModel.selectUI) {
            Constants.WHITE_MODE -> dialogView.rdoWhite.isChecked = true
            Constants.DARK_MODE -> dialogView.rdoDark.isChecked = true
            Constants.DEFAULT_MODE -> dialogView.rdoDefault.isChecked = true
        }

        dialogView.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rdo_white -> {
                    updateTheme(Constants.WHITE_MODE)
                }
                R.id.rdo_dark -> {
                    updateTheme(Constants.DARK_MODE)
                }
                R.id.rdo_default -> {
                    updateTheme(Constants.DEFAULT_MODE)
                }
            }
        }
        dialogView.btnOk.setOnClickListener {
            dialogTheme.dismiss()
        }
        dialogView.btnCancel.setOnClickListener {
            dialogTheme.dismiss()
        }
        dialogTheme.show()
    }

    private fun updateTheme(value: String) {
        lifecycleScope.launch {
            requireActivity().dataStore.edit {
                it[stringPreferencesKey(Constants.UI_STATUS)] = value
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        notesAdapter = null
        _binding = null
        Log.e("HomeFragment", "onDestroyView")
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}