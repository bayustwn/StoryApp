package com.example.storyapp.utils

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.storyapp.ui.dialog.ConfirmDialogFragment
import com.example.storyapp.ui.dialog.MessageDialogFragment

object Dialog {

    fun messageDialog(fragmentManager: FragmentManager,close:String?=null,message:String,isError:Boolean, isOnCloselistener: (()->Unit)?=null){
        val bundle = Bundle()
        bundle.putString("close",close)
        bundle.putString("message",message)
        bundle.putBoolean("isError",isError)
        val dialog = MessageDialogFragment()
        dialog.arguments = bundle
        isOnCloselistener?.let {
            dialog.onCloseListener = {
                it.invoke()
            }
        }
        dialog.show(fragmentManager,dialog::class.java.simpleName)
    }

    fun confirmDialog(fragmentManager: FragmentManager,positive: String?=null,message: String,icon: Int, isOnClicklistener: (()->Unit)?=null){
        val bundle = Bundle()
        bundle.putString("positive",positive)
        bundle.putString("message",message)
        bundle.putInt("icon",icon)
        val dialog = ConfirmDialogFragment()
        dialog.arguments = bundle
        isOnClicklistener?.let {
            dialog.onClickListener = {
                it.invoke()
            }
        }
        dialog.show(fragmentManager, dialog::class.java.simpleName)
    }

}