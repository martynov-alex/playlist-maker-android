package com.practicum.playlistmaker

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private var searchRequest: String = ""
    private val tracks = ArrayList<Track>()

    enum class RequestResult { DONE, LOADING, NOTHING_FOUND, ERROR }

    companion object {
        const val SEARCH_REQUEST = "SEARCH_REQUEST"
    }

    // Retrofit init
    private val retrofit =
        Retrofit.Builder().baseUrl(iTunesBaseUrl).addConverterFactory(GsonConverterFactory.create())
            .build()

    // Search service init
    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchResultRv: RecyclerView
    private lateinit var searchResultPlaceholder: LinearLayout
    private lateinit var searchResultPlaceholderIcon: ImageView
    private lateinit var searchResultPlaceholderText: TextView
    private lateinit var searchResultPlaceholderButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViewVariables()
        initListeners()

        searchResultRv.layoutManager = LinearLayoutManager(this)
        searchResultRv.adapter = SearchResultAdapter(tracks)

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

        inputEditText.addTextChangedListener(searchFormTextWatcher)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        searchRequest = savedInstanceState.getString(SEARCH_REQUEST, "")
        Log.d("Search activity", "Restore $searchRequest")

        inputEditText.setText(searchRequest)
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
                            searchResultRv.adapter?.notifyDataSetChanged()
                            showResult(RequestResult.DONE)
                        } else {
                            showResult(RequestResult.NOTHING_FOUND)
                        }
                    }
                    else -> {
                        showResult(RequestResult.ERROR)
                    }
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                showResult(RequestResult.ERROR)
            }

        })
    }

    private fun showResult(result: RequestResult) {
        when (result) {
            RequestResult.DONE -> {
                searchResultRv.visibility = View.VISIBLE
                progressBar.visibility = ProgressBar.GONE
                searchResultPlaceholder.visibility = View.GONE
            }
            RequestResult.LOADING -> {
                searchResultRv.visibility = View.GONE
                progressBar.visibility = ProgressBar.VISIBLE
                searchResultPlaceholder.visibility = View.GONE
            }
            RequestResult.NOTHING_FOUND -> {
                searchResultPlaceholderIcon.setImageResource(R.drawable.search_result_placeholder_nothing_found_icon)
                searchResultPlaceholderText.setText(R.string.search_result_placeholder_nothing_found)
                searchResultPlaceholderButton.visibility = View.GONE

                searchResultRv.visibility = View.GONE
                progressBar.visibility = ProgressBar.GONE
                searchResultPlaceholder.visibility = View.VISIBLE
            }
            RequestResult.ERROR -> {
                searchResultPlaceholderIcon.setImageResource(R.drawable.search_result_placeholder_error_icon)
                searchResultPlaceholderText.setText(R.string.search_result_placeholder_error)
                searchResultPlaceholderButton.visibility = View.VISIBLE

                searchResultRv.visibility = View.GONE
                progressBar.visibility = ProgressBar.GONE
                searchResultPlaceholder.visibility = View.VISIBLE
            }
        }
    }

    private fun initViewVariables() {
        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearButton)
        progressBar = findViewById(R.id.progressBar)
        searchResultRv = findViewById(R.id.search_result_rv)
        searchResultPlaceholder = findViewById(R.id.search_result_placeholder)
        searchResultPlaceholderIcon = findViewById(R.id.search_result_placeholder_icon)
        searchResultPlaceholderText = findViewById(R.id.search_result_placeholder_text)
        searchResultPlaceholderButton = findViewById(R.id.search_result_placeholder_button)
    }

    private fun initListeners() {
        clearButton.setOnClickListener {
            inputEditText.setText("")
            inputEditText.hideKeyboard()
            tracks.clear()
            searchResultRv.adapter?.notifyDataSetChanged()
        }

        searchResultPlaceholderButton.setOnClickListener {
            showResult(RequestResult.LOADING)
            search(searchRequest)
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                showResult(RequestResult.LOADING)
                search(searchRequest)
            }
            false
        }
    }
}

