package edu.kylegilmartin.shopapp.ui.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import edu.kylegilmartin.shopapp.R
import edu.kylegilmartin.shopapp.firestore.FirebaseClass
import edu.kylegilmartin.shopapp.models.Address
import edu.kylegilmartin.shopapp.models.CartItem
import edu.kylegilmartin.shopapp.models.Product
import edu.kylegilmartin.shopapp.ui.adapters.CartItemsListAdapter
import edu.kylegilmartin.shopapp.widgets.Constants
import edu.kylegilmartin.shopapp.widgets.popupActivity
import kotlinx.android.synthetic.main.activity_checkout.*


class CheckoutActivity : popupActivity() {

    private var mAddressDetails: Address? = null
    private lateinit var mProductList:ArrayList<Product>
    private lateinit var mCartItemsList:ArrayList<CartItem>

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