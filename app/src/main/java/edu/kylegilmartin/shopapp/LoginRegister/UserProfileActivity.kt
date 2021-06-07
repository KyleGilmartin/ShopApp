package edu.kylegilmartin.shopapp.LoginRegister

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import edu.kylegilmartin.shopapp.MainActivity
import edu.kylegilmartin.shopapp.R
import edu.kylegilmartin.shopapp.firestore.FirebaseClass
import edu.kylegilmartin.shopapp.models.User
import edu.kylegilmartin.shopapp.widgets.Constants
import edu.kylegilmartin.shopapp.widgets.GlideLoader
import edu.kylegilmartin.shopapp.widgets.popupActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.activity_user_profile.et_email
import kotlinx.android.synthetic.main.activity_user_profile.et_first_name
import kotlinx.android.synthetic.main.activity_user_profile.et_last_name
import java.io.IOException

class UserProfileActivity : popupActivity(), View.OnClickListener {

  private lateinit var mUserDetails: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)


        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }


        et_first_name.setText(mUserDetails.firstName)
        et_last_name.setText(mUserDetails.lastName)
        et_email.isEnabled =false
        et_email.setText(mUserDetails.email)

        iv_user_photo.setOnClickListener(this@UserProfileActivity)
        btn_submit.setOnClickListener(this@UserProfileActivity)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.iv_user_photo -> {

                    if (ContextCompat.checkSelfPermission(
                                    this,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            == PackageManager.PERMISSION_GRANTED
                    ) {


                        Constants.showImageChoosen(this@UserProfileActivity)

                    } else {
                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/
                        ActivityCompat.requestPermissions(
                                this,
                                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                                Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_submit -> {
                    if (validateUserProfileDetails()) {
                        val userHashMap = HashMap<String, Any>()
                        val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }

                        val gender = if (rb_male.isChecked) {
                            Constants.MALE
                        } else {
                            Constants.FEMALE
                        }
                        if(mobileNumber.isEmpty()){
                            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
                        }
                        userHashMap[Constants.GENDER] = gender
                        userHashMap[Constants.MOBILE] = mobileNumber
                        userHashMap[Constants.FIRSTNAME] = et_first_name.text.toString().trim { it <= ' ' }
                        userHashMap[Constants.LASTNAME] = et_last_name.text.toString().trim { it <= ' ' }
                        showProgressDialog(resources.getString(R.string.please_wait))

                        FirebaseClass().updateUserProfileDetails(this,userHashMap)
                        //showErrorSnackBar("Your details are valid. you can update them",false)
                    }
                }
            }
        }
    }

    fun userProfileUpdateSuccess(){
        hideProgressDialog()

        Toast.makeText(this,resources.getString(R.string.msg_profile_update_success),Toast.LENGTH_SHORT).show()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }


    /**
     * This function will identify the result of runtime permission after the user allows or deny permission based on the unique code.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                /*showErrorSnackBar("The storage permission is granted.",false)*/

                Constants.showImageChoosen(this@UserProfileActivity)

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
                    try {
                        // The uri of selected image from phone storage.
                        val selectedImageFileUri = data.data!!

                       // iv_user_photo.setImageURI(Uri.parse(selectedImageFileUri.toString()))
                        GlideLoader(this).loadUserPicture(selectedImageFileUri,iv_user_photo)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                                this@UserProfileActivity,
                                resources.getString(R.string.image_selection_failed),
                                Toast.LENGTH_SHORT
                        )
                                .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateUserProfileDetails():Boolean{
        return when{
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
             TextUtils.isEmpty(et_mobile_number.text.toString().trim{it <= ' '}) -> {
              showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number),true)
              false
        }else ->{
            true
        }
        }
    }


}