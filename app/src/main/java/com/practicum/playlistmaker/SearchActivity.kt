package com.practicum.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    private var searchRequest: String = ""

    companion object {
        const val SEARCH_REQUEST = "SEARCH_REQUEST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearButton)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            inputEditText.hideKeyboard()
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

        // Search results RecyclerView init
        val recycler = findViewById<RecyclerView>(R.id.search_result_rv)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = SearchResultAdapter(MockTracks.list)
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
            context,
            InputMethodManager::class.java
        ) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}