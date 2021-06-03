package edu.kylegilmartin.shopapp.widgets

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import java.util.jar.Attributes

class MSPBoldTextView(context: Context, attributeSet: AttributeSet) : AppCompatTextView(context,attributeSet) {
init {
applyFont()
}
    private fun applyFont(){
        val boldTypeFace: Typeface =
                Typeface.createFromAsset(context.assets,"Montserrat-Bold.ttf")
        typeface = boldTypeFace
    }
}