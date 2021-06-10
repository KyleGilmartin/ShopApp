package edu.kylegilmartin.shopapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import edu.kylegilmartin.shopapp.R
import edu.kylegilmartin.shopapp.firestore.FirebaseClass
import edu.kylegilmartin.shopapp.models.Product
import edu.kylegilmartin.shopapp.ui.activities.AddProductActivity
import edu.kylegilmartin.shopapp.ui.activities.SettingsActivity
import edu.kylegilmartin.shopapp.ui.adapters.MyProductListAdapter
import kotlinx.android.synthetic.main.fragment_products.*

class ProductFragment : BaseFragment() {

   // private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    fun successProductListFromFireStore(productList: ArrayList<Product>){
        hideProgressDialog()
       if(productList.size > 0){
           rv_my_product_items.visibility = View.VISIBLE
           tv_no_products_found.visibility = View.GONE

           rv_my_product_items.layoutManager = LinearLayoutManager(activity)
           rv_my_product_items.setHasFixedSize(true)
           val adapterProducts = MyProductListAdapter(requireActivity(),productList,this)
           rv_my_product_items.adapter = adapterProducts
       }else{
           rv_my_product_items.visibility = View.GONE
           tv_no_products_found.visibility = View.VISIBLE
       }
    }

    fun deleteProduct(productID:String){
        //Toast.makeText(requireActivity(),"you can noe delete the product, $productID", Toast.LENGTH_SHORT).show()
        showAlertDialogToDeleteProduct(productID)
    }
    private fun showAlertDialogToDeleteProduct(productID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

            showProgressDialog(resources.getString(R.string.please_wait))
            // Call the function of FireStore class.
            FirebaseClass().deleteProduct(this, productID)
            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun productDeleteSuccess(){
        hideProgressDialog()
        Toast.makeText(requireActivity(),resources.getString(R.string.product_delete_success_msg),Toast.LENGTH_SHORT).show()
        getProductListFromFireStore()
    }

    private fun getProductListFromFireStore(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirebaseClass().getProductList(this)
    }

    override fun onResume() {
        super.onResume()
        getProductListFromFireStore()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      //  homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_products, container, false)


        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {

            R.id.action_add_products -> {

                startActivity(Intent(activity, AddProductActivity::class.java))

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}