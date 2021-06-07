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


    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar =
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                            this,
                            R.color.colorSnackBarError
                    )
            )
        }else{
            snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                            this,
                            R.color.colorSnackBarSuccess
                    )
            )
        }
        snackBar.show()
    }


    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.tv_progress_text.text = text

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }


    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}