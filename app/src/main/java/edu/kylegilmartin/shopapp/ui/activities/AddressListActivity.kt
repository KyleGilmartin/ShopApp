package edu.kylegilmartin.shopapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myshoppal.utils.SwipeToEditCallback
import edu.kylegilmartin.shopapp.R
import edu.kylegilmartin.shopapp.firestore.FirebaseClass
import edu.kylegilmartin.shopapp.models.Address
import edu.kylegilmartin.shopapp.ui.adapters.AddressListAdapter
import edu.kylegilmartin.shopapp.widgets.popupActivity
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlinx.android.synthetic.main.activity_settings.*

class AddressListActivity : popupActivity() {
    private var mSelectAddress: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        setupActionBar()



        tv_add_address.setOnClickListener {
            startActivity(Intent(this,AddEditAddressActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        getAddressList()
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

    fun successAddressListFromFireStore(addressList: ArrayList<Address>){
        // Hide the progress dialog
        hideProgressDialog()

        if (addressList.size > 0) {

            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility = View.GONE

            rv_address_list.layoutManager = LinearLayoutManager(this@AddressListActivity)
            rv_address_list.setHasFixedSize(true)



            val addressAdapter = AddressListAdapter(this@AddressListActivity, addressList, mSelectAddress)

            rv_address_list.adapter = addressAdapter


            if (!mSelectAddress) {
                val editSwipeHandler = object : SwipeToEditCallback(this) {
                  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        val adapter = rv_address_list.adapter as AddressListAdapter
                       adapter.notifyEditItem(
                               this@AddressListActivity,
                               viewHolder.adapterPosition
                       )
                   }
                }
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list)


//                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
//                   override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//
//                        // Show the progress dialog.
//                        showProgressDialog(resources.getString(R.string.please_wait))
//
//                        FirebaseClass().deleteAddress(
//                                this@AddressListActivity,
//                                addressList[viewHolder.adapterPosition].id
//                        )
//                    }
//               }
//               val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
//               deleteItemTouchHelper.attachToRecyclerView(rv_address_list)
           }
        } else {
            rv_address_list.visibility = View.GONE
            tv_no_address_found.visibility = View.VISIBLE
        }
    }

    /**
     * A function notify the user that the address is deleted successfully.
     */
//    fun deleteAddressSuccess() {
//
//        // Hide progress dialog.
//        hideProgressDialog()
//
//        Toast.makeText(
//                this@AddressListActivity,
//                resources.getString(R.string.err_your_address_deleted_successfully),
//                Toast.LENGTH_SHORT
//        ).show()
//
//        getAddressList()
//    }


    private fun getAddressList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirebaseClass().getAddressesList(this)
    }
}