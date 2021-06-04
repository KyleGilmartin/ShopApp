package edu.kylegilmartin.shopapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.kylegilmartin.shopapp.widgets.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences(Constants.SHOP_PREFERENCES,Context.MODE_PRIVATE)
        val username =sharedPreferences.getString(Constants.LOGGED_IN_USERNAME,"")!!
        displayName.text = "Hello ${username.toString()}"
    }
}