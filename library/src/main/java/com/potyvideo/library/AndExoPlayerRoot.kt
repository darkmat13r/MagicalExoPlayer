package com.potyvideo.library

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.google.android.exoplayer2.ui.PlayerView
import com.potyvideo.library.globalEnums.*
import com.potyvideo.library.utils.DoubleClick
import java.lang.StringBuilder
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sign


abstract class AndExoPlayerRoot @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private lateinit var screenConfig: ScreenConfig
    private var inflatedView: View = inflate(context, R.layout.layout_player_base_kotlin, this)

    companion object{
        private  val TAG = AndExoPlayerRoot::class.java.simpleName
    }
    var playerView: PlayerView
    var retryView: LinearLayout
    var retryViewTitle: TextView
    var retryButton: Button
    var backwardView: AppCompatButton
    var forwardView: AppCompatButton
    var mute: AppCompatImageButton
    var unMute: AppCompatImageButton
    var settingContainer: FrameLayout
    var seekText: TextView
    var container: View

    abstract var customClickListener: DoubleClick

    var currAspectRatio: EnumAspectRatio = EnumAspectRatio.ASPECT_16_9
    var currRepeatMode: EnumRepeatMode = EnumRepeatMode.REPEAT_OFF
    var currPlayerSize: EnumPlayerSize = EnumPlayerSize.EXACTLY
    var currResizeMode: EnumResizeMode = EnumResizeMode.FILL
    var currMute: EnumMute = EnumMute.UNMUTE
    var currPlaybackSpeed: EnumPlaybackSpeed = EnumPlaybackSpeed.NORMAL

    init {
        val dm = context.resources.displayMetrics
        val yRange = dm.widthPixels.coerceAtMost(dm.heightPixels)
        val xRange = dm.widthPixels.coerceAtLeast(dm.heightPixels)
        screenConfig = ScreenConfig(dm, xRange, yRange, resources.configuration.orientation)
        // views
        playerView = inflatedView.findViewById(R.id.playerView)
        retryView = inflatedView.findViewById(R.id.retry_view)
        backwardView = inflatedView.findViewById(R.id.exo_backward)
        forwardView = inflatedView.findViewById(R.id.exo_forward)
        retryViewTitle = retryView.findViewById(R.id.textView_retry_title)
        retryButton = retryView.findViewById(R.id.button_try_again)
        mute = playerView.findViewById(R.id.exo_mute)
        unMute = playerView.findViewById(R.id.exo_unmute)
        settingContainer = playerView.findViewById(R.id.container_setting)
        seekText = inflatedView.findViewById(R.id.timeForward)
        container = inflatedView.findViewById(R.id.container)
        seekText.visibility = View.GONE
        // listeners
        initListeners()

    }

    private fun initListeners() {
        retryButton.setOnClickListener(customClickListener)
        backwardView.setOnClickListener(customClickListener)
        forwardView.setOnClickListener(customClickListener)
        mute.setOnClickListener(customClickListener)
        unMute.setOnClickListener(customClickListener)
        playerView.setOnTouchListener(object : OnTouchListener {
            var distance: Long = 0
            var initTouchY : Float = 0f
            var touchY : Float = 0f
            var initTouchX : Float = 0f
            var touchX : Float = 0f

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event == null)
                    return false
                val xChanged = if (touchX != -1f && touchY != -1f) event.x - touchX else 0f
                val yChanged = if (touchX != -1f && touchY != -1f) event.y - touchY else 0f
                // coef is the gradient's move to determine a neutral zone
                val coef = abs(yChanged / xChanged)
                val xgesturesize = xChanged / screenConfig.metrics.xdpi * 2.54f
                val deltaY = ((abs(initTouchY - event.y) / screenConfig.metrics.xdpi + 0.5f) * 2f).coerceAtLeast(1f)
                if (event.action  == (MotionEvent.ACTION_DOWN)) {
                    distance = 0
                    initTouchY = event.y
                    initTouchX = event.x
                    touchY = initTouchY
                    touchX = event.x
                    seekText.setText("0")
                    seekText.visibility = View.VISIBLE
                }
                if (event.action  ==(MotionEvent.ACTION_MOVE)) {

                    doSeekTouch(deltaY.roundToInt(), xgesturesize, false)
                }
                if(event.action  ==(MotionEvent.ACTION_UP)){
                    seekText.setText("0")
                    doSeekTouch(deltaY.roundToInt(), xgesturesize, true)
                    seekText.visibility = View.GONE
                }
                return true
            }

        })
    }

    fun showInfo(text : String){
        seekText.setText(text)
    }
    abstract fun doSeekTouch(coef: Int, gesturesize: Float, seek: Boolean)

    protected fun showRetryView() {
        showRetryView(null)
    }

    protected fun showRetryView(retryTitle: String?) {
        retryView.visibility = VISIBLE

        if (retryTitle != null)
            retryViewTitle.text = retryTitle
    }

    protected fun hideRetryView() {
        retryView.visibility = GONE
    }

    protected fun showLoading() {
        hideRetryView()
    }

    protected fun hideLoading() {
        hideRetryView()
    }

    protected fun showController() {
        playerView.showController()
    }

    protected fun hideController() {
        playerView.hideController()
    }

    protected fun showUnMuteButton() {
        mute.visibility = GONE
        unMute.visibility = VISIBLE
    }

    protected fun showMuteButton() {
        mute.visibility = VISIBLE
        unMute.visibility = GONE
    }

    protected fun setShowSetting(showSetting: Boolean = false) {
        if (showSetting)
            settingContainer.visibility = VISIBLE
        else
            settingContainer.visibility = GONE
    }

}
data class ScreenConfig(val metrics: DisplayMetrics, val xRange: Int, val yRange: Int, val orientation: Int)