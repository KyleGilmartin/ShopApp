package edu.kylegilmartin.shopapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.kylegilmartin.shopapp.R
import edu.kylegilmartin.shopapp.models.Product
import edu.kylegilmartin.shopapp.widgets.GlideLoader
import kotlinx.android.synthetic.main.item_dashboard_layout.view.*
import kotlinx.android.synthetic.main.item_list_layout.view.*

open class DashboardItemsListAdapter (
    private val context:Context,
    private val list:ArrayList<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener:OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_dashboard_layout,
                parent,
                false
            )
        )
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder){
            GlideLoader(context).loadProductPicture(model.image,holder.itemView.iv_dashboard_item_image)
            holder.itemView.tv_dashboard_item_title.text = model.title
            holder.itemView.tv_dashboard_item_price.text = "$${model.price}"


            holder.itemView.setOnClickListener{
                if (onClickListener != null){
                    onClickListener!!.onClick(position,model)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view:View): RecyclerView.ViewHolder(view)

    interface OnClickListener {
        fun onClick(position: Int,product: Product){

        }
    }

}