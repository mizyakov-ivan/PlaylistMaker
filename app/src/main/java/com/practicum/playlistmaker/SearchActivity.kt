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

class SearchActivity : AppCompatActivity() {

    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        val editText = findViewById<EditText>(R.id.editText)
        val clearButton = findViewById<ImageView>(R.id.clear_bottom)
        val arrowBackButton = findViewById<ImageView>(R.id.arrow_back)

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