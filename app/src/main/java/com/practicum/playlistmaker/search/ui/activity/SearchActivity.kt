package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.search.ui.adapter.TrackAdapter
import com.practicum.playlistmaker.search.ui.adapter.TrackHistoryAdapter
import com.practicum.playlistmaker.search.ui.models.SearchStateInterface
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.search.ui.router.SearchNavigationRouter

import org.koin.androidx.viewmodel.ext.android.viewModel
class SearchActivity : AppCompatActivity() {

    //Переменные для работы с UI
    lateinit var searchEditText: EditText
    lateinit var searchClearIcon: ImageView
    lateinit var buttonArrowBackSettings: androidx.appcompat.widget.Toolbar
    lateinit var tracksAdapter: TrackAdapter
    lateinit var tracksHistoryAdapter: TrackHistoryAdapter
    lateinit var placeholderNothingWasFound: LinearLayout
    lateinit var placeholderCommunicationsProblem: LinearLayout
    lateinit var historyList: LinearLayout
    lateinit var buttonClear: Button
    lateinit var sharedPref: SharedPreferences
    lateinit var progressBar: ProgressBar
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewHistory: RecyclerView
    lateinit var titleHistoryTextView: TextView


    private val searchViewModel: SearchViewModel by viewModel()
    private val searchNavigationRouter = SearchNavigationRouter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()

        initAdapter()

        searchViewModel.observeState().observe(this) {
            render(it)
        }

        //Listener
        setListeners()

        visibleHistoryTrack()
    }

    private fun initViews() {
        buttonArrowBackSettings = findViewById(R.id.toolbarSetting)
        searchClearIcon = findViewById(R.id.searchClearIcon)
        searchEditText = findViewById(R.id.searchEditText)
        placeholderNothingWasFound = findViewById<View>(R.id.not_found) as LinearLayout
        placeholderCommunicationsProblem = findViewById<View>(R.id.no_internet) as LinearLayout
        historyList = findViewById(R.id.history_list)
        buttonClear = findViewById(R.id.clear_history)
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory)
        searchEditText.setText("")
        titleHistoryTextView = findViewById(R.id.title_history)
    }

    private fun textSearch(): String {
        return searchEditText.getText().toString()
    }

    private fun initAdapter() {
        tracksAdapter = TrackAdapter(ArrayList<Track>())
        recyclerView.adapter = tracksAdapter
        tracksHistoryAdapter = TrackHistoryAdapter(ArrayList<Track>())
        recyclerViewHistory.adapter = tracksHistoryAdapter
    }

    private fun setListeners() {

        buttonArrowBackSettings.setOnClickListener() {
            searchNavigationRouter.backView()
        }

        //Очистка истории поиска
        buttonClear.setOnClickListener() {
            searchViewModel.clickButtonClearHistory()
        }
        //Поиск трека
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && !textSearch().equals("")) {
                searchTracks()
                true
            }
            false
        }
        //Очистить строку поиска
        searchClearIcon.setOnClickListener {
            searchViewModel.clearSearchText()

        }
        //Связать поля для ввода и TextWatcher
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            //Действие при вводе текста
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

                searchViewModel.onTextChanged(
                    changedText = s?.toString() ?: "",
                    focus = true
                )
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        tracksAdapter.itemClickListener = { position, track ->
            searchViewModel.onTrackClick(track, position)
            searchNavigationRouter.sendToMedia(track)
        }
        //Обработать нажатие на View трека в истории поиска
        tracksHistoryAdapter.itemClickListener = { position, track ->
            searchViewModel.onTrackClick(track, position)
            searchNavigationRouter.sendToMedia(track)
            searchViewModel.visibleHistoryTrack()
        }
    }
    private fun render(state: SearchStateInterface) {
        when (state) {
            is SearchStateInterface.Loading -> showLoading()
            is SearchStateInterface.changeTextSearch -> showChangeTextSearch()
            is SearchStateInterface.SearchTracks -> showSearchTracks(state.tracks)
            is SearchStateInterface.HistoryTracks -> showHistoryTracks(state.tracks)
            is SearchStateInterface.Error -> showMessageError(state.error)
        }
    }

    private fun showLoading() {
        hideKeyboard()
        hideMessageError()
        showLoad()
        hideHistoryList()
        buttonClear.isEnabled = false
    }

    private fun showChangeTextSearch() {
        hideMessageError()
        hideLoad()
        showClearIcon()
        buttonClear.isEnabled = true
    }

    private fun showSearchTracks(tracks: List<Track>) {
        hideLoad()
        hideKeyboard()
        showTracks(tracks)
        hideMessageError()
        hideHistoryList()
        buttonClear.isEnabled = true
    }

    private fun showHistoryTracks(tracks: List<Track>) {
        hideLoad()
        hideKeyboard()
        hideMessageError()
        showTracks(emptyList())
        clearTextSearch()
        hideClearIcon()
        buttonClear.isEnabled = true
        if (tracks.isNotEmpty()) {
            showHistoryList()
        } else
            hideHistoryList()
        refreshHistory(tracks)
    }

    private fun showMessageError(networkError: NetworkError) {
        hideLoad()
        hideKeyboard()
        hideHistoryList()
        buttonClear.isEnabled = true
        when (networkError) {
            is NetworkError.NoData -> {
                placeholderNothingWasFound.isVisible = true
                placeholderCommunicationsProblem.isVisible = false
            }

            is NetworkError.NoInternet -> {
                placeholderCommunicationsProblem.isVisible = true
                placeholderNothingWasFound.isVisible = false
            }

            else -> {
                placeholderNothingWasFound.isVisible = true
                placeholderCommunicationsProblem.isVisible = false
            }
        }
    }

    private fun showLoad() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoad() {
        progressBar.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        //Скрыть клавиатуру
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun searchTracks() {
        searchViewModel.loadTracks(textSearch())
    }

    private fun refreshHistory(tracks: List<Track>) {
        tracksHistoryAdapter.setTracks(tracks)
    }

    private fun clearTextSearch() {
        searchEditText.setText("")
    }

    //Отображение истории поиска
    private fun visibleHistoryTrack() {
        searchViewModel.visibleHistoryTrack()
    }

    private fun hideMessageError() {
        placeholderNothingWasFound.isVisible = false
        placeholderCommunicationsProblem.isVisible = false
    }

    private fun showHistoryList() {
        historyList.visibility = View.VISIBLE
    }

    private fun hideHistoryList() {
        historyList.visibility = View.GONE
    }

    private fun showTracks(tracks: List<Track>) {
        tracksAdapter.setTracks(tracks)
    }

    private fun hideClearIcon() {
        searchClearIcon.visibility = View.GONE
    }
    private fun showClearIcon() {
        searchClearIcon.visibility = View.VISIBLE
    }
}