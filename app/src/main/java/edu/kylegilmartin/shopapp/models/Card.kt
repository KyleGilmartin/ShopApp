package edu.kylegilmartin.shopapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Card (
    val user_id:String = "",
    val name:String = "",
    val cardNo:String ="",
    val cardDate:String = "",
    val cardCVS:String = "",
    var id:String = ""
): Parcelable