package edu.kylegilmartin.shopapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import edu.kylegilmartin.shopapp.LoginRegister.LoginActivity
import edu.kylegilmartin.shopapp.LoginRegister.UserProfileActivity
import edu.kylegilmartin.shopapp.R
import edu.kylegilmartin.shopapp.admin.UI.activities.AdminMainActivity
import edu.kylegilmartin.shopapp.firestore.FirebaseClass
import edu.kylegilmartin.shopapp.models.User
import edu.kylegilmartin.shopapp.widgets.Constants
import edu.kylegilmartin.shopapp.widgets.GlideLoader
import edu.kylegilmartin.shopapp.widgets.popupActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : popupActivity(), View.OnClickListener {
    private lateinit var mUserDetails:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupActionBar()
        tv_edit.setOnClickListener(this)
        btn_logout.setOnClickListener(this)
        ll_address.setOnClickListener(this)
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_settings_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_settings_activity.setNavigationOnClickListener { onBackPressed() }
    }



    private  fun getUserDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirebaseClass().getUserDetails(this)
    }

     fun userDetailsSuccess(user: User){

         mUserDetails = user

        hideProgressDialog()
        GlideLoader(this).loadUserPicture(user.image,iv_user_photo)
        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_gender.text = "${user.gender}"
        tv_email.text = "${user.email}"
        tv_mobile_number.text = "${user.mobile}"

         if (user.admin == Constants.Admin_code){
             btn_go_to_admin.visibility = View.VISIBLE

             btn_go_to_admin.setOnClickListener {
                 val intent = Intent(this, AdminMainActivity::class.java)
                 intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                 startActivity(intent)
                 finish()
             }
         }
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.tv_edit->{
                    val intent = Intent(this,UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS,mUserDetails)
                    startActivity(intent)
                }

                R.id.btn_logout ->{
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this,LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                R.id.ll_address ->{
                    startActivity(Intent(this,AddressListActivity::class.java))
                }
            }
        }
    }
}