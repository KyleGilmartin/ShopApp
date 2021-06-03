package edu.kylegilmartin.shopapp.widgets

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import edu.kylegilmartin.shopapp.R

 open class popupActivity : AppCompatActivity() {

    fun showErrorSnackBar(message: String,errorMessage: Boolean){
        val snackbar =
            Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view

        if(errorMessage){
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorSnackBarError
                )
            )
        }else{
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorSnackBarSuccess
                )
            )

        }
        snackbar.show()
    }
}