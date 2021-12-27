package com.cellstudio.cellstream.ui.utilities

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

object ImageUtils {
    @JvmStatic fun setImageUri(view: ImageView, imageUri: String) {
        Glide.with(view.context)
            .load(imageUri)
            .placeholder(android.R.color.transparent)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}