package com.practicum.playlistmaker.media.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.TimeUtils.formatTrackDuraction
import com.practicum.playlistmaker.Constant.delayMillis
import com.practicum.playlistmaker.media.domain.model.Track
import com.practicum.playlistmaker.media.creator.CreatorMedia

class MediaActivity : AppCompatActivity(), MediaView {

    //Переменные
    lateinit var buttonArrowBackSearch: androidx.appcompat.widget.Toolbar
    lateinit var track: Track
    lateinit var artworkUrl100: ImageView
    lateinit var trackName: TextView
    lateinit var artistName: TextView
    lateinit var trackTime: TextView
    lateinit var collectionName: TextView
    lateinit var releaseDate: TextView
    lateinit var primaryGenreName: TextView
    lateinit var country: TextView
    lateinit var duration: TextView
    lateinit var previewUrl: String
    lateinit var buttonPlay: ImageView

    var handler = Handler(Looper.getMainLooper())

    private lateinit var mediaPresenter: MediaPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        //Присвоить значение переменным
        initViews()

        mediaPresenter = CreatorMedia.provideMediaPresenter(
            mediaRouter = MediaRouter(this),
            view = this
        )

        //Listener
        setListeners()
        //Отображение данных трека
        getInfoTrack()
        //Подготовка воспроизведения
        startPreparePlayer()
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
        pausePlayer()
        mediaPresenter.pausePlayer()
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        duration.text = "00:00"
        mediaPresenter.onViewDestroyed()
    }
    private fun initViews() {
        buttonArrowBackSearch = findViewById(R.id.toolbarSearch)
        artworkUrl100 = findViewById(R.id.artwork_url_100)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackTime = findViewById(R.id.lengthD)
        collectionName = findViewById(R.id.collectionNameV)
        releaseDate = findViewById(R.id.yearV)
        primaryGenreName = findViewById(R.id.genreV)
        country = findViewById(R.id.countryV)
        duration = findViewById(R.id.duration)
        buttonPlay = findViewById(R.id.playButton)
    }
    //Настроить Listeners
    private fun setListeners() {
        //Обработка нажатия на ToolBar "<-" и переход
        // на главный экран через закрытие экрана "Настройки"
        buttonArrowBackSearch.setOnClickListener() {
            handler.removeCallbacksAndMessages(null)
            mediaPresenter.clickArrowBack()
        }

        buttonPlay.setOnClickListener() {
            handler.removeCallbacksAndMessages(null)
            mediaPresenter.playbackControl()
        }
    }
    fun getInfoTrack() {
        mediaPresenter.loadInfoTrack()
    }
    override fun showDataTrack(track: Track) {
        previewUrl = track.previewUrl
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = formatTrackDuraction(track.trackTimeMillis.toInt())
        collectionName.text = track.collectionName
        releaseDate.text = track.releaseDate.substring(0..3)
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country
        duration.text = "00:00"

        val roundingRadius = this.resources.getDimensionPixelSize(R.dimen.s_padding)
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
            .into(artworkUrl100)
    }
    private fun startPreparePlayer() {
        mediaPresenter.startPreparePlayer()
    }

    override fun preparePlayer(){
        buttonPlay.isEnabled = true
        handler.removeCallbacksAndMessages(null)
        buttonPlay.setImageResource(R.drawable.play_button)
        duration.text = "00:00"
    }

    override fun startPlayer() {
        buttonPlay.setImageResource(R.drawable.pause_button)
        startTimer()
    }
    override fun pausePlayer() {
        buttonPlay.setImageResource(R.drawable.play_button)
    }

    private fun timerTrack(): Runnable {
        return object : Runnable {
            override fun run() {
                duration.text = mediaPresenter.getCurrentPosition()
                handler.postDelayed(this, delayMillis)
            }
        }
    }
    fun startTimer() {
        handler.post(timerTrack())
    }
}

