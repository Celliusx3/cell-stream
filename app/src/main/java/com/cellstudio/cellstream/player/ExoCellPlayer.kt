package com.cellstudio.cellmovie.player

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.SurfaceView
import com.cellstudio.cellstream.player.models.CellPlayerPlaySpeed
import com.cellstudio.cellstream.player.models.QualityLevel
import com.cellstudio.cellstream.player.CellPlayer
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.ext.cast.CastPlayer
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.cast.framework.CastContext
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ExoCellPlayer: CellPlayer {
    private var disTimer: Disposable? = null
    private var trackSelector: MappingTrackSelector? = null
    private val qualityLevels: MutableList<QualityLevel> = mutableListOf<QualityLevel>()

    private var player: SimpleExoPlayer?= null
//    private var castPlayer: CastPlayer? = null

    private var dataSourceFactory: DataSource.Factory ?= null

    private var currentPlayer: Player? = null

    private val listeners = mutableListOf<CellPlayer.CellPlayerListener>()

    override fun init(context: Context, playerView: PlayerView) {
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter.Builder(context).build()
        initExoPlayer(context, bandwidthMeter, playerView)
//        initCastPlayer(context)
        dataSourceFactory = buildDataSourceFactory()
    }

    override fun addPlayerListener(listener: CellPlayer.CellPlayerListener) {
        this.listeners.add(listener)
    }

    override fun getCellPlayerLifecycle(): CellPlayer.CellPlayerLifecycle {
        return object: CellPlayer.CellPlayerLifecycle {
            override fun onDestroy() {
                disTimer?.dispose()
                stop()
            }

            override fun onPause() {
                disTimer?.dispose()
                pause()
            }

            override fun onResume() {
                startOnTimeTimer()
                play()
            }

            override fun onStop() {
                disTimer?.dispose()
                pause()
            }
        }

    }

    override fun getCellPlayerData(): CellPlayer.CellPlayerData {
        return object: CellPlayer.CellPlayerData{
            override fun getQualityLevels(): List<QualityLevel> {
                return qualityLevels
            }
        }
    }

    override fun play(url: String) {
        val uri = Uri.parse(url)
        val videoSource = createCleanDataSource(uri, "", DrmSessionManager.DRM_UNSUPPORTED, dataSourceFactory!!)
        player?.prepare(videoSource)
        player?.playWhenReady = true
//        val castContext = CastContext.getSharedInstance(context!!)
//        castContext?.let {
//            val castPlayer: Player = CastPlayer(it)
//            castPlayer.addMediaItem(videoSource)
//        }
    }

    override fun play() {
        player?.playWhenReady = true
        listeners.forEach { it.onPlayListener() }
    }

    override fun pause() {
        player?.playWhenReady = false
        listeners.forEach { it.onPauseListener() }
    }

    override fun stop() {
        player?.stop()
    }

    override fun seekTo(position: Long) {
        player?.seekTo(position)
    }

    override fun seekBy(position: Long) {
        val realPostion = (player?.currentPosition?: 0) + position
        val tempPosition = when {
            realPostion <= 0 -> { 0 }
            realPostion > player?.duration?: 0 -> { player?.duration?: 0 }
            else -> { realPostion }
        }

        player?.seekTo(tempPosition)
    }

    private fun initExoPlayer(context: Context, bandwidthMeter: BandwidthMeter, playerView: PlayerView) {

        val adaptiveTrackSelection = AdaptiveTrackSelection.Factory()
        trackSelector = DefaultTrackSelector(context, adaptiveTrackSelection)

        player = SimpleExoPlayer.Builder(context)
                .setBandwidthMeter(bandwidthMeter)
                .setTrackSelector(trackSelector!!)
                .build()

        (playerView.videoSurfaceView!! as SurfaceView).setSecure(false)
        playerView.player = player
        playerView.useController = false
        player?.addListener(playerEventListener)
    }

//    private fun initCastPlayer(context: Context) {
//        try {
//            val castContext = CastContext.getSharedInstance(context)
//            castPlayer = CastPlayer(castContext)
//            castPlayer?.addListener(playerEventListener)
//            castPlayer?.setSessionAvailabilityListener(object: SessionAvailabilityListener {
//                override fun onCastSessionAvailable() {
//                    Log.d(TAG, "onCastSessionAvailable")
////                    setCurrentPlayer(exoPlayer);
//                }
//
//                override fun onCastSessionUnavailable() {
//                    Log.d(TAG, "onCastSessionUnavailable")
////                    setCurrentPlayer(exoPlayer);
//                }
//            })
////            castControlView.setPlayer(castPlayer);
//
//
//        } catch (e: RuntimeException) {
//            e.printStackTrace()
//        }
//
//
//    }

    private fun createCleanDataSource(uri: Uri,
                                      extension: String,
                                      drmSessionManager: DrmSessionManager,
                                      dataSourceFactory: DataSource.Factory): MediaSource {
        return when (@C.ContentType val type = Util.inferContentType(uri, extension)) {
            C.TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory)
                    .setDrmSessionManager(drmSessionManager)
                    .createMediaSource(MediaItem.fromUri(uri))
            C.TYPE_SS -> SsMediaSource.Factory(dataSourceFactory)
                    .setDrmSessionManager(drmSessionManager)
                    .createMediaSource(MediaItem.fromUri(uri))
            C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory)
                    .setDrmSessionManager(drmSessionManager)
                    .createMediaSource(MediaItem.fromUri(uri))
            C.TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory)
                    .setDrmSessionManager(drmSessionManager)
                    .createMediaSource(MediaItem.fromUri(uri))
            else -> throw IllegalStateException("Unsupported type: $type")
        }
    }

    private fun buildDataSourceFactory(): DataSource.Factory {
        return DefaultHttpDataSource.Factory().apply {
            setUserAgent("USER_AGENT")
        }
    }

    override fun setMute(isMute: Boolean) {
        player?.volume = if (isMute) 0.0f else 1.0f
    }

    override fun setPlaybackSpeed(speed: CellPlayerPlaySpeed) {
        player?.setPlaybackParameters(PlaybackParameters(speed.speed))
    }

    private fun startOnTimeTimer() {
        disTimer?.dispose()
        disTimer = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    val position: Long = player?.currentPosition ?: 0
                    val duration: Long = player?.duration ?: 0
                    if (duration < 0) return@subscribe
                    for (listener in listeners) {
                        listener.onTimeListener(position, duration)
                    }
                }, {})
    }

    private val playerEventListener: Player.EventListener = object: Player.EventListener {
        override fun onLoadingChanged(isLoading: Boolean) {
            super.onLoadingChanged(isLoading)
            Log.d(TAG, "loading [$isLoading]")
            listeners.forEach { it.onLoadingListener(isLoading) }
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    listeners.forEach { it.onBufferListener() }
                }
                Player.STATE_ENDED -> {
                    listeners.forEach { it.onCompleteListener() }
                }
                Player.STATE_IDLE -> {
                    listeners.forEach { it.onIdleListener() }
                }
                Player.STATE_READY -> {
                    listeners.forEach { it.onReadyListener() }
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            listeners.forEach { it.onErrorListener(error) }
        }

        override fun onTracksChanged(
                trackGroups: TrackGroupArray,
                trackSelections: TrackSelectionArray
        ) {
            val mappedTrackInfo = trackSelector?.currentMappedTrackInfo ?: return
            qualityLevels.clear()
            for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {
                val rendererTrackGroups =
                        mappedTrackInfo.getTrackGroups(rendererIndex)
                val trackSelection = trackSelections[rendererIndex]
                if (rendererTrackGroups.length > 0) {
                    for (groupIndex in 0 until rendererTrackGroups.length) {
                        val trackGroup = rendererTrackGroups[groupIndex]
                        for (trackIndex in 0 until trackGroup.length) {
                            val format = trackGroup.getFormat(trackIndex)
                            if (format.sampleMimeType!!.startsWith(MimeTypes.BASE_TYPE_VIDEO)) {
                                if (qualityLevels.isEmpty()) {
                                    val qlAuto = QualityLevel(label = "Auto", groupIndex = groupIndex, rendererIndex = rendererIndex)
                                    qualityLevels.add(qlAuto)

                                    val qualityLevel = QualityLevel(
                                            width = format.width,
                                            height = format.height,
                                            bitrate = format.bitrate,
                                            label = format.id?: "",
                                            trackIndex = trackIndex,
                                            groupIndex = groupIndex,
                                            rendererIndex = rendererIndex
                                    )
                                    qualityLevels.add(qualityLevel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setCurrentPlayer(player: Player) {
        if (player == currentPlayer) {
            return
        }

        // View management.
//        if (currentPlayer == exoPlayer) {
//            localPlayerView.setVisibility(View.VISIBLE)
//            castControlView.hide()
//        } else  /* currentPlayer == castPlayer */ {
//            localPlayerView.setVisibility(View.GONE)
//            castControlView.show()
//        }

        // Player state management.
        var playbackPositionMs = C.TIME_UNSET
        var windowIndex = C.INDEX_UNSET
        var playWhenReady = false
        val previousPlayer = currentPlayer
        if (previousPlayer != null) {
            // Save state from the previous player.
            val playbackState = previousPlayer.playbackState
            if (playbackState != Player.STATE_ENDED) {
                playbackPositionMs = previousPlayer.currentPosition
                playWhenReady = previousPlayer.playWhenReady
                windowIndex = previousPlayer.currentWindowIndex
//                if (windowIndex != currentItemIndex) {
//                    playbackPositionMs = C.TIME_UNSET
//                    windowIndex = currentItemIndex
//                }
            }
            previousPlayer.stop()
            previousPlayer.clearMediaItems()
        }
        this.currentPlayer = player

        // Media queue management.
//        player.setMediaItems(mediaQueue, windowIndex, playbackPositionMs)
        player.playWhenReady = playWhenReady
        player.prepare()
    }

    companion object {
        val TAG = ExoCellPlayer::class.java.simpleName
    }
}