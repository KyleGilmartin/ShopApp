package edu.kylegilmartin.shopapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.kylegilmartin.shopapp.R
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlinx.android.synthetic.main.activity_settings.*

class AddressListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        setupActionBar()

        tv_add_address.setOnClickListener {
            startActivity(Intent(this,AddEditAddressActivity::class.java))
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_address_list_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_address_list_activity.setNavigationOnClickListener { onBackPressed() }
    }
}