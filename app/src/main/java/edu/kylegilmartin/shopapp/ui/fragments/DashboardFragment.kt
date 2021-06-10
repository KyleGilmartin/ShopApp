package edu.kylegilmartin.shopapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import edu.kylegilmartin.shopapp.R
import edu.kylegilmartin.shopapp.firestore.FirebaseClass
import edu.kylegilmartin.shopapp.models.Product
import edu.kylegilmartin.shopapp.ui.activities.SettingsActivity

class DashboardFragment : BaseFragment() {

    //private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {

            R.id.action_settings -> {

                startActivity(Intent(activity, SettingsActivity::class.java))

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun successDashboardItemList(dashboardItemsList:ArrayList<Product>){
        hideProgressDialog()

    }

    private fun getDashboardItemsList(){
        showProgressDialog(resources.getString(R.string.please_wait))

        FirebaseClass().getDashboardItemList(this)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }
}