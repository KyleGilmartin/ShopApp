package edu.kylegilmartin.shopapp.widgets

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS : String = "users"
    const val SHOP_PREFERENCES: String = "ShopPrefs"
    const val LOGGED_IN_USERNAME:String = "Logged_in_username"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1

    const val MALE:String = "Male"
    const val FEMALE:String = "Female"
    const val MOBILE:String = "mobile"
    const val GENDER:String = "gender"
    const val FIRSTNAME:String = "firstName"
    const val LASTNAME:String = "lastName"
    const val IMAGE:String = "image"
    const val USER_PROFILE_IMAGE:String = "User_Profile_Image"
    const val COMPLETE_PROFILE:String = "profileCompleted"
    const val USER_PROFILE_COMPLETE_CODE = 1
    const val USER_PROFILE_INCOMPLETE_CODE = 0
    const val PRODUCT_IMAGE:String = "Product_image"

    fun showImageChoosen(activity: Activity){
        val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity,uri: Uri?):String?{
        return MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}