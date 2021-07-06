package edu.kylegilmartin.shopapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import edu.kylegilmartin.shopapp.R
import edu.kylegilmartin.shopapp.models.Address
import edu.kylegilmartin.shopapp.models.Card
import edu.kylegilmartin.shopapp.widgets.Constants
import edu.kylegilmartin.shopapp.widgets.popupActivity
import kotlinx.android.synthetic.main.activity_add_edit_address.*
import kotlinx.android.synthetic.main.activity_add_edit_address.toolbar_add_edit_address_activity
import kotlinx.android.synthetic.main.activity_add_edit_card.*

class AddEditCardActivity : popupActivity() {
    private var mCardDetails: Card? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_card)

        if (intent.hasExtra(Constants.EXTRA_CARD_DETAILS)) {
            mCardDetails =
                intent.getParcelableExtra(Constants.EXTRA_CARD_DETAILS)!!
        }

        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_add_edit_card_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_add_edit_card_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun addUpdateCardSuccess() {

        // Hide progress dialog
        hideProgressDialog()

        val notifySuccessMessage: String = if (mCardDetails != null && mCardDetails!!.id.isNotEmpty()) {
            resources.getString(R.string.msg_your_card_updated_successfully)
        } else {
            resources.getString(R.string.err_your_card_added_successfully)
        }

        Toast.makeText(
            this@AddEditCardActivity,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()


        setResult(RESULT_OK)
        finish()
    }
}