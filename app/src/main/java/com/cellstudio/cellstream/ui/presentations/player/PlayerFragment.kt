package com.cellstudio.cellstream.ui.presentations.player

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.cellstudio.cellstream.player.ExoCellPlayer
import com.cellstudio.cellstream.databinding.FragmentPlayerBinding
import com.cellstudio.cellstream.player.CellPlayer
import com.cellstudio.cellstream.player.models.CellPlayerPlaySpeed
import com.cellstudio.cellstream.player.playercontrol.CellPlayerControl
import com.cellstudio.cellstream.player.playercontrol.CellPlayerControlListener
import com.cellstudio.cellstream.player.playercontrol.DefaultCellVideoPlayerControl
import com.cellstudio.cellstream.player.playercontrol.Orientation
import com.cellstudio.cellstream.ui.presentations.action.ActionFragment
import com.cellstudio.cellstream.ui.presentations.action.models.ActionModel
import com.cellstudio.cellstream.ui.presentations.base.BaseFragment
import com.cellstudio.cellstream.ui.presentations.player.models.PlayerSpeedModel
import com.cellstudio.cellstream.ui.presentations.player.viewModel.DefaultPlayerViewModel
import com.cellstudio.cellstream.ui.utilities.UIVisibilityUtils
import com.cellstudio.cellstream.ui.utilities.UIVisibilityUtils.hideSystemUI
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@AndroidEntryPoint
class PlayerFragment : BaseFragment<FragmentPlayerBinding>() {
    private lateinit var detailsId: String
    private val disposable: CompositeDisposable = CompositeDisposable()

    override val viewModel: DefaultPlayerViewModel by viewModels()

    override fun createBindings(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, attachToParent)
    }

    private lateinit var url: String
    private lateinit var title: String
    private lateinit var playerControl: CellPlayerControl
    private lateinit var videoPlayer: CellPlayer

    private var isFullScreen = false
    val pressFullScreenButtonSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val pressPlayButtonSubject: PublishSubject<Unit> = PublishSubject.create()
    private val pressPauseButtonSubject: PublishSubject<Unit> = PublishSubject.create()

    override fun onGetInputData() {
        super.onGetInputData()
        detailsId = this.arguments?.getString(EXTRA_DETAILS)?: ""
    }

    private fun setUrl(url: String, extension: String?) {
        this.url = url
        videoPlayer.play(url, extension)
    }

    private fun setTitle(title: String) {
        this.title = title
        playerControl.setTitle(title)
    }

    override fun onBindView(view: View, savedInstanceState: Bundle?) {
        super.onBindView(view, savedInstanceState)
        createVideoPlayer()
        initializeVideoPlayerListener()
        initializeVideoPlayerControls()
        setTitle("Test Movie")
        binding?.pvVideoPlayerPlayer?.setOnTouchListener(playerControl)
    }

    override fun onBindData(view: View) {
        super.onBindData(view)
        viewModel.episodeData.observe(viewLifecycleOwner) { setUrl(it.first, it.second) }

        disposable.add(pressPlayButtonSubject.subscribe { videoPlayer.play() })
        disposable.add(pressPauseButtonSubject.subscribe { videoPlayer.pause() })
        disposable.add(pressFullScreenButtonSubject.subscribe {isNextFullScreen ->
            if (isNextFullScreen) {
                binding?.pvVideoPlayerPlayer?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            } else {
                binding?.pvVideoPlayerPlayer?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            }
            isFullScreen = isNextFullScreen
        })
    }

    override fun onStop() {
        super.onStop()
        videoPlayer.getCellPlayerLifecycle().onStop()
    }

    override fun onResume() {
        super.onResume()
        videoPlayer.getCellPlayerLifecycle().onResume()

        UIVisibilityUtils.enableWindowFullScreen(activity = activity as AppCompatActivity?)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    }

    override fun onPause() {
        super.onPause()
        videoPlayer.getCellPlayerLifecycle().onPause()
        UIVisibilityUtils.disableWindowFullScreen(activity = activity as AppCompatActivity?)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
        videoPlayer.getCellPlayerLifecycle().onDestroy()
    }

    private fun initializeVideoPlayerListener() {
        videoPlayer.addPlayerListener(object: CellPlayer.CellPlayerListener{
            override fun onBufferListener() { Log.d(TAG, "onBuffer")}
            override fun onErrorListener(throwable: PlaybackException) {
                onPlayerError(throwable)
            }
            override fun onTimeListener(position: Long, duration: Long) {
                Log.d(TAG, "onTime: $position | $duration")
                playerControl.updateDuration(duration)
                playerControl.updatePosition(position)
            }
            override fun onPlayListener() { playerControl.setPlayPause(true)}
            override fun onPauseListener() { playerControl.setPlayPause(false)}
            override fun onCompleteListener() { Log.d(TAG, "onCompleteListener") }
            override fun onIdleListener() {
                Log.d(TAG, "onIdleListener")}
            override fun onReadyListener() { Log.d(TAG, "onReadyListener") }
            override fun onLoadingListener(isLoading: Boolean) { } // viewModel.loading.value = isLoading }
        })
    }

    private fun createVideoPlayer() {
        if (context == null) { return }

        videoPlayer = ExoCellPlayer()
        binding?.pvVideoPlayerPlayer?.let {
            videoPlayer.init(requireContext(), it)
        }
    }

    private fun initializeVideoPlayerControls () {
        playerControl = DefaultCellVideoPlayerControl()

        if (!playerControl.isInitialized())
            playerControl.initialize(requireContext(), Orientation.LANDSCAPE)

        // Add the player view in front of the player
        val viewControl = playerControl.getView()
        if (viewControl.parent != null) {
            (viewControl.parent as ViewGroup).removeView(viewControl)
        }
        binding?.flVideoPlayerControls?.addView(viewControl)
        // Register callbacks
        playerControl.registerCallbackListener(controlsListener)
        playerControl.updateFullScreenIcon(false)
    }

    /**
     * Callbacks from the player controls
     */
    private val controlsListener = object : CellPlayerControlListener {
        override fun onBackPressed() { requireActivity().onBackPressed() }
        override fun onPlay() { pressPlayButtonSubject.onNext(Unit) }
        override fun onPause() { pressPauseButtonSubject.onNext(Unit) }
        override fun onSeekForward() { onSeekingTouchNSec(true) }
        override fun onSeekBackward() { onSeekingTouchNSec(false) }
        override fun onSeekTo(position: Long) { onSeekToPosition(position) }
        override fun onSeekComplete() {}
        override fun onMuteChanged(isMuted: Boolean) { mutePlayer(isMuted) }
        override fun onFullScreenIconPressed(isFullScreen: Boolean) { pressFullScreenButtonSubject.onNext(isFullScreen) }
        override fun onMorePressed() {
            getVideoPlayerSpeedFragment().show(childFragmentManager, null)
        }
    }

    // Seek to new position when Seek button is pressed
    private fun onSeekingTouchNSec(bIsForward: Boolean) {
        if (bIsForward) {
            videoPlayer.seekBy(SEEK_M_SECONDS)
        } else {
            videoPlayer.seekBy(-SEEK_M_SECONDS)
        }
    }

    private fun onSeekToPosition(lNewPosition: Long) {
        videoPlayer.seekTo(lNewPosition)
    }

    private fun mutePlayer(isMuted: Boolean) {
        videoPlayer.setMute(isMuted)
    }

    private fun onPlayerError(error: PlaybackException) {
        binding?.tvVideoPlayerError?.visibility = View.VISIBLE
        binding?.flVideoPlayerControls?.visibility = View.GONE
    }

    private fun getVideoPlayerSpeedFragment(): ActionFragment {
        val fragment = ActionFragment()
        fragment.setActions(CellPlayerPlaySpeed.values().toList().map { PlayerSpeedModel(it) })
        fragment.listener = object : ActionFragment.Listener {
            override fun onClicked(model: ActionModel) {
                activity?.hideSystemUI()
                if (model is PlayerSpeedModel){
                    videoPlayer.setPlaybackSpeed(model.speed)
                }
                fragment.dismiss()
            }
        }

        return fragment
    }

    companion object {
        // Number of seconds to seek fw bw when FF or RW is pressed.
        private const val SEEK_M_SECONDS: Long = 10000
        const val EXTRA_DETAILS = "extra_details"
        fun newInstance(data: String): Bundle {
            return Bundle().apply {
                putString(EXTRA_DETAILS, data)
            }
        }
    }

}

