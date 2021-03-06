package edu.kylegilmartin.shopapp.ui.activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import edu.kylegilmartin.shopapp.R
import edu.kylegilmartin.shopapp.SplashScreen.OrderPlacedActivity
import edu.kylegilmartin.shopapp.firestore.FirebaseClass
import edu.kylegilmartin.shopapp.models.Address
import edu.kylegilmartin.shopapp.models.CartItem
import edu.kylegilmartin.shopapp.models.Order
import edu.kylegilmartin.shopapp.models.Product
import edu.kylegilmartin.shopapp.ui.adapters.CartItemsListAdapter
import edu.kylegilmartin.shopapp.widgets.Constants
import edu.kylegilmartin.shopapp.widgets.popupActivity
import kotlinx.android.synthetic.main.activity_checkout.*
import java.util.*
import kotlin.collections.ArrayList


class CheckoutActivity : popupActivity() {

    private var mAddressDetails: Address? = null
    private lateinit var mProductList:ArrayList<Product>
    private lateinit var mCartItemsList:ArrayList<CartItem>
    private var mSubTotal:Double = 0.0
    private var mTotalAmount:Double = 0.0
    private lateinit var mOrderDetails:Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)){
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)
        }

        if (mAddressDetails != null){
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote
            tv_checkout_mobile_number.text = mAddressDetails?.mobileNumber

            if (mAddressDetails?.otherDetails!!.isNotEmpty()){
                tv_checkout_other_details.text = mAddressDetails?.otherDetails
            }
        }

        getProductList()

        btn_place_order.setOnClickListener {
            placeAnOrder()
        }
    }

    fun orderplaceSuccess(){
        FirebaseClass().updateProductCartDetails(this,mCartItemsList,mOrderDetails)
    }

    fun allDetailsUpdatedSuccessFully(){
        hideProgressDialog()

        val intent = Intent(this,OrderPlacedActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun placeAnOrder(){
        showProgressDialog(resources.getString(R.string.please_wait))

       if (mAddressDetails != null){
           mOrderDetails = Order(
                   FirebaseClass().getCurrentUserID(),
                   mCartItemsList,
                   mAddressDetails!!,
                   "My order ${System.currentTimeMillis()}",
                   mCartItemsList[0].image,
                   mSubTotal.toString(),
                   "10.00",
                   mTotalAmount.toString(),
                   System.currentTimeMillis()
           )

           FirebaseClass().placeOrder(this,mOrderDetails)
       }
    }


    fun successProductListFromFireStore(productsList:ArrayList<Product>){

        mProductList = productsList

        getCartItemsList()
    }

    private fun getCartItemsList(){
        FirebaseClass().getCartList(this)
    }

    fun successCartItemList(cartList:ArrayList<CartItem>){
        hideProgressDialog()
        for (product in mProductList){
            for (cart in cartList){
                if (product.product_id == cart.product_id){
                    cart.stock_quantity = product.stock_quantity
                }
            }
        }

        mCartItemsList = cartList

        rv_cart_list_items.layoutManager = LinearLayoutManager(this)
        rv_cart_list_items.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this,mCartItemsList,false)
        rv_cart_list_items.adapter = cartListAdapter

        for (item in mCartItemsList){
            val availableQuantity = item.stock_quantity.toInt()
            if (availableQuantity > 0){
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal +=(price * quantity)
            }
        }

        tv_checkout_sub_total.text = "$${mSubTotal}"
        tv_checkout_shipping_charge.text = "$10.00"
        if (mSubTotal >0){
            ll_checkout_place_order.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 10.0
            tv_checkout_total_amount.text = "$${mTotalAmount}"
        }else{
            ll_checkout_place_order.visibility = View.GONE
        }


    }

    private fun getProductList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirebaseClass().getAllProductsList(this)
    }



    private fun setupActionBar(){
        setSupportActionBar(toolbar_checkout_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }
}