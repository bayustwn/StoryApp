package com.example.storyapp.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.storyapp.R

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var emailIcon: Drawable? = null

    init {
        emailIcon = ContextCompat.getDrawable(context,R.drawable.email)
        background = ContextCompat.getDrawable(context,R.drawable.edit_text_shape)
        setCompoundDrawablesRelativeWithIntrinsicBounds(emailIcon,null,null,null)

        isSingleLine = true
        maxLines = 1

        fontFamily()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(email: Editable?) {
                isEmail(email.toString())
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val black = ContextCompat.getColor(context,R.color.black)
        textSize = 18f
        setTextColor(black)
        compoundDrawablePadding = 15
        hint = context.getString(R.string.email)

    }

    private fun fontFamily(){
        typeface = ResourcesCompat.getFont(context,R.font.poppins)
    }

    private fun isEmail(email:String){
        if (email.isNotEmpty()){
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                error = context.getString(R.string.valid_email)
            }
        }
    }

}