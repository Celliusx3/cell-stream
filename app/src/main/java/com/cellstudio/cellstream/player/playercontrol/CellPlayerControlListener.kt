package com.cellstudio.cellstream.player.playercontrol

interface CellPlayerControlListener {
    fun onPlay()
    fun onPause()
    fun onSeekForward()
    fun onSeekBackward()
    fun onSeekTo(milliseconds: Long)
    fun onSeekComplete()  // specifically only for calling GD event
    fun onMuteChanged(isMuted: Boolean)
    fun onFullScreenIconPressed(isFullScreen: Boolean)
    fun onBackPressed()
    fun onMorePressed()
}