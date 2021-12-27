package com.cellstudio.cellstream.ui.utilities

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity

object UIVisibilityUtils {
    fun enableKeepScreenOn(activity: AppCompatActivity?) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_SECURE)
    }

    fun disableKeepScreenOn(activity: AppCompatActivity?) {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_SECURE)
    }

    fun hideStatusBar(activity: AppCompatActivity?) {
        if (activity != null) {
            activity.window
                .addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_SECURE)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
            // Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
            hideActionBar(activity)
        }
    }

    fun showStatusBar(activity: AppCompatActivity?) {
        if (activity != null) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    fun hideNavigationBar(activity: AppCompatActivity?) {
        if (activity != null) {
            activity.window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        }
    }

    fun showNavigationBar(activity: AppCompatActivity?) {
        if (activity != null) {
            activity.window.decorView.systemUiVisibility = 0
        }
    }

    fun hideActionBar(activity: AppCompatActivity?) {
        if (activity == null) {
            return
        }
        val actionBar: ActionBar? = activity.supportActionBar
        if (actionBar != null) {
            actionBar.hide()
        }
    }

    fun showActionBar(activity: AppCompatActivity?) {
        if (activity == null) {
            return
        }
        val actionBar: ActionBar? = activity.supportActionBar
        if (actionBar != null) {
            actionBar.show()
        }
    }

    fun disableWindowFullScreen(activity: AppCompatActivity?) {
        if (activity != null) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            showActionBar(activity)
            showStatusBar(activity)
        }
    }

    fun enableWindowFullScreen(activity: AppCompatActivity?) {
        if (activity != null) {
            activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_IMMERSIVE)
            hideActionBar(activity)
            hideStatusBar(activity)
        }
    }

    /**
     * 判断是否有NavigationBar
     *
     * @param activity
     * @return
     */
    fun hasNavigationBar(activity: Activity): Boolean {
        val windowManager = activity.windowManager
        val d = windowManager.defaultDisplay
        val realDisplayMetrics = DisplayMetrics()
        d.getRealMetrics(realDisplayMetrics)
        val realHeight = realDisplayMetrics.heightPixels
        val realWidth = realDisplayMetrics.widthPixels
        val displayMetrics = DisplayMetrics()
        d.getMetrics(displayMetrics)
        val displayHeight = displayMetrics.heightPixels
        val displayWidth = displayMetrics.widthPixels
        return realWidth - displayWidth > 0 || realHeight - displayHeight > 0
    }

    /**
     * 获得NavigationBar的高度 +15
     */
    fun getNavigationBarHeight(activity: Activity): Int {
        var result = 0
        val resources = activity.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0 && hasNavigationBar(activity)) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result + 15
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        // 获得状态栏高度
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }

    fun Activity.hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                //  or View.SYSTEM_UI_FLAG_LOW_PROFILE
                )
        // window.addFlags(View.KEEP_SCREEN_ON)
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    fun Activity.showSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                ) // or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        // window.clearFlags(View.KEEP_SCREEN_ON)
    }
}