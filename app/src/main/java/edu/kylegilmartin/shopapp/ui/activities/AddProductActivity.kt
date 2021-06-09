package edu.kylegilmartin.shopapp.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import edu.kylegilmartin.shopapp.R
import edu.kylegilmartin.shopapp.firestore.FirebaseClass
import edu.kylegilmartin.shopapp.widgets.Constants
import edu.kylegilmartin.shopapp.widgets.GlideLoader
import edu.kylegilmartin.shopapp.widgets.popupActivity
import kotlinx.android.synthetic.main.activity_add_product.*
import java.io.IOException

class AddProductActivity : popupActivity() , View.OnClickListener{
    private var mSelectedImageFileURI: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        setupActionBar()
        iv_add_update_product.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_add_product_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_add_product_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_add_update_product -> {
                    if (ContextCompat.checkSelfPermission(
                                    this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChoosen(this)
                    } else {
                        ActivityCompat.requestPermissions(
                                this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                                Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit ->{
                    if(validateProductDetails()){
                        uploadProductImage()
                    }
                }
            }
        }

    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChoosen(this)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                        this,
                        resources.getString(R.string.read_storage_permission_denied),
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                   iv_add_update_product.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit))

                   mSelectedImageFileURI = data.data!!

                    try {
                        GlideLoader(this).loadUserPicture(mSelectedImageFileURI!!,iv_product_image)
                    }catch (e: IOException){
                        e.printStackTrace()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateProductDetails():Boolean{
        return when{
            mSelectedImageFileURI == null ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image),true)
                false
            }

            TextUtils.isEmpty(et_product_title.text.toString().trim{it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title),true)
                false
            }
            TextUtils.isEmpty(et_product_price.text.toString().trim{it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price),true)
                false
            }
            TextUtils.isEmpty(et_product_description.text.toString().trim{it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_description),true)
                false
            }
            TextUtils.isEmpty(et_product_quantity.text.toString().trim{it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_quantity),true)
                false
            }
            else -> {
                true
            }

        }
    }

    private fun uploadProductImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirebaseClass().uploadImageToCloudStorage(this,mSelectedImageFileURI,Constants.PRODUCT_IMAGE)
    }

    fun imageUploadSuccess(imageURL:String){
         hideProgressDialog()
        //Toast.makeText(this,"Image Uploaded success",Toast.LENGTH_SHORT).show()

       showErrorSnackBar("image uploaded",false)
    }




}