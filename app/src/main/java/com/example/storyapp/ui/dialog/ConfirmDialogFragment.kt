package com.example.storyapp.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentConfirmDialogBinding

class ConfirmDialogFragment : DialogFragment() {

    private var _binding:FragmentConfirmDialogBinding?=null
    private val binding get ()= _binding!!

    var onClickListener: (() -> Unit)? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmDialogBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false
        arguments?.let {
            binding.icon.setImageResource(it.getInt("icon"))
            binding.message.text = it.getString("message")
            binding.okay.text = it.getString("positive") ?: getString(R.string.close)
        }

        binding.cancel.setOnClickListener{
            dismiss()
        }

        binding.okay.setOnClickListener {
            onClickListener?.invoke()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}