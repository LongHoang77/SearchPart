package com.example.searchpart;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Search_Activity extends AppCompatActivity {

    private EditText editTextSearch;
    private RecyclerView recyclerView;
    private EmailAdapter emailAdapter;
    private List<EmailItem> emailList;
    private List<EmailItem> filteredList;

    // Flag to control input state
    private boolean isInputEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize data
        emailList = new ArrayList<>();
        emailList.add(new EmailItem("Alice", "Meeting at 3PM"));
        emailList.add(new EmailItem("Bob", "Project Update"));
        emailList.add(new EmailItem("Charlie", "Birthday Invitation"));
        emailList.add(new EmailItem("Dave", "Weekly Report"));

        // Copy the original list to filteredList (this will be the one we modify for search results)
        filteredList = new ArrayList<>(emailList);

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emailAdapter = new EmailAdapter(filteredList);  // Set filteredList in adapter
        recyclerView.setAdapter(emailAdapter);

        // Initialize EditText
        editTextSearch = findViewById(R.id.editTextSearch);

        // Set cursor visibility to true initially
        editTextSearch.setCursorVisible(true);

        // Add TextWatcher to EditText to perform real-time search
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Perform filtering when text changes
                if (isInputEnabled) {
                    filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });

        // Set an OnEditorActionListener to detect the Enter key press
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                    // Hide the keyboard
                    hideKeyboard();

                    // Stop further input
                    isInputEnabled = false; // Disable input processing

                    // Stop showing the cursor
                    editTextSearch.setCursorVisible(false);

                    return true;  // Consume the event
                }
                return false;  // Allow default behavior for other actions
            }
        });

        // Add OnClickListener to EditText to re-enable input when clicked
        editTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Allow input again
                isInputEnabled = true; // Re-enable input processing
                // Show the keyboard
                showKeyboard();
                // Make the cursor visible
                editTextSearch.setCursorVisible(true);
            }
        });
    }

    // Filter method that will update the filtered list and notify the adapter
    private void filter(String query) {
        filteredList.clear(); // Clear the current filtered list

        if (query.isEmpty()) {
            // If search query is empty, show the full list
            filteredList.addAll(emailList);
        } else {
            // Otherwise, filter based on query
            for (EmailItem item : emailList) {
                if (item.getSender().toLowerCase().contains(query.toLowerCase()) ||
                        item.getSubject().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }

        // Notify the adapter that the data has changed
        emailAdapter.notifyDataSetChanged();
    }

    // Method to hide the keyboard
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Method to show the keyboard
    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editTextSearch, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
