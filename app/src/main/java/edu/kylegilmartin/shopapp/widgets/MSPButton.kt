package edu.kylegilmartin.shopapp.widgets

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class MSPButton(context: Context, attributeSet: AttributeSet) : AppCompatButton(context,attributeSet) {
    init {
        applyFont()
    }
    private fun applyFont(){
        val boldTypeFace: Typeface =
            Typeface.createFromAsset(context.assets,"Montserrat-Bold.ttf")
        typeface = boldTypeFace
    }
}