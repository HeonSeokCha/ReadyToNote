package com.chs.readytonote.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chs.readytonote.R
import com.chs.readytonote.adapter.LabelAdapter
import com.chs.readytonote.databinding.LayoutLabelBinding
import com.chs.readytonote.entities.Label

class LabelDialog(
    private val labelListener: (label: String) -> Unit
) : DialogFragment() {
    private val viewModel by activityViewModels<MainViewModel>()
    private var _binding: LayoutLabelBinding? = null
    private var addLabelTitle: String = ""
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
        viewModel.getAllLabel()
        initView()
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
        viewModel.labelLiveData.observe(this, {
            labelAdapter.submitList(it)
            binding.layoutAddLabel.isVisible = it.isEmpty()
        })
    }

    private fun initView() {
        binding.inputLabel.doAfterTextChanged {
            viewModel.searchLabel(it?.trim().toString())
            binding.txtAddLabelTitle.text = "'${it?.trim()}' 라벨 만들기"
            addLabelTitle = it?.trim().toString()
        }
    }

    private fun initClick() {
        binding.textAdd.setOnClickListener {
            labelListener.invoke(selectLabel.title ?: "")
        }

        binding.textCancel.setOnClickListener {
            this.dismiss()
            addLabelTitle = ""
        }

        binding.layoutAddLabel.setOnClickListener {
            viewModel.insertLabel(Label(addLabelTitle))
            binding.inputLabel.text.clear()
        }
    }

    private fun initRecyclerView() {
        binding.RvLabel.apply {
            labelAdapter = LabelAdapter(object : LabelAdapter.LabelClickListener {
                override fun clickListener(label: Label) {
                    selectLabel = label
                }

                override fun addClickListener(title: String) {
                    viewModel.insertLabel(Label(title))
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