package edu.kylegilmartin.shopapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.kylegilmartin.shopapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val sharedPreferences = getSharedPreferences(Constants.SHOP_PREFERENCES,Context.MODE_PRIVATE)
//        val username =sharedPreferences.getString(Constants.LOGGED_IN_USERNAME,"")!!
//        displayName.text = "Hello ${username.toString()}"
    }
}