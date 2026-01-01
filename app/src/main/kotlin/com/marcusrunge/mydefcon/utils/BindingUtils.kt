package com.marcusrunge.mydefcon.utils

import android.annotation.SuppressLint
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.text.format.DateFormat
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColorInt
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.ui.main.MainViewModel
import java.util.Date

/**
 * A collection of [BindingAdapter]s for data binding in XML layouts.
 * This object provides custom attributes that can be used to bind data to views.
 */
object BindingUtils {

    /**
     * Binds a [NavController] to a [BottomNavigationView], setting up the ActionBar and
     * handling navigation events.
     *
     * @param view The [BottomNavigationView] to bind.
     * @param mainViewModel The [MainViewModel] instance.
     */
    @SuppressLint("RestrictedApi")
    @BindingAdapter("bindNavController")
    @JvmStatic
    fun bindNavController(view: View, mainViewModel: MainViewModel) {
        // Find the NavController from the NavHostFragment.
        val navController =
            (view.context as AppCompatActivity).findNavController(R.id.nav_host_fragment_activity_main)
        // Configure the app bar to show the Up button only on top-level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_status, R.id.navigation_checklist
            )
        )
        // Set up the ActionBar with the NavController.
        (view.context as AppCompatActivity).setupActionBarWithNavController(
            navController,
            appBarConfiguration
        )
        // Set up the BottomNavigationView with the NavController.
        (view as BottomNavigationView).setupWithNavController(navController)

        // Custom listener to handle re-selection and navigation stack.
        view.setOnItemSelectedListener { item ->
            // Clear the back stack if it's not empty to prevent deep stacks.
            if (navController.currentBackStack.value.isNotEmpty()) {
                val entry = navController.currentBackStack.value[0]
                navController.popBackStack(entry.id, true)
            }
            // Navigate to the selected destination.
            when (item.itemId) {
                R.id.navigation_status -> {
                    navController.navigate(R.id.navigation_status)
                    true
                }

                R.id.navigation_checklist -> {
                    navController.navigate(R.id.navigation_checklist)
                    true
                }

                else -> false
            }
        }
    }

    /**
     * Sets a tiled background drawable on a [ConstraintLayout].
     *
     * @param layout The [ConstraintLayout] to modify.
     * @param drawable The [Drawable] to be tiled as the background.
     */
    @BindingAdapter("setBackground")
    @JvmStatic
    fun bindSetBackground(layout: ConstraintLayout, drawable: Drawable) {
        layout.background = TileUtil(drawable, Shader.TileMode.REPEAT)
    }

    /**
     * Loads a URL into a [WebView] and sets a semi-transparent background color.
     *
     * @param view The [WebView] to load the URL into.
     * @param endpointUrl The URL to load.
     */
    @BindingAdapter("endpointUrl")
    @JvmStatic
    fun setEndpointUrl(view: WebView, endpointUrl: String) {
        view.loadUrl(endpointUrl)
        view.setBackgroundColor("#B2000000".toColorInt())
    }

    /**
     * Binds a [RecyclerView.Adapter] to a [RecyclerView] and sets its LayoutManager.
     *
     * @param recyclerView The [RecyclerView] to set up.
     * @param adapter The [RecyclerView.Adapter] to attach.
     */
    @BindingAdapter("setAdapter")
    @JvmStatic
    fun bindAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = adapter
    }

    /**
     * Attaches an [ItemTouchHelper] to a [RecyclerView] for swipe or drag-and-drop functionality.
     *
     * @param recyclerView The [RecyclerView] to attach the helper to.
     * @param itemTouchHelper The [ItemTouchHelper] to attach.
     */
    @BindingAdapter("setItemTouchHelper")
    @JvmStatic
    fun bindItemTouchHelper(recyclerView: RecyclerView, itemTouchHelper: ItemTouchHelper?) {
        itemTouchHelper?.attachToRecyclerView(recyclerView)
    }

    /**
     * Sets the tint color of a view's background drawable.
     *
     * @param view The [View] whose background is to be tinted.
     * @param resourceId The color resource ID to use for the tint.
     */
    @BindingAdapter("setBackgroundDrawableColor")
    @JvmStatic
    fun bindBackgroundDrawableColor(view: View, resourceId: Int?) {
        if (resourceId != null) {
            view.background.setTint(view.context.resources.getColor(resourceId, view.context.theme))
        }
    }

    /**
     * Generates a QR code from a string and displays it in an [ImageView].
     *
     * @param view The [ImageView] to display the QR code in.
     * @param textToEncode The text to encode into the QR code.
     */
    @BindingAdapter("setTextToEncode")
    @JvmStatic
    fun bindTextToEncode(view: ImageView, textToEncode: String) {
        QrCodeUtils.generateAndDisplayQrCode(textToEncode, view)
    }

    /**
     * Formats a Unix timestamp (in milliseconds) into a localized date string and sets it on a [TextView].
     *
     * @param view The [TextView] to display the formatted date in.
     * @param timeStamp The timestamp as a [Long].
     */
    @BindingAdapter("setTimeStamp")
    @JvmStatic
    fun bindTimeStamp(view: TextView, timeStamp: Long) {
        // Format the timestamp into a date string based on the user's locale and set it.
        view.text = DateFormat.getDateFormat(view.context).format(Date(timeStamp))
    }
}
