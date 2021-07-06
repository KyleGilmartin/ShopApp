package edu.kylegilmartin.shopapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.kylegilmartin.shopapp.R
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlinx.android.synthetic.main.activity_address_list.toolbar_address_list_activity
import kotlinx.android.synthetic.main.activity_card_list.*

class CardListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_list)

        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_card_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_card_list_activity.setNavigationOnClickListener { onBackPressed() }
    }
}