package com.chs.readytonote.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chs.readytonote.adapter.LabelAdapter
import com.chs.readytonote.databinding.LayoutLabelBinding
import com.chs.readytonote.entities.Label

class LabelDialog(
    noteLabelTitle: String?,
    private val labelListener: (labelTitle: String?) -> Unit
) : DialogFragment() {
    private val viewModel by activityViewModels<MainViewModel>()
    private var _binding: LayoutLabelBinding? = null
    private var addLabelTitle: String = ""
    private var selectLabelList: String? = noteLabelTitle
    private val binding get() = _binding!!

    private var labelAdapter: LabelAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAllLabel()
    }

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
        viewModel.labelLiveData.observe(this, { labelList ->
            if (selectLabelList != null) {
                labelList[labelList.indexOfFirst { it.title == selectLabelList!! }].checked = true
            }
            labelAdapter?.submitList(labelList)
            binding.layoutAddLabel.isVisible =
                binding.inputLabel.text.isNotBlank() && labelList.isEmpty()
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
            //todo 클릭한 아이템이 없으면 비활성화
            labelListener.invoke(selectLabelList)
            this.dismiss()
        }

        binding.textCancel.setOnClickListener {
            this.dismiss()
        }

        binding.layoutAddLabel.setOnClickListener {
            viewModel.insertLabel(Label(addLabelTitle))
            binding.inputLabel.text.clear()
        }
    }

    private fun initRecyclerView() {
        binding.RvLabel.apply {
            labelAdapter = LabelAdapter { title, checked ->
                selectLabelList = if (checked) {
                    title
                } else {
                    null
                }
            }

            this.adapter = labelAdapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        labelAdapter = null
    }
}