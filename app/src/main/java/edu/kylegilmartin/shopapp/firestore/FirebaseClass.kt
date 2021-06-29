package edu.kylegilmartin.shopapp.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import edu.kylegilmartin.shopapp.LoginRegister.LoginActivity
import edu.kylegilmartin.shopapp.LoginRegister.RegisterActivity
import edu.kylegilmartin.shopapp.LoginRegister.UserProfileActivity
import edu.kylegilmartin.shopapp.models.*
import edu.kylegilmartin.shopapp.ui.activities.*
import edu.kylegilmartin.shopapp.ui.fragments.DashboardFragment
import edu.kylegilmartin.shopapp.ui.fragments.ProductFragment
import edu.kylegilmartin.shopapp.widgets.Constants

class FirebaseClass {


    private val mFireStore = FirebaseFirestore.getInstance()


    fun registerUser(activity: RegisterActivity, userInfo: User) {

        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        mFireStore.collection(Constants.USERS)
                // Document ID for users fields. Here the document it is the User ID.
                .document(userInfo.id)
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                    activity.userRegistrationSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while registering the user.",
                            e
                    )
                }
    }

    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
                // The document id to get the Fields of user.
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->

                    Log.i(activity.javaClass.simpleName, document.toString())

                    // Here we have received the document snapshot which is converted into the User Data model object.
                    val user = document.toObject(User::class.java)!!

                    val sharedPreferences =
                            activity.getSharedPreferences(
                                    Constants.SHOP_PREFERENCES,
                                    Context.MODE_PRIVATE
                            )

                    // Create an instance of the editor which is help us to edit the SharedPreference.
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString(
                            Constants.LOGGED_IN_USERNAME,
                            "${user.firstName} ${user.lastName}"
                    )
                    editor.apply()

                    when (activity) {
                        is LoginActivity -> {
                            // Call a function of base activity for transferring the result to it.
                            activity.userLoggedInSuccess(user)
                        }
                        is SettingsActivity ->{
                            activity.userDetailsSuccess(user)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error. And print the error in log.
                    when (activity) {
                        is LoginActivity -> {
                            activity.hideProgressDialog()
                        }
                        is SettingsActivity ->{
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while getting user details.",
                            e
                    )
                }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(Constants.USERS)
                // Document ID against which the data to be updated. Here the document id is the current logged in user id.
                .document(getCurrentUserID())
                // A HashMap of fields which are to be updated.
                .update(userHashMap)
                .addOnSuccessListener {


                    when (activity) {
                        is UserProfileActivity -> {
                            // Call a function of base activity for transferring the result to it.
                            activity.userProfileUpdateSuccess()
                        }
                    }

                }
                .addOnFailureListener { e ->

                    when (activity) {
                        is UserProfileActivity -> {
                            // Hide the progress dialog if there is any error. And print the error in log.
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while updating the user details.",
                            e
                    )
                }
    }

    fun uploadImageToCloudStorage(activity: Activity,imageFileURI:Uri?,imageType: String){
        var sRef:StorageReference = FirebaseStorage.getInstance().reference.child(
                imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(
                        activity,
                        imageFileURI
                )
        )

        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            Log.e("Firebase Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

            taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Download Image URL",uri.toString())
                        when(activity){
                            is UserProfileActivity ->{
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is AddProductActivity ->{
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }

        }
                .addOnFailureListener { exception ->
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.hideProgressDialog()
                        }
                        is AddProductActivity ->{
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e(
                            activity.javaClass.simpleName,
                            exception.message,
                            exception
                    )
                }


    }

    fun uploadProductDetails(activity: AddProductActivity,productInfo:Product){
        mFireStore.collection(Constants.PRODUCTS)
                .document()
                .set(productInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.productUploadSuccess()
                }
                .addOnFailureListener {e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error while uploading product",e)
                }
    }

    fun getProductList(fragment:Fragment){
        mFireStore.collection(Constants.PRODUCTS)
                .whereEqualTo(Constants.USER_ID,getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    Log.e("product list",document.documents.toString())
                    val productList:ArrayList<Product> = ArrayList()
                    for (i in document.documents){
                        val product = i.toObject(Product::class.java)
                        product!!.product_id = i.id

                        productList.add(product)
                    }
                    when(fragment){
                        is ProductFragment ->{
                            fragment.successProductListFromFireStore(productList)
                        }
                    }
                }
    }

    fun getDashboardItemList(fragment: DashboardFragment){
        mFireStore.collection(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener { document ->
                    val productList: ArrayList<Product> = ArrayList()

                    for (i in document){
                        val product = i.toObject(Product::class.java)!!
                        product.product_id = i.id
                        productList.add(product)
                    }
                    fragment.successDashboardItemList(productList)

                }.addOnFailureListener { e->
                    fragment.hideProgressDialog()
                    Log.e(fragment.javaClass.simpleName,"Error while loading dashboard items",e)
                }
    }

    fun deleteProduct(fragment: ProductFragment,productId:String){
        mFireStore.collection(Constants.PRODUCTS)
                .document(productId)
                .delete()
                .addOnSuccessListener {
                    fragment.productDeleteSuccess()

                }.addOnFailureListener {
                    e->
                    fragment.hideProgressDialog()
                    Log.e(fragment.requireActivity().javaClass.simpleName,"Error while deleting the product",e)
                }
    }

    fun getProductDetails(activity: ProductDetailsActivity,productId: String){
        mFireStore.collection(Constants.PRODUCTS)
                .document(productId)
                .get()
                .addOnSuccessListener { document ->
                    val product = document.toObject(Product::class.java)
                    if (product != null) {
                        activity.productDetailsSuccess(product)
                    }

                }.addOnFailureListener { e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error while getting product details",e)
                }
    }

    fun addToCart(activity: ProductDetailsActivity,addToCart:CartItem){
        mFireStore.collection(Constants.CART_ITEMS)
                .document()
                .set(addToCart, SetOptions.merge())
                .addOnSuccessListener {
                    activity.addToCartSuccess()
                }.addOnFailureListener { e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error while adding item to cart",e)
                }
    }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity,productId: String){
        mFireStore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID,getCurrentUserID())
                .whereEqualTo(Constants.PRODUCT_ID,productId)
                .get()
                .addOnSuccessListener { document ->
                    Log.e(activity.javaClass.simpleName,document.documents.toString())
                    if (document.documents.size > 0){
                        activity.productExistInCart()
                    }else{
                        activity.hideProgressDialog()
                    }
                }
                .addOnFailureListener {e ->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,
                    "Error while checking the cart list",e)
                }
    }

    fun getCartList(activity: Activity){
        mFireStore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID,getCurrentUserID())
                .get()
                .addOnSuccessListener {document ->
                    Log.e(activity.javaClass.simpleName,document.documents.toString())
                    val list: ArrayList<CartItem> = ArrayList()

                    for (i in document.documents){
                        val cartItem = i.toObject(CartItem::class.java)!!
                        cartItem.id = i.id

                        list.add(cartItem)
                    }

                    when(activity){
                        is CartListActivity ->{
                            activity.successCartItemsList(list)
                        }
                        is CheckoutActivity ->{
                            activity.successCartItemList(list)
                        }
                    }
                } .addOnFailureListener { e->
                    when(activity){
                        is CartListActivity ->{
                            activity.hideProgressDialog()
                        }
                        is CheckoutActivity ->{
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e(activity.javaClass.simpleName,"Error while getting the cart list",e)
                }
    }

    fun getAllProductsList(activity: Activity){
        mFireStore.collection(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener { document ->

                    val productsList:ArrayList<Product> = ArrayList()
                    for (i in document.documents){
                        val product = i.toObject(Product::class.java)
                        product!!.product_id = i.id

                        productsList.add(product)
                    }
                    when(activity){
                        is CartListActivity ->{
                            activity.successProductsListFromFireStore(productsList)

                        }
                        is CheckoutActivity ->{
                            activity.successProductListFromFireStore(productsList)
                        }

                    }

                }
                .addOnFailureListener { e->
                    when(activity) {
                        is CartListActivity -> {
                            activity.hideProgressDialog()
                        }
                        is CheckoutActivity ->{
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(activity.javaClass.simpleName, "Error while getting all products list", e)

                }
    }

    fun removeItemFromCart(context: Context,cart_id:String){
        mFireStore.collection(Constants.CART_ITEMS)
                .document(cart_id)
                .delete()
                .addOnSuccessListener {
                when(context){
                    is CartListActivity ->{
                        context.itemRemovedSuccess()
                    }
                }
                }
                .addOnFailureListener {
                    e->
                    when(context){
                        is CartListActivity ->{
                            context.hideProgressDialog()
                        }
                    }
                    Log.e(context.javaClass.simpleName,"Error when removing item from cart",e)
                }
    }

    fun updateMyCart(context: Context,cart_id: String,itemHashMap: HashMap<String,Any>){
        mFireStore.collection(Constants.CART_ITEMS)
        .document(cart_id)
        .update(itemHashMap)
                .addOnSuccessListener {
                        when (context){
                            is CartListActivity ->{
                                context.itemUpdateSuccess()
                            }
                        }
                }
                .addOnFailureListener {
                    e->
                    when(context){
                        is CartListActivity ->{
                            context.hideProgressDialog()
                        }
                    }
                    Log.e(context.javaClass.simpleName,"Error when updating cart",e)
                }
    }

    fun addAddress(activity: AddEditAddressActivity,addressInfo:Address){
        mFireStore.collection(Constants.ADDRESSES)
                .document()
                .set(addressInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.addUpdateAddressSuccess()
                }
                .addOnFailureListener {
                    e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error while adding the address",e)
                }
    }

    fun getAddressesList(activity: AddressListActivity){
        mFireStore.collection(Constants.ADDRESSES)
                .whereEqualTo(Constants.USER_ID,getCurrentUserID())
                .get()
                .addOnSuccessListener {
                    document ->
                    Log.e(activity.javaClass.simpleName,document.documents.toString())
                    val addressList: ArrayList<Address> = ArrayList()
                    for (i in document.documents){
                        val address = i.toObject(Address::class.java)!!
                        address.id = i.id
                        addressList.add(address)
                    }
                    activity.successAddressListFromFirestore(addressList)
                }.addOnFailureListener {
                    e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error while getting the address",e)
                }
    }

    fun updateAddress(activity:AddEditAddressActivity,addressInfo: Address,addressId:String){
        mFireStore.collection(Constants.ADDRESSES)
                .document(addressId)
                .set(addressInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.addUpdateAddressSuccess()
                }.addOnFailureListener {
                    e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error while updating the address",e)
                }
    }

    fun deleteAddress(activity: AddressListActivity,addressId: String){
        mFireStore.collection(Constants.ADDRESSES)
                .document(addressId)
                .delete()
                .addOnSuccessListener {
                    activity.deleteAddressSuccess()
                }
                .addOnFailureListener {
                    e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error while deleting the address",e)
                }
    }

    fun placeOrder(activity: CheckoutActivity,order:Order){
        mFireStore.collection(Constants.ORDERS)
                .document()
                .set(order, SetOptions.merge())
                .addOnSuccessListener {
                activity.orderplaceSuccess()
                }
                .addOnFailureListener { e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error while placing an order.",e)
                }
    }

}