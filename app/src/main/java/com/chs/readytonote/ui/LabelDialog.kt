package com.chs.readytonote.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chs.readytonote.adapter.LabelAdapter
import com.chs.readytonote.databinding.LayoutLabelBinding
import com.chs.readytonote.entities.Label

class LabelDialog(
    private val labelListener: (label: String) -> Unit
) : DialogFragment() {
    private val viewModel by activityViewModels<MainViewModel>()
    private var _binding: LayoutLabelBinding? = null
    private lateinit var selectLabel: Label
    private val binding get() = _binding!!

    private lateinit var labelAdapter: LabelAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutLabelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
        initRecyclerView()
        initObserver()
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes.apply {
            this.width = WindowManager.LayoutParams.MATCH_PARENT
            this.height = WindowManager.LayoutParams.WRAP_CONTENT
        }
        this.isCancelable = false
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }

    private fun initObserver() {
        viewModel.getAllLabel().observe(viewLifecycleOwner, {
            labelAdapter.submitList(it)
        })
    }

    private fun initClick() {
        binding.textAdd.setOnClickListener {
            labelListener.invoke(selectLabel.title ?: "")
        }

        binding.textCancel.setOnClickListener {
            this.dismiss()
        }
    }

    private fun initRecyclerView() {
        binding.RvLabel.apply {
            labelAdapter = LabelAdapter(object : LabelAdapter.LabelClickListener {
                override fun clickListener(label: Label) {
                    selectLabel = label
                }

                override fun addClickListener(title: String) {
                    TODO("Not yet implemented")
                }
            })
            this.adapter = labelAdapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}