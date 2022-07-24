package com.app.payseracurrencyexchange.ui.customViews

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.payseracurrencyexchange.R
import com.google.android.material.textview.MaterialTextView

class CustomTextView @JvmOverloads constructor(
    context: Context, @Nullable attrs: AttributeSet
) : ConstraintLayout(context, attrs){

    private var view : View = LayoutInflater.from(context).inflate(R.layout.custom_textview_layout, this, true)

    private var labelTextView: MaterialTextView? = null
    private var icon: ImageView? = null

    private var iconDrawable : Drawable? = null
    private var label: String? = null



    init {
        icon = view.findViewById(R.id.imageIcon) as ImageView
        labelTextView = view.findViewById(R.id.titleTextView) as MaterialTextView
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomTextView, 0, 0)

        with(attributes){
            iconDrawable = getDrawable(R.styleable.CustomTextView_setIcon)
            label = getString(R.styleable.CustomTextView_setLabel)

            icon?.setImageDrawable(iconDrawable)
            labelTextView?.text = label
        }

    }

    fun setImageDrawable(drawable: Drawable?) {
        icon?.setImageDrawable(drawable)
    }
    fun getImageDrawable() : Drawable? {
        return iconDrawable
    }

    fun setTitle(text: String?) {
        labelTextView?.text = text
    }
    fun getTitle() : String? {
        return label
    }

}