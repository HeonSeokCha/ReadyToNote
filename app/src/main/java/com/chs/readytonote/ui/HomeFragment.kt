package com.chs.readytonote.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.chs.readytonote.R
import com.chs.readytonote.adapter.NoteAdapter
import com.chs.readytonote.databinding.FragmentHomeBinding
import com.chs.readytonote.databinding.FragmentNoteBinding
import com.chs.readytonote.entities.Note

class HomeFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private var _binding: FragmentHomeBinding? = null
    private var notesAdapter: NoteAdapter? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = MainViewModel(requireActivity().application)
        viewModel.getAllNotes()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentHomeBinding.bind(inflater.inflate(R.layout.fragment_home, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
        initRecyclerView()
        initObserver()
    }

    private fun initClick() {
        binding.imgAddNoteMain.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment()
            findNavController().navigate(action)
        }
    }

    private fun initRecyclerView() {
        binding.RvNotes.apply {
            notesAdapter = NoteAdapter(object : NoteAdapter.ClickListener {
                override fun clickListener(note: Note, position: Int) {
                    TODO("Not yet implemented")
                }

                override fun checkClickListener(checkList: MutableMap<Int, Note>) {
                    TODO("Not yet implemented")
                }

                override fun longClickListener(chkState: Boolean) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun initObserver() {
        viewModel.noteLiveData.observe(viewLifecycleOwner, {
            notesAdapter?.submitList(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        notesAdapter = null
        _binding = null
    }
}