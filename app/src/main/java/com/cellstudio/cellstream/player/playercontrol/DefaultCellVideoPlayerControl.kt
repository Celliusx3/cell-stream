package com.cellstudio.cellstream.player.playercontrol

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.mediarouter.app.MediaRouteButton
import com.cellstudio.cellmovie.player.utils.TimeUtils
import com.cellstudio.cellstream.R
import com.google.android.gms.cast.framework.CastButtonFactory

class DefaultCellVideoPlayerControl : CellPlayerControl {
    private lateinit var context: Context
    private lateinit var orientation: Orientation
    private lateinit var controlsView: View

    // UI Elements of Main controls
    private lateinit var backButton: ImageButton
    private lateinit var currentTimeTextView: TextView
    private lateinit var timeSeekBar: SeekBar
    private lateinit var maxTimeTextView: TextView
    private lateinit var rwButton: ImageView
    private lateinit var playPauseButton: ImageView
    private lateinit var ffButton: ImageView
    private lateinit var fullScreenButton: ImageButton
    private lateinit var volumeButton: ImageButton
    private lateinit var centerMainContainerLayout: LinearLayout
    private lateinit var bottomMainContainerLayout: LinearLayout
    private lateinit var moreButton: ImageButton
    private lateinit var tvPlayerControlsTitle: TextView

    // Drawables
    private val volumeOnIcon = R.drawable.ic_volume_white_24dp
    private val volumeOffIcon = R.drawable.ic_volume_off_white_24dp
    private val pauseIcon = R.drawable.ic_video_player_pause_white_24dp
    private val playIcon = R.drawable.ic_video_player_play_white_24dp
    private val fullScreenExit = R.drawable.ic_video_player_fullscreen_exit_white_24dp
    private val fullScreen = R.drawable.ic_video_player_fullscreen_white_24dp
    private val fastForward = R.drawable.ic_video_player_fast_forward_white_24dp
    private val fastRewind = R.drawable.ic_video_player_fast_rewind_white_24dp

    // State of the controls and views
    private var isPaused: Boolean = false
    private var isFullScreen: Boolean = false
    private var currentTime: Long = 0
    private var maxTime: Long = 0
    private var isInitialized = false
    private var isMuted = false

    // Determines if the player controls are visible or not
    private var isVisible = true
    // Determines if the controls are being animated in the hide/show transition
    private var isAnimating = false
    // Only used this for maintaining the paused/play state before and after skipping
    private var isPausedSkip = true

    // Listener for handling User interaction with controls
    private var playerListener: CellPlayerControlListener? = null

    // Handler for UI show/hide
    private var mHandler: Handler? = null

    override fun initialize(context: Context, orientation: Orientation) {
        this.context = context
        this.orientation = orientation
        mHandler = AnimationHandler(Looper.getMainLooper())
        val inflater = LayoutInflater.from(context)
        controlsView = inflater.inflate(R.layout.video_player_control, null)
        findViews()
        setupViews()
        setInitialValues()
        isInitialized = true
    }

    override fun isInitialized(): Boolean {
        return isInitialized
    }

    override fun getView(): View {
        return controlsView
    }

    override fun registerCallbackListener(listener: CellPlayerControlListener): Boolean {
        this.playerListener = listener
        return true
    }

    override fun unregisterCallbackListener(listener: CellPlayerControlListener): Boolean {
        this.playerListener = null
        return true
    }

    override fun updatePosition(position_ms: Long) {
        // Only update the textviews and seekbar if it is playing
        if (position_ms > 0 && position_ms != currentTime && !isPaused) {
            currentTime = position_ms
            if (maxTime > 0 && currentTime >= 0) {
                timeSeekBar.progress = (SEEKBAR_MAX_POSITION * currentTime / maxTime).toInt()
            }
            updateTimePositionAndDurationViews(currentTime)
        }
    }

    override fun updateDuration(duration_ms: Long) {
        if (duration_ms > 0 && duration_ms != maxTime) {
            maxTime = duration_ms
            maxTimeTextView.text = TimeUtils.getTimePlayerFormat(maxTime - currentTime, true)
        }
    }

    override fun setPlayPause(isPlaying: Boolean) {
        isPaused = !isPlaying
        updatePlayPauseIcon()
        updateRewindForwardVisibility()
    }

    override fun isPlaying(): Boolean {
        return !isPaused
    }

    override fun updateFullScreenIcon(isFullScreen: Boolean) {
        this.isFullScreen = isFullScreen
        updateFullScreenButton()
    }

    override fun setTitle(title: String) {
        tvPlayerControlsTitle.text = title
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        // Start animations
        if (!isAnimating && !isVisible && event?.action == MotionEvent.ACTION_DOWN) {
            showControls()
        } else if (event?.action == MotionEvent.ACTION_DOWN && !isAnimating && isVisible) {
            // If the user press again the screen and the controls are visible, then hide them
            mHandler?.removeCallbacks(hideControlsRunnable)
            hideControls()
        }
        return false
    }

    private fun findViews() {
        // Main controls layout
        centerMainContainerLayout =  controlsView.findViewById<View>(R.id.llPlayerControlsCenter) as LinearLayout
        bottomMainContainerLayout = controlsView.findViewById<View>(R.id.llPlayerControlsBtmBar) as LinearLayout
        currentTimeTextView = controlsView.findViewById<View>(R.id.tvPlayerControlsCurrentTime) as TextView
        timeSeekBar = controlsView.findViewById<View>(R.id.sbPlayerControlsSeekBar) as SeekBar
        timeSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)
        maxTimeTextView = controlsView.findViewById<View>(R.id.tvPlayerControlsTimeLeft) as TextView
        rwButton = controlsView.findViewById<View>(R.id.ivPlayerControlsRewind) as ImageView
        rwButton.setOnClickListener(onClickListener)
        playPauseButton = controlsView.findViewById<View>(R.id.ivPlayerControlsPlayPause) as ImageView
        playPauseButton.setOnClickListener(onClickListener)
        ffButton = controlsView.findViewById<View>(R.id.ivPlayerControlsForward) as ImageView
        ffButton.setOnClickListener(onClickListener)
        fullScreenButton = controlsView.findViewById<View>(R.id.ivPlayerControlsFullScreen) as ImageButton
        fullScreenButton.setOnClickListener(onClickListener)
        volumeButton = controlsView.findViewById<View>(R.id.ivPlayerControlsVolume) as ImageButton
        volumeButton.setOnClickListener(onClickListener)
        backButton = controlsView.findViewById<View>(R.id.ivPlayerControlsBack) as ImageButton
        backButton.setOnClickListener(onClickListener)
        moreButton = controlsView.findViewById<View>(R.id.ivPlayerControlsMore) as ImageButton
        moreButton.setOnClickListener(onClickListener)
        tvPlayerControlsTitle = controlsView.findViewById<View>(R.id.tvPlayerControlsTitle) as TextView
    }

    private fun setInitialValues() {
        refreshViews()
        initializeAnimations()
    }

    /**
     * Initialize the player controls as hidden, then show them
     */
    private fun initializeAnimations() {
        isAnimating = false
        isVisible = false
        controlsView.visibility = View.VISIBLE
        controlsView.alpha = 0.0f
        // Force layout re draw
        controlsView.requestLayout()
        controlsView.postInvalidate()

        showControls()
    }

    /**
     * Main onClick listener for all clickable views
     */
    private val onClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.ivPlayerControlsRewind -> {
                requestRW()
                restartHidingTimer()
            }
            R.id.ivPlayerControlsPlayPause -> {
                togglePlayPauseIcon()
                updateRewindForwardVisibility()
                requestPlayPause(!isPaused)
                restartHidingTimer()
            }
            R.id.ivPlayerControlsForward -> {
                requestFF()
                restartHidingTimer()
            }
            R.id.ivPlayerControlsFullScreen -> {
                toggleFullScreenButton()
                requestFullScreen()
                restartHidingTimer()
            }
            R.id.ivPlayerControlsVolume -> {
                toggleVolumeButton()
                requestVolume()
                restartHidingTimer()
            }
            R.id.ivPlayerControlsBack -> {
                requestBack()
                restartHidingTimer()
            }
            R.id.ivPlayerControlsMore -> {
                requestMore()
            }
        }
    }

    /**
     * Common actions to be sent to the player trough the playerListener
     */
    private fun requestRW() {
        playerListener?.onSeekBackward()
    }

    private fun requestFF() {
        playerListener?.onSeekForward()
    }

    private fun requestFullScreen() {
        playerListener?.onFullScreenIconPressed(isFullScreen)
    }

    private fun requestVolume() {
        playerListener?.onMuteChanged(isMuted)
    }

    private fun requestBack() {
        playerListener?.onBackPressed()
    }

    private fun requestMore() {
        playerListener?.onMorePressed()
    }

    private fun requestSeekComplete() {
        playerListener?.onSeekComplete()
    }

    private fun requestPlayPause(play: Boolean) {
        if (playerListener != null) {
            if (play) {
                playerListener!!.onPlay()
            } else {
                playerListener!!.onPause()
            }
            setPlayPause(play)
        }
    }

    private fun requestSeekTo(milliseconds: Long) {
        playerListener?.onSeekTo(milliseconds)
    }

    private fun toggleFullScreenButton() {
        isFullScreen = !isFullScreen
        updateFullScreenButton()
    }

    private fun updateFullScreenButton() {
        mHandler?.post {
            if (isFullScreen) {
                fullScreenButton.setImageResource(fullScreenExit)
            } else {
                fullScreenButton.setImageResource(fullScreen)
            }
        }
    }

    private fun updateRewindForwardVisibility() {
        mHandler?.post {
            if (isPaused) {
                ffButton.visibility = View.GONE
                rwButton.visibility = View.GONE
            } else {
                ffButton.visibility = View.VISIBLE
                rwButton.visibility = View.VISIBLE
            }
        }
    }

    private fun toggleVolumeButton() {
        isMuted = !isMuted
        updateVolumeButton()
    }

    private fun updateVolumeButton() {
        mHandler?.post {
            if (isMuted) {
                volumeButton.setImageResource(volumeOffIcon)
            } else {
                volumeButton.setImageResource(volumeOnIcon)
            }
        }
    }

    private fun togglePlayPauseIcon() {
        isPaused = !isPaused
        updatePlayPauseIcon()
    }

    private fun updatePlayPauseIcon() {
        mHandler?.post {
            if (isPaused) {
                playPauseButton.setImageResource(playIcon)
            } else {
                playPauseButton.setImageResource(pauseIcon)
            }
        }
    }

    /**
     * Restart the timer used to auto-hide the controls. This is useful when some buttons are
     * clicked by the user.
     */
    private fun restartHidingTimer() {
        if (isVisible) {
            mHandler?.removeCallbacks(hideControlsRunnable)
            mHandler?.postDelayed(hideControlsRunnable, HIDE_ANIMATION_AFTER_MS)
        }
    }

    private fun showControls() {
        if (isAnimating) {
            return
        }
        mHandler?.post(showControlsRunnable)
    }

    private fun hideControls() {
        if (isAnimating) {
            return
        }
        mHandler?.post(hideControlsRunnable)
    }

    private val showControlsRunnable = Runnable {
        if (isAnimating) {
            // Don't animate if another animation is running.
            return@Runnable
        }
        controlsView.animate().alpha(1.0f).setDuration(ANIMATION_SHOW_DURATION_MS).setListener(showAnimatorListener)
    }

    private val showAnimatorListener: Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            isAnimating = true
            controlsView.visibility = View.VISIBLE
        }

        override fun onAnimationEnd(animation: Animator) {
            isAnimating = false
            isVisible = true
            mHandler?.postDelayed(hideControlsRunnable, HIDE_ANIMATION_AFTER_MS)
            // Make sure the controls are shown in the screen
            controlsView.requestLayout()
        }

        override fun onAnimationCancel(animation: Animator) {
            isAnimating = false
        }

        override fun onAnimationRepeat(animation: Animator) {

        }
    }

    private val hideControlsRunnable = Runnable {
        if (isAnimating) {
            // Don´t animate if another animation is running.
            return@Runnable
        }
        controlsView.animate().alpha(0.0f).setDuration(ANIMATION_HIDE_DURATION_MS).setListener(hideAnimatorListener)
    }

    private val hideAnimatorListener: Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            // Make sure the controls are shown in the screen
            controlsView.requestLayout()
            isAnimating = true
        }

        override fun onAnimationEnd(animation: Animator) {
            isAnimating = false
            isVisible = false
            controlsView.visibility = View.GONE
        }

        override fun onAnimationCancel(animation: Animator) {
            isAnimating = false
        }

        override fun onAnimationRepeat(animation: Animator) {

        }
    }

    /**
     * This method takes the state values and refresh the views according to them
     */
    private fun refreshViews() {
        timeSeekBar.max = SEEKBAR_MAX_POSITION
        updatePlayPauseIcon()
        updateRewindForwardVisibility()
        updateFullScreenButton()
        timeSeekBar.progress = 0
    }

    /**
     * Listens to changes to the time seekbar
     */
    private val onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            Log.d(TAG, "onProgressChanged  progress=$progress fromUser=$fromUser")
            // Just send events from user
            if (fromUser) {
                updateTimePositionAndDurationViews(getTimeInMsFromSeekbarProgress(seekBar.progress))
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            Log.d(TAG, "onStartTrackingTouch")
            // Pause video reproduction while moving the time seekbar
            isPausedSkip = isPaused
            if (!isPausedSkip) { // do not pause it again if it is already paused
                requestPlayPause(false)
            }
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            Log.d(TAG, "onStopTrackingTouch")
            // When the user has stoped tracking the time seekbar, then request to seek to a position
            requestSeekTo(getTimeInMsFromSeekbarProgress(seekBar.progress))

            // force-send a GD player_seek() event
            requestSeekComplete()

            // Pause video reproduction while moving the time seekbar
            // However, do not pause it again if it is already paused
            if (!isPausedSkip) {
                requestPlayPause(true)
            }
        }
    }

    private fun updateTimePositionAndDurationViews(milliseconds: Long) {
        currentTimeTextView.text = TimeUtils.getTimePlayerFormat(milliseconds, false)
        maxTimeTextView.text = TimeUtils.getTimePlayerFormat(maxTime - milliseconds, true)
    }

    private fun getTimeInMsFromSeekbarProgress(progress: Int): Long {
        return progress.toLong() * maxTime / SEEKBAR_MAX_POSITION.toLong()
    }

    private fun setupViews() {
        when (orientation) {
            Orientation.LANDSCAPE -> setupViewsInLandscape()
            Orientation.PORTRAIT -> setupViewsInPortrait()
        }
    }

    private fun setupViewsInLandscape() {
        backButton.visibility = View.VISIBLE
    }

    private fun setupViewsInPortrait() {
        backButton.visibility = View.GONE
    }

    companion object {
        private val TAG = DefaultCellVideoPlayerControl::class.java.simpleName
        // Duration of show animation
        private const val ANIMATION_SHOW_DURATION_MS: Long = 100
        // Duration of hide animation
        private const val ANIMATION_HIDE_DURATION_MS: Long = 500
        // Time after the controls are automatically hidden
        private const val HIDE_ANIMATION_AFTER_MS: Long = 5000
        // Seekbar max position
        private const val SEEKBAR_MAX_POSITION = 1000

        /**
         * Handler class for managing messages to show/hide the controls
         */
        private class AnimationHandler(looper: Looper) : Handler(looper)
    }
}