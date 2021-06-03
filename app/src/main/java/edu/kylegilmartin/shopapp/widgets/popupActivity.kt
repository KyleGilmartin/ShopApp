package edu.kylegilmartin.shopapp.widgets

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import edu.kylegilmartin.shopapp.R
import kotlinx.android.synthetic.main.dialog_progress.*

open class popupActivity : AppCompatActivity() {

     private lateinit var mProgressDialog: Dialog

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

     fun showProgressDialog(text:String){
         mProgressDialog = Dialog(this)

         mProgressDialog.setContentView(R.layout.dialog_progress)
         mProgressDialog.tv_progress_text.text = text
         mProgressDialog.setCancelable(false)
         mProgressDialog.setCanceledOnTouchOutside(false)
         mProgressDialog.show()
     }
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }
}