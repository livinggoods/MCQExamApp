package org.livinggoods.exam.activity;

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import org.livinggoods.exam.R
import java.io.File
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_video_player.*
import org.livinggoods.exam.App
import java.io.IOException


class VideoPlayerActivity : BaseActivity(), Player.EventListener {

    val TAG: String = VideoPlayerActivity::class.java.simpleName
    val BACK_STACK_ROOT_TAG: String = "root_fragment"

    private var video: MediaStore.Video? = null
    private var file: File? = null

    var player: SimpleExoPlayer? = null

    private var progressPercent: Int = 0
    private var isDownloading: Boolean = false

    private var isPlayed = false

    lateinit var mediaUrl: String
    private var playerState: Int? = -1
    private var shouldAutoPlay: Boolean = false
    private var resumeWindow: Int = 0
    private var resumePosition: Long = 0

    companion object {
        val KEY_VIDEO_URL = "video_url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_video_player)

        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
        }

        if (intent.hasExtra(KEY_VIDEO_URL)) {
            mediaUrl = intent.getStringExtra(KEY_VIDEO_URL)
        } else {
            Toast.makeText(this, "Video URL not provided", Toast.LENGTH_LONG).show()
        }
    }


    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
        }


            playVideoFile()

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {

            android.R.id.home -> {
                finish()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        releasePlayer()
        super.onNewIntent(intent)
    }

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun updateResumePosition() {
        resumeWindow = player?.getCurrentWindowIndex()!!
        resumePosition = if (player?.isCurrentWindowSeekable()!!)
            Math.max(0, player?.getCurrentPosition()!!)
        else
            C.TIME_UNSET
    }

    private fun clearResumePosition() {
        resumeWindow = C.INDEX_UNSET
        resumePosition = C.TIME_UNSET
    }

    private fun initializePlayer() {

        if (player == null) {

            val bandwidthMeter = DefaultBandwidthMeter()
            val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
            val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
            val loadControl = DefaultLoadControl()

            player = ExoPlayerFactory.newSimpleInstance(this@VideoPlayerActivity, trackSelector, loadControl)
            exo_player_view.player = player as SimpleExoPlayer

            player?.addListener(this@VideoPlayerActivity)
        }
    }

    private fun playVideoFile() {

        runOnUiThread {

            if (player == null) initializePlayer()

            val dataSourceFactory =
                    DefaultHttpDataSourceFactory(Util.getUserAgent(this, getString(R.string.app_name)));
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(mediaUrl))

            player?.prepare(mediaSource)
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player?.playWhenReady!!
            updateResumePosition()
            player!!.release()
            player = null
        }
    }

    override fun onSeekProcessed() {

    }

    override fun onLoadingChanged(isLoading: Boolean) {

    }

    override fun onPositionDiscontinuity(reason: Int) {

    }

    override fun onRepeatModeChanged(repeatMode: Int) {

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

    }


    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

    }

    /**
     * Get URI of a image/file
     *
     * @param type
     * @return
     */
    private fun getOutputMediaFile(fileName: String, category: String): File? {

        // External sdcard location
        val appMediaDir = File(Environment.getExternalStorageDirectory(), App.VIDEO_DIRECTORY_NAME)
        if (!appMediaDir.exists()) {
            val created = appMediaDir.mkdirs()

            if (!created) throw IOException("Failed to create media directory")
        }

        val categoryDir = File(appMediaDir, category.replace(" ", "_"))
        if (!categoryDir.exists()) {
            val created = categoryDir.mkdirs()

            if (!created) throw IOException("Failed to create ${category} directory in ${appMediaDir.absoluteFile}")
        }

        val mediaFile = File(categoryDir, fileName)

        if (!mediaFile.exists()) {
            mediaFile.createNewFile()
        }

        return mediaFile
    }


}
