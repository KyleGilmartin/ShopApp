package edu.kylegilmartin.shopapp.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import edu.kylegilmartin.shopapp.LoginRegister.LoginActivity
import edu.kylegilmartin.shopapp.LoginRegister.RegisterActivity
import edu.kylegilmartin.shopapp.LoginRegister.UserProfileActivity
import edu.kylegilmartin.shopapp.models.User
import edu.kylegilmartin.shopapp.widgets.Constants
import edu.kylegilmartin.shopapp.widgets.popupActivity
import java.net.URI
import java.net.URL

class FirebaseClass {


    private val mFireStore = FirebaseFirestore.getInstance()


    fun registerUser(activity: RegisterActivity, userInfo: User) {

        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        mFireStore.collection(Constants.USERS)
                // Document ID for users fields. Here the document it is the User ID.
                .document(userInfo.id)
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                    activity.userRegistrationSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while registering the user.",
                            e
                    )
                }
    }

    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
                // The document id to get the Fields of user.
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->

                    Log.i(activity.javaClass.simpleName, document.toString())

                    // Here we have received the document snapshot which is converted into the User Data model object.
                    val user = document.toObject(User::class.java)!!

                    val sharedPreferences =
                            activity.getSharedPreferences(
                                    Constants.SHOP_PREFERENCES,
                                    Context.MODE_PRIVATE
                            )

                    // Create an instance of the editor which is help us to edit the SharedPreference.
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString(
                            Constants.LOGGED_IN_USERNAME,
                            "${user.firstName} ${user.lastName}"
                    )
                    editor.apply()

                    when (activity) {
                        is LoginActivity -> {
                            // Call a function of base activity for transferring the result to it.
                            activity.userLoggedInSuccess(user)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error. And print the error in log.
                    when (activity) {
                        is LoginActivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while getting user details.",
                            e
                    )
                }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(Constants.USERS)
                // Document ID against which the data to be updated. Here the document id is the current logged in user id.
                .document(getCurrentUserID())
                // A HashMap of fields which are to be updated.
                .update(userHashMap)
                .addOnSuccessListener {


                    when (activity) {
                        is UserProfileActivity -> {
                            // Call a function of base activity for transferring the result to it.
                            activity.userProfileUpdateSuccess()
                        }
                    }

                }
                .addOnFailureListener { e ->

                    when (activity) {
                        is UserProfileActivity -> {
                            // Hide the progress dialog if there is any error. And print the error in log.
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while updating the user details.",
                            e
                    )
                }
    }

    fun uploadImageToCloudStorage(activity: Activity,imageFileURI:Uri?){
        var sRef:StorageReference = FirebaseStorage.getInstance().reference.child(
                Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." + Constants.getFileExtension(
                        activity,
                        imageFileURI
                )
        )

        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            Log.e("Firebase Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

            taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Download Image URL",uri.toString())
                        when(activity){
                            is UserProfileActivity ->{
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }

        }
                .addOnFailureListener { exception ->
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e(
                            activity.javaClass.simpleName,
                            exception.message,
                            exception
                    )
                }


    }

}