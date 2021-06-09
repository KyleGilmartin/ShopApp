package edu.kylegilmartin.shopapp.LoginRegister

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
import edu.kylegilmartin.shopapp.ui.activities.MainActivity
import edu.kylegilmartin.shopapp.R
import edu.kylegilmartin.shopapp.firestore.FirebaseClass
import edu.kylegilmartin.shopapp.models.User
import edu.kylegilmartin.shopapp.ui.activities.DashboardActivity
import edu.kylegilmartin.shopapp.widgets.Constants
import edu.kylegilmartin.shopapp.widgets.GlideLoader
import edu.kylegilmartin.shopapp.widgets.popupActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.activity_user_profile.et_email
import kotlinx.android.synthetic.main.activity_user_profile.et_first_name
import kotlinx.android.synthetic.main.activity_user_profile.et_last_name
import kotlinx.android.synthetic.main.activity_user_profile.iv_user_photo
import java.io.IOException

class UserProfileActivity : popupActivity(), View.OnClickListener {


    // Instance of User data model class. We will initialize it later on.
    private lateinit var mUserDetails: User
    private var mSelectedImageUri:Uri? = null
    private var mUserProfileImageURL:String = ""


    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_user_profile)


        // Create a instance of the User model class.
        /*var userDetails: User = User()*/


        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            // Get the user details from intent as a ParcelableExtra.
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        if(mUserDetails.profileCompleted == Constants.USER_PROFILE_INCOMPLETE_CODE){
            tv_title_name.text = resources.getString(R.string.title_complete_profile)

            et_first_name.setText(mUserDetails.firstName)
            et_last_name.setText(mUserDetails.lastName)
            et_email.isEnabled = false
            et_email.setText(mUserDetails.email)

        }else{
            setupActionBar()
            tv_title_name.text = resources.getString(R.string.title_edit_profile)
            GlideLoader(this).loadUserPicture(mUserDetails.image,iv_user_photo)
            et_first_name.setText(mUserDetails.firstName)
            et_last_name.setText(mUserDetails.lastName)
            et_email.isEnabled = false
            et_email.setText(mUserDetails.email)

            if(mUserDetails.mobile != 0L){
                et_mobile_number.setText(mUserDetails.mobile.toString())
            }
            if(mUserDetails.gender == Constants.MALE){
                rb_male.isChecked = true
            }else{
                rb_female.isChecked = true
            }

        }

        //the some of the edittext components are disabled because it is added at a time of Registration.

        et_first_name.setText(mUserDetails.firstName)
        et_last_name.setText(mUserDetails.lastName)
        et_email.isEnabled = false
        et_email.setText(mUserDetails.email)

        // Assign the on click event to the user profile photo.
        iv_user_photo.setOnClickListener(this@UserProfileActivity)

        // Assign the on click event to the SAVE button.
        btn_submit.setOnClickListener(this@UserProfileActivity)
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_user_profile_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }
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

                        showProgressDialog(resources.getString(R.string.please_wait))

                        if(mSelectedImageUri != null){

                            FirebaseClass().uploadImageToCloudStorage(this,mSelectedImageUri)
                        }else{
                            updateUserProfileDetails()
                        }

                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails(){
        val userHashMap = HashMap<String, Any>()

        // Here the field which are not editable needs no update. So, we will update user Mobile Number and Gender for now.

        // Here we get the text from editText and trim the space
        val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }

        val firstName = et_first_name.text.toString().trim{it <= ' '}
        if(firstName != mUserDetails.firstName){
            userHashMap[Constants.FIRSTNAME] = firstName
        }
        val lastName = et_last_name.text.toString().trim{it <= ' '}
        if(firstName != mUserDetails.lastName){
            userHashMap[Constants.LASTNAME] = lastName
        }


        val gender = if (rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if(mUserProfileImageURL.isNotEmpty()){
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if(gender.isNotEmpty() && gender != mUserDetails.gender){
            userHashMap[Constants.GENDER] = gender
        }

        userHashMap[Constants.GENDER] = gender

        userHashMap[Constants.COMPLETE_PROFILE] = Constants.USER_PROFILE_COMPLETE_CODE

        /*showErrorSnackBar("Your details are valid. You can update them.", false)*/

        // Show the progress dialog.
       // showProgressDialog(resources.getString(R.string.please_wait))

        // call the registerUser function of FireStore class to make an entry in the database.
        FirebaseClass().updateUserProfileData(
                this@UserProfileActivity,
                userHashMap
        )
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

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        // The uri of selected image from phone storage.
                        mSelectedImageUri = data.data!!

                        GlideLoader(this@UserProfileActivity).loadUserPicture(
                                mSelectedImageUri!!,
                                iv_user_photo
                        )
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

    /**
     * A function to validate the input entries for profile details.
     */
    private fun validateUserProfileDetails(): Boolean {
        return when {

            // We have kept the user profile picture is optional.
            // The FirstName, LastName, and Email Id are not editable when they come from the login screen.
            // The Radio button for Gender always has the default selected value.

            // Check if the mobile number is not empty as it is mandatory to enter.
            TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }
    }


    /**
     * A function to notify the success result and proceed further accordingly after updating the user details.
     */
    fun userProfileUpdateSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
                this@UserProfileActivity,
                resources.getString(R.string.msg_profile_update_success),
                Toast.LENGTH_SHORT
        ).show()


        // Redirect to the Main Screen after profile completion.
        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }

    fun imageUploadSuccess(imageURL:String){
       // hideProgressDialog()
        //Toast.makeText(this,"Image Uploaded success",Toast.LENGTH_SHORT).show()

        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }

}