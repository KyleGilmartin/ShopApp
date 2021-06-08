package edu.kylegilmartin.shopapp.SplashScreen

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import edu.kylegilmartin.shopapp.ui.activities.DashboardActivity
import edu.kylegilmartin.shopapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // full screen
       @Suppress("DEPRECATION")
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
           window.insetsController?.hide(WindowInsets.Type.statusBars())
       }else{
           window.setFlags(
                   WindowManager.LayoutParams.FLAG_FULLSCREEN,
                   WindowManager.LayoutParams.FLAG_FULLSCREEN
           )
       }
        @Suppress("DEPRECATION")
        Handler().postDelayed({
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
           },
                2500
        )



    }
}