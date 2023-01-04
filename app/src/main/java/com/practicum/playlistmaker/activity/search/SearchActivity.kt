package com.practicum.playlistmaker.activity.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.hideKeyboard
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.repository.ITunesApi
import com.practicum.playlistmaker.repository.TracksResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private var searchRequest: String = ""
    private val tracks = ArrayList<Track>()

    enum class SearchScreenState { TRACKS, LOADING, NOTHING_FOUND, ERROR }

    companion object {
        const val SEARCH_REQUEST = "SEARCH_REQUEST"
    }

    // Инициализация Retrofit
    private val retrofit =
        Retrofit.Builder().baseUrl(iTunesBaseUrl).addConverterFactory(GsonConverterFactory.create())
            .build()

    // Инициализация сервиса по работе с iTunes API
    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private lateinit var searchInputField: EditText
    private lateinit var clearButton: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchResultRv: RecyclerView
    private lateinit var searchResultPlaceholder: LinearLayout
    private lateinit var searchResultPlaceholderIcon: ImageView
    private lateinit var searchResultPlaceholderText: TextView
    private lateinit var searchResultPlaceholderReloadButton: Button

    private lateinit var searchHistory: SearchHistory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initVariables()
        initListeners()

        // Загрузка истории поиска из памяти (Shared Preferences)
        searchHistory.loadHistory()

        searchResultRv.layoutManager = LinearLayoutManager(this)
        searchResultRv.adapter = SearchResultAdapter(tracks, false)

        val searchFormTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchRequest = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        searchInputField.addTextChangedListener(searchFormTextWatcher)
    }

    override fun onPause() {
        super.onPause()
        searchHistory.saveHistory()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val searchInputField = findViewById<EditText>(R.id.search_input_field)
        searchRequest = savedInstanceState.getString(SEARCH_REQUEST, "")
        Log.d("Search activity", "Restore $searchRequest")

        searchInputField.setText(searchRequest)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_REQUEST, searchRequest)
        Log.d("Search activity", "Save $searchRequest")
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun search(searchRequest: String) {
        iTunesService.search(searchRequest).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(
                call: Call<TracksResponse>, response: Response<TracksResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.clear()
                            tracks.addAll(response.body()?.results!!)
                            searchResultRv.adapter = SearchResultAdapter(tracks, false)
                            showResult(SearchScreenState.TRACKS)
                        } else {
                            showResult(SearchScreenState.NOTHING_FOUND)
                        }
                    }
                    else -> {
                        showResult(SearchScreenState.ERROR)
                    }
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                showResult(SearchScreenState.ERROR)
            }

        })
    }

    private fun showResult(result: SearchScreenState) {
        when (result) {
            SearchScreenState.TRACKS -> {
                searchResultRv.visibility = View.VISIBLE
                progressBar.visibility = ProgressBar.GONE
                searchResultPlaceholder.visibility = View.GONE
            }
            SearchScreenState.LOADING -> {
                searchResultRv.visibility = View.GONE
                progressBar.visibility = ProgressBar.VISIBLE
                searchResultPlaceholder.visibility = View.GONE
            }
            SearchScreenState.NOTHING_FOUND -> {
                searchResultPlaceholderIcon.setImageResource(R.drawable.search_result_placeholder_nothing_found_icon)
                searchResultPlaceholderText.setText(R.string.search_result_placeholder_nothing_found)
                searchResultPlaceholderReloadButton.visibility = View.GONE

                searchResultRv.visibility = View.GONE
                progressBar.visibility = ProgressBar.GONE
                searchResultPlaceholder.visibility = View.VISIBLE
            }
            SearchScreenState.ERROR -> {
                searchResultPlaceholderIcon.setImageResource(R.drawable.search_result_placeholder_error_icon)
                searchResultPlaceholderText.setText(R.string.search_result_placeholder_error)
                searchResultPlaceholderReloadButton.visibility = View.VISIBLE

                searchResultRv.visibility = View.GONE
                progressBar.visibility = ProgressBar.GONE
                searchResultPlaceholder.visibility = View.VISIBLE
            }
        }
    }

    private fun initVariables() {
        searchInputField = findViewById(R.id.search_input_field)
        clearButton = findViewById(R.id.clear_button)
        progressBar = findViewById(R.id.progress_bar)
        searchResultRv = findViewById(R.id.search_result_rv)
        searchResultPlaceholder = findViewById(R.id.search_result_placeholder)
        searchResultPlaceholderIcon = findViewById(R.id.search_result_placeholder_icon)
        searchResultPlaceholderText = findViewById(R.id.search_result_placeholder_text)
        searchResultPlaceholderReloadButton =
            findViewById(R.id.search_result_placeholder_reload_button)

        // Создание экземпляра сервиса по работе с историей поиска
        searchHistory = SearchHistory()
    }

    private fun initListeners() {
        clearButton.setOnClickListener {
            searchInputField.setText("")
            searchInputField.hideKeyboard()
            tracks.clear()
            if (searchHistory.historyTracks.isNotEmpty()) {
                searchResultRv.adapter =
                    SearchResultAdapter(searchHistory.historyTracks.reversed(), true)
            } else {
                searchResultRv.adapter = SearchResultAdapter(tracks, false)
            }
            showResult(SearchScreenState.TRACKS)
        }

        searchResultPlaceholderReloadButton.setOnClickListener {
            showResult(SearchScreenState.LOADING)
            search(searchRequest)
        }

        searchInputField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                showResult(SearchScreenState.LOADING)
                search(searchRequest)
            }
            false
        }

        searchInputField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchHistory.historyTracks.isNotEmpty()) {
                searchResultRv.adapter =
                    SearchResultAdapter(searchHistory.historyTracks.reversed(), true)
                showResult(SearchScreenState.TRACKS)
            }
        }
    }

    fun addTrackToHistory(track: Track) {
        searchHistory.addTrack(track)
    }

    fun clearHistoryTracks() {
        searchHistory.clearHistory()
        tracks.clear()
        searchResultRv.adapter = SearchResultAdapter(tracks, false)
        showResult(SearchScreenState.TRACKS)
    }
}

