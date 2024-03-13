package com.app.taocalligraphy.ui.meditation_timer.dialog

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.view.LayoutInflater
import android.widget.Toast
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogMeditationMusicBinding
import com.app.taocalligraphy.models.response.meditation_timer.SoundApiResponse
import com.app.taocalligraphy.ui.meditation_timer.adapter.MeditationMusicAdapter
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.google.android.material.bottomsheet.BottomSheetDialog

class MeditationMusicSelectionDialog(
    context: Context,
    private val selectedSoundId: Int,
    var arrayList: ArrayList<SoundApiResponse.SoundList?>,
    soundSelectionListener: SoundSelectionListener
) : BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme),
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener {

    var mediaPlayer: MediaPlayer? = null
    var contextLang: Context = context
    val listener: SoundSelectionListener = soundSelectionListener
    var meditationMusicAdapter = MeditationMusicAdapter(mutableListOf())
    var selectedPosition = -1

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkInfo = cm.activeNetworkInfo

    init {
        initView()
    }

    private fun initView() {
        val itemBindingUtil =
            DialogMeditationMusicBinding.inflate(
                LayoutInflater.from(contextLang),
                null, false
            )
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(true)
        if (arrayList.size > 0) {
            itemBindingUtil.rvSound.visible()
            itemBindingUtil.tvSoundNotFound.gone()
        } else {
            itemBindingUtil.rvSound.gone()
            itemBindingUtil.tvSoundNotFound.visible()
        }
        itemBindingUtil.rvSound.adapter = meditationMusicAdapter
        if (selectedSoundId >= 0) {
            arrayList.map {
                if (it?.soundId == selectedSoundId) {
                    it.isSelected = true
                    //setMediaPlayer(it.sound!!)
                } else {
                    it?.isSelected = false
                }
            }
        }
        meditationMusicAdapter.updateList(arrayList)
        meditationMusicAdapter.select = object : MeditationMusicAdapter.OnSelectSoundItem {
            override fun onSelectSoundItem(position: Int) {
                for (i in arrayList.indices) {
                    if (i == position) {
                        arrayList[position]?.isSelected = true
                        meditationMusicAdapter.notifyItemChanged(position)
                    } else {
                        if (arrayList[i]?.isSelected!!)
                            meditationMusicAdapter.notifyItemChanged(i)
                        arrayList[i]?.isSelected = false
                    }
                }
                selectedPosition = position
                mediaPlayer?.stop()
                arrayList[position]?.sound?.let { setMediaPlayer(it) }
            }
        }

        itemBindingUtil.btnDone.setOnClickListener {
            mediaPlayer?.stop()
            if (arrayList.size > 0) {
                if (selectedPosition >= 0)
                    arrayList[selectedPosition]?.let { it1 -> listener.onSoundSelect(it1) }
            }
            dismiss()
        }
        setOnDismissListener {
            mediaPlayer?.stop()
        }
    }

    private fun setMediaPlayer(sound: String) {
        /*if (mediaPlayer != null)
            mediaPlayer = null*/

        mediaPlayer?.reset()

        // if network is NOT available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected && networkInfo.isConnectedOrConnecting) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
                val b = AudioAttributes.Builder()
                b.setUsage(AudioAttributes.USAGE_MEDIA)
                b.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                mediaPlayer?.setAudioAttributes(b.build())
                mediaPlayer?.setOnPreparedListener(this)
                mediaPlayer?.setOnCompletionListener(this)
                if (sound.isNotBlank()) {
                    try {
                        mediaPlayer?.setDataSource(sound)
                        mediaPlayer?.prepareAsync()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                if (sound.isNotBlank()) {
                    try {
                        mediaPlayer?.setDataSource(sound)
                        mediaPlayer?.prepareAsync()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.no_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    interface SoundSelectionListener {
        fun onSoundSelect(data: SoundApiResponse.SoundList)
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mediaPlayer?.start()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        mediaPlayer?.stop()
    }

    override fun setCanceledOnTouchOutside(cancel: Boolean) {
        mediaPlayer?.stop()
        super.setCanceledOnTouchOutside(cancel)
    }
}