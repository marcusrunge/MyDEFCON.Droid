package com.marcusrunge.mydefcon.utils

import android.graphics.Color
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.view.View
import android.webkit.WebView
import android.widget.RadioGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
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

object BindingUtils {
    @BindingAdapter("bindNavController")
    @JvmStatic
    fun bindNavController(view: View, mainViewModel: MainViewModel) {
        val navController =
            (view.context as AppCompatActivity).findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_status, R.id.navigation_checklist
            )
        )
        (view.context as AppCompatActivity).setupActionBarWithNavController(
            navController,
            appBarConfiguration
        )
        (view as BottomNavigationView).setupWithNavController(navController)
        view.setOnItemSelectedListener { item ->
            if (navController.backQueue.size > 0) {
                val entry = navController.backQueue[0]
                navController.popBackStack(entry.id, true)
            }
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

    @BindingAdapter("setBackground")
    @JvmStatic
    fun bindSetBackground(layout: ConstraintLayout, drawable: Drawable) {
        layout.background = TileUtil(drawable, Shader.TileMode.REPEAT)
    }

    @BindingAdapter("endpointUrl")
    @JvmStatic
    fun setEndpointUrl(view: WebView, endpointUrl: String) {
        view.loadUrl(endpointUrl)
        view.setBackgroundColor(Color.parseColor("#B2000000"))
    }

    @BindingAdapter("setAdapter")
    @JvmStatic
    fun bindAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = adapter
    }

    @BindingAdapter("setItemTouchHelper")
    @JvmStatic
    fun bindItemTouchHelper(recyclerView: RecyclerView, itemTouchHelper: ItemTouchHelper?) {
        itemTouchHelper?.attachToRecyclerView(recyclerView)
    }

    @BindingAdapter("setBackgroundDrawableColor")
    @JvmStatic
    fun bindbackgroundDrawableColor(view: View, resourceId: Int?) {
        if (resourceId != null) {
            view.background.setTint(view.context.resources.getColor(resourceId, view.context.theme))
        }
    }

    @BindingAdapter("setReceivedRadioButtonId")
    @JvmStatic
    fun bindReceivedRadioButtonId(radioGroup: RadioGroup?, receivedRadioButtonId: MutableLiveData<Int>?) {
        if (radioGroup != null && receivedRadioButtonId!=null) {
            if(radioGroup.checkedRadioButtonId!=receivedRadioButtonId.value)
                receivedRadioButtonId.value?.let { radioGroup.check(it) }
        }
    }
}