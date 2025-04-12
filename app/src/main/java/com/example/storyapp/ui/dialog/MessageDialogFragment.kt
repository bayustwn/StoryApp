package com.example.storyapp.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentMessageDialogBinding

class MessageDialogFragment : DialogFragment() {

    private var _binding:FragmentMessageDialogBinding?=null
    private val binding get() = _binding!!

    var onCloseListener: (()->Unit)? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageDialogBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        arguments?.let {
            binding.icon.setImageResource(if (it.getBoolean("isError")) R.drawable.warning else R.drawable.success)
            binding.message.text = it.getString("message")
            binding.close.text = it.getString("close") ?: getString(R.string.close)
        }

        binding.close.setOnClickListener {
            onCloseListener?.invoke() ?: dismiss()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding =null
    }

}