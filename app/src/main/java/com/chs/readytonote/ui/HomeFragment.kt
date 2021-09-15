package com.chs.readytonote.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
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
        if (_binding == null) {
            _binding =
                FragmentHomeBinding.bind(inflater.inflate(R.layout.fragment_home, container, false))
        }
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
            val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment(null)
            findNavController().navigate(action)
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
            Log.e("NoteList", it.toString())
            notesAdapter?.submitList(it.toMutableList())
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        notesAdapter = null
        _binding = null
        Log.e("HomeFragment", "onDestroyView")
    }
}