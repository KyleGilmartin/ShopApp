package edu.kylegilmartin.shopapp.widgets

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore

object Constants {
    const val USERS : String = "users"
    const val SHOP_PREFERENCES: String = "ShopPrefs"
    const val LOGGED_IN_USERNAME:String = "Logged_in_username"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1

    fun showImageChoosen(activity: Activity){
        val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
}