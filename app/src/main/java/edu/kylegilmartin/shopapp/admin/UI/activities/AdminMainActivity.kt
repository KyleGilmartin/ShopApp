package edu.kylegilmartin.shopapp.admin.UI.activities

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.kylegilmartin.shopapp.R
import edu.kylegilmartin.shopapp.widgets.popupActivity


class AdminMainActivity : popupActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        supportActionBar!!.setBackgroundDrawable(
                ContextCompat.getDrawable(
                        this,R.drawable.app_gradient_color_background
                )
        )

        val navView: BottomNavigationView = findViewById(R.id.admin_nav_view)

        val navController = findNavController(R.id.nav_admin_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.navigation_admin_users,
                        R.id.navigation_admin_settings

                )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
}