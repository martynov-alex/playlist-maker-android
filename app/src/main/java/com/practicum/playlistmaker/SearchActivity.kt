package com.practicum.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

    companion object {
        const val SEARCH_REQUEST = "SEARCH_REQUEST"
    }

    // Retrofit init
    private val retrofit =
        Retrofit.Builder().baseUrl(iTunesBaseUrl).addConverterFactory(GsonConverterFactory.create())
            .build()

    // Search service init
    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private lateinit var searchResultRv: RecyclerView
    private lateinit var nothingFoundPlaceholder: LinearLayout
    private lateinit var errorPlaceholder: LinearLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        val repeatRequestButton = findViewById<Button>(R.id.repeat_request_button)

        // Search results RecyclerView init
        searchResultRv = findViewById(R.id.search_result_rv)
        searchResultRv.layoutManager = LinearLayoutManager(this)
        searchResultRv.adapter = SearchResultAdapter(tracks)

        nothingFoundPlaceholder = findViewById(R.id.search_result_placeholder_nothing_found)
        errorPlaceholder = findViewById(R.id.search_result_placeholder_error)
        progressBar = findViewById(R.id.progressBar)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            inputEditText.hideKeyboard()
        }

        repeatRequestButton.setOnClickListener {
            showResult(RequestResult.LOADING)
            search(searchRequest)
        }

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
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                showResult(RequestResult.LOADING)
                search(searchRequest)
            }
            false
        }
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

    private fun View.hideKeyboard() {
        val imm = ContextCompat.getSystemService(
            context, InputMethodManager::class.java
        ) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
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
                nothingFoundPlaceholder.visibility = View.GONE
                errorPlaceholder.visibility = View.GONE
            }
            RequestResult.LOADING -> {
                searchResultRv.visibility = View.GONE
                progressBar.visibility = ProgressBar.VISIBLE
                nothingFoundPlaceholder.visibility = View.GONE
                errorPlaceholder.visibility = View.GONE
            }
            RequestResult.NOTHING_FOUND -> {
                searchResultRv.visibility = View.GONE
                progressBar.visibility = ProgressBar.GONE
                nothingFoundPlaceholder.visibility = View.VISIBLE
                errorPlaceholder.visibility = View.GONE
            }
            RequestResult.ERROR -> {
                searchResultRv.visibility = View.GONE
                progressBar.visibility = ProgressBar.GONE
                nothingFoundPlaceholder.visibility = View.GONE
                errorPlaceholder.visibility = View.VISIBLE
            }
        }
    }
}

enum class RequestResult { DONE, LOADING, NOTHING_FOUND, ERROR }