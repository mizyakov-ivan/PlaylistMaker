package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Track
import com.example.playlistmaker.TrackAdapter

class SearchActivity : AppCompatActivity() {

    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        val editText = findViewById<EditText>(R.id.editText)
        val clearButton = findViewById<ImageView>(R.id.clear_bottom)
        val arrowBackButton = findViewById<ImageView>(R.id.arrow_back)

        val trackList: ArrayList<Track> = ArrayList()
        trackList.add(Track("Smells Like Teen Spirit", "Nirvana", "5:01", getString(R.string.smells_like)))
        trackList.add(Track("Billie Jean", "Michael Jackson", "4:35", getString(R.string.billie_jean)))
        trackList.add(Track("Stayin' Alive", "Bee Gees", "4:10", getString(R.string.stayin_alive)))
        trackList.add(Track("Whole Lotta Love", "Led Zeppelin", "5:33", getString(R.string.whole_lotta)))
        trackList.add(Track("Sweet Child O'Mine", "Guns N' Roses", "5:03", getString(R.string.sweet_child)))

        arrowBackButton.setOnClickListener {
            finish()
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchQuery = editText.text.toString()
                clearButton.isVisible = searchQuery.isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        clearButton.isVisible = false

        clearButton.setOnClickListener {
            editText.setText("")
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }
        val tracksRecycler = findViewById<RecyclerView>(R.id.track_recycler)

        tracksRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksRecycler.adapter = TrackAdapter(trackList)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("searchQuery", searchQuery)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString("searchQuery", "")
        val editText = findViewById<EditText>(R.id.editText)
        editText.setText(searchQuery)
    }
}