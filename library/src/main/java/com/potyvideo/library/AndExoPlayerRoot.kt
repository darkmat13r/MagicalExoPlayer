package com.potyvideo.library

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
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


abstract class AndExoPlayerRoot @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {

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
            var lastEventY : Float = 0f
            var lastEventX : Float = 0f
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event == null)
                    return false
                if (event.action  == (MotionEvent.ACTION_DOWN)) {
                    distance = 0
                    lastEventX = event.getX(0)
                    lastEventY = event.getY(0)
                    seekText.setText("0")
                    seekText.visibility = View.VISIBLE
                }
                if (event.action  ==(MotionEvent.ACTION_MOVE)) {
                    val d = Math.sqrt(
                        Math.pow(
                            lastEventX - event.getX(0).toDouble(),
                            2.0
                        ) + Math.pow( lastEventY - event.getY(0).toDouble(), 2.0)
                    )
                    distance = d.toLong()


                    val direction = Math.atan2( event.getY(0).toDouble() - lastEventY ,   event.getX(0).toDouble() - lastEventX)
                    val stringBuilder = StringBuilder()
                    if(direction >  0){
                        distance = -distance;
                        stringBuilder.append("-")
                    }
                    val durationInMillis = Math.abs(distance*100)
                    val millis: Long = durationInMillis % 1000
                    val second: Long = durationInMillis / 1000 % 60
                    val minute: Long = durationInMillis / (1000 * 60) % 60
                    val hour: Long = durationInMillis / (1000 * 60 * 60) % 24
                    if(hour > 0){
                        stringBuilder.append("${String.format("%02d",hour)}:")
                    }
                    stringBuilder.append("${String.format("%02d",minute)}:")
                    stringBuilder.append(String.format("%02d",second))
                    seekText.setText(stringBuilder)
                }
                if(event.action  ==(MotionEvent.ACTION_UP)){
                    onSwipeSeek(distance*100)
                    seekText.setText("0")
                    lastEventY = 0f
                    lastEventX = 0f
                    seekText.visibility = View.GONE
                }
                return true
            }

        })
    }

    abstract fun onSwipeSeek(distance : Long)

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
