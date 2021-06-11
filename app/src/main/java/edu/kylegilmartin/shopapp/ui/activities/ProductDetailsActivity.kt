package edu.kylegilmartin.shopapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import edu.kylegilmartin.shopapp.R
import edu.kylegilmartin.shopapp.firestore.FirebaseClass
import edu.kylegilmartin.shopapp.models.CartItem
import edu.kylegilmartin.shopapp.models.Product
import edu.kylegilmartin.shopapp.widgets.Constants
import edu.kylegilmartin.shopapp.widgets.GlideLoader
import edu.kylegilmartin.shopapp.widgets.popupActivity
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.activity_settings.*

class ProductDetailsActivity : popupActivity(),View.OnClickListener {
    private var mProductId: String =""
    private lateinit var mProductDetails:Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        setupActionBar()

        if(intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!

        }
        var productOwnerID:String = ""
        if(intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)){
             productOwnerID = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        if(FirebaseClass().getCurrentUserID() == productOwnerID){
            btn_add_to_cart.visibility = View.GONE
        }else{
            btn_add_to_cart.visibility = View.VISIBLE
        }

        btn_add_to_cart.setOnClickListener(this)
        getProductDetails()
    }

    private fun getProductDetails(){
        showProgressDialog(resources.getString(R.string.project_id))
        FirebaseClass().getProductDetails(this,mProductId)
    }

    fun productDetailsSuccess(product: Product){
        mProductDetails = product
        hideProgressDialog()
        GlideLoader(this).loadProductPicture(product.image,iv_product_detail_image)
        tv_product_details_title.text = product.title
        tv_product_details_price.text = "$${product.price}"
        tv_product_details_description.text = product.description
        tv_product_details_stock_quantity.text = product.stock_quantity
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_product_details_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun addToCart(){
        val addToCart = CartItem(
                FirebaseClass().getCurrentUserID(),
                mProductId,
                mProductDetails.title,
                mProductDetails.price,
                mProductDetails.image,
                Constants.DEFAULT_CART_QUANTITY
        )
        showProgressDialog(resources.getString(R.string.please_wait))
        FirebaseClass().addToCart(this,addToCart)
    }

    fun addToCartSuccess(){
        hideProgressDialog()
        Toast.makeText(this,resources.getString(R.string.success_msg_item_added_to_cart),Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id) {
                R.id.btn_add_to_cart -> {
                    addToCart()
                }
            }
        }
    }
}