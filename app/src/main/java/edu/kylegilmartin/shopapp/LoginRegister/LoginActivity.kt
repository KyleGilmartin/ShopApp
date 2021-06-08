package edu.kylegilmartin.shopapp.LoginRegister

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import edu.kylegilmartin.shopapp.ui.activities.DashboardActivity
import edu.kylegilmartin.shopapp.R
import edu.kylegilmartin.shopapp.firestore.FirebaseClass
import edu.kylegilmartin.shopapp.models.User
import edu.kylegilmartin.shopapp.widgets.Constants
import edu.kylegilmartin.shopapp.widgets.popupActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : popupActivity(),View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        // full screen
//        @Suppress("DEPRECATION")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
//        }else{
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        }

        tv_forgot_password.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        tv_register.setOnClickListener(this)


    }

    fun userLoggedInSuccess(user: User){
        hideProgressDialog()



        if(user.profileCompleted == Constants.USER_PROFILE_INCOMPLETE_CODE){
            val intent = Intent(this,UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
        }else{
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        finish()

    }

    // overrides the OnClickListener - all onclick happen here for login
    override fun onClick(v:View?){
        if(v != null){
            when(v.id){
                R.id.tv_forgot_password ->{
                    val intent = Intent(this, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }
                R.id.btn_login ->{
                    loginRegisteredUser()
                }
                R.id.tv_register ->{
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)

                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_email_login.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_password_login.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                //showErrorSnackBar("Your details are valid.", false)
                true
            }
        }
    }

    private fun loginRegisteredUser(){
        if(validateLoginDetails()){
            // show the progress bar
            showProgressDialog(resources.getString(R.string.please_wait))

            //get the text from editText and trim the space
            val email = et_email_login.text.toString().trim{it <= ' '}
            val password = et_password_login.text.toString().trim{it <= ' '}

            // log in useing firebase
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener{task ->

                       if(task.isSuccessful){
                          FirebaseClass().getUserDetails(this@LoginActivity)
                       }else{
                           hideProgressDialog()
                           showErrorSnackBar(task.exception!!.message.toString(),true)
                       }
                    }
        }
    }


}