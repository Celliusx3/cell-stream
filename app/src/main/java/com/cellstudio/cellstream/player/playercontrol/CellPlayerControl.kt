package com.cellstudio.cellstream.player.playercontrol

import android.content.Context
import android.view.View

interface CellPlayerControl : View.OnTouchListener {
    /**
     * initialize the controls
     */
    fun initialize(context: Context, orientation: Orientation)

    /**
     * Returns whether the IPlayerControl is initialized
     */
    fun isInitialized(): Boolean

    /**
     * returns the View object to be attached to the player
     */
    fun getView(): View

    /**
     * Register a listener to execute the events required by the user
     */
    fun registerCallbackListener(listener: CellPlayerControlListener): Boolean

    /**
     * Unregister a listener for any event.
     */
    fun unregisterCallbackListener(listener: CellPlayerControlListener): Boolean

    /**
     * Updates the position and duration of the streaming.
     * Position is a long stating the milliseconds played
     * Duration is the duration of the streaming in milliseconds
     */
    fun updatePosition(position_ms: Long)
    fun updateDuration(duration_ms: Long)

    /**
     * Request from the video player to change the state of the play/pause button
     */
    fun setPlayPause(isPlaying: Boolean)

    /**
     * @return player controls isPlaying status
     */
    fun isPlaying(): Boolean

    /**
     * Updates the fullscreen icon
     */
    fun updateFullScreenIcon(isFullScreen: Boolean)

    /**
     * Updates title
     */
    fun setTitle(title: String)
}

enum class Orientation {
    LANDSCAPE, PORTRAIT
}
