package edu.kylegilmartin.shopapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import edu.kylegilmartin.shopapp.R
import edu.kylegilmartin.shopapp.firestore.FirebaseClass
import edu.kylegilmartin.shopapp.models.CartItem
import edu.kylegilmartin.shopapp.models.Product
import edu.kylegilmartin.shopapp.ui.adapters.CartItemsListAdapter
import edu.kylegilmartin.shopapp.widgets.popupActivity
import kotlinx.android.synthetic.main.activity_cart_list.*
import kotlinx.android.synthetic.main.activity_settings.*

class CartListActivity : popupActivity() {
    private lateinit var mProductList:ArrayList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)

        setupActionBar()
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_cart_list_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_cart_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        //getCartItemsList()
        getProductList()
    }

    fun successCartItemList(cartList: ArrayList<CartItem>){
        hideProgressDialog()

        if (cartList.size > 0){
            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE

            rv_cart_items_list.layoutManager = LinearLayoutManager(this)
            rv_cart_items_list.setHasFixedSize(true)

            val cartListAdapter = CartItemsListAdapter(this,cartList)
            rv_cart_items_list.adapter = cartListAdapter

            var subTotal:Double = 0.0
            for (item in cartList){
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                subTotal += (price * quantity)
            }

            tv_sub_total.text = "$${subTotal}"
            tv_shipping_charge.text = "$10.00"

            if (subTotal > 0){
                ll_checkout.visibility = View.VISIBLE
                val total = subTotal + 10 // shipping charge
                tv_total_amount.text = "$${total}"
            }else{
                ll_checkout.visibility = View.GONE
            }
        }
    }

    fun successProductListFromFireStore(productList:ArrayList<Product>){
        mProductList = productList

        getCartItemsList()
    }

    private fun getProductList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirebaseClass().getAllProductsList(this)
    }

    private fun getCartItemsList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirebaseClass().getCartList(this)
    }
}