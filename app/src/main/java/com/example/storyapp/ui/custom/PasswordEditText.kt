package com.example.storyapp.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.storyapp.R

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var passwordIcon: Drawable? = null
    private var isPasswordVisible: Boolean = false
    private var eyeOpen: Drawable? = null

    init {

        passwordIcon = ContextCompat.getDrawable(context, R.drawable.password)
        background = ContextCompat.getDrawable(context, R.drawable.edit_text_shape)
        eyeOpen = ContextCompat.getDrawable(context, R.drawable.eye_open)

        transformationMethod = PasswordTransformationMethod.getInstance()
        setCompoundDrawablesRelativeWithIntrinsicBounds(passwordIcon, null, eyeOpen, null)

        fontFamily()
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(password: Editable?) {
                isValid(password.toString())
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val black = ContextCompat.getColor(context,R.color.black)

        textSize = 18f
        hint = context.getString(R.string.password)
        maxLines = 1
        setTextColor(black)
        compoundDrawablePadding = 15

    }

    private fun isValid(pass: String) {
        if (pass.isNotEmpty()){
            error = if (pass.length < 8) {
                context.getString(R.string.password_error)
            }else{
                null
            }
        }
    }

    private fun fontFamily(){
        typeface = ResourcesCompat.getFont(context,R.font.poppins)
    }

    private fun togglePassword(){
        isPasswordVisible = !isPasswordVisible

        transformationMethod = if (isPasswordVisible) {
            null
        } else {
            PasswordTransformationMethod.getInstance()
        }

        setSelection(text?.length ?: 0)
        invalidate()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
            val eye = compoundDrawablesRelative[2]
            if (error == null){
                if (eye != null && event.x >= (width - paddingEnd - eye.intrinsicWidth) &&
                    event.x <= (width - paddingEnd)) {
                    when(event.action){
                        MotionEvent.ACTION_UP->{
                            togglePassword()
                        }
                        MotionEvent.ACTION_DOWN->{
                            togglePassword()
                        }
                    }
                    return true
                }
            }
        return super.onTouchEvent(event)
    }

}