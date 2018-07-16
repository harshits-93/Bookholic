package com.example.harsh.bookholics;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final int BOOK_LOADER_ID = 1;
    URL booksUrl;
    TextView mEmptyView;
    ProgressBar mLoadingIndicator;
    //search button instance
    Button mSearchButton;
    //Edit text in which user inputs his query
    EditText mQueryEditText;
    //Variable to check Internet connection
    boolean isConnected;
    private BookAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declaration and initialization of ConnectivityManager for checking internet connection
        final ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        checkInternetConnection(cm);

        //Getting references of required xml elements
        mLoadingIndicator = findViewById(R.id.loading_indicator);
        mEmptyView = findViewById(R.id.emptyView);
        mQueryEditText = findViewById(R.id.queryEditText);
        mSearchButton = findViewById(R.id.searchButton);

        ListView listView = findViewById(R.id.listView);
        listView.setEmptyView(mEmptyView);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        listView.setAdapter(mAdapter);

        //Defining what happens when user click a single list item view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowId) {
                Book bookItemClicked = mAdapter.getItem(position);
                Uri bookPreviewUri = Uri.parse(bookItemClicked.getmPreviewUrl());

                Intent bookPreviewPageIntent = new Intent(Intent.ACTION_VIEW, bookPreviewUri);
                if (bookPreviewPageIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(bookPreviewPageIntent);

                }

            }
        });

        //Defining what happens when user click the search button
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInternetConnection(cm);
                if (isConnected) {
                    // Update URL and restart loader to displaying new result of searching

                    mQueryEditText.setVisibility(View.GONE);
                    mLoadingIndicator.setVisibility(View.VISIBLE);

                    String userQuery = mQueryEditText.getText().toString().trim();
                    booksUrl = QueryUtils.buildUrl(userQuery);
                    getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                } else {
                    // Clear the adapter of previous book data
                    mAdapter.clear();
                    // Set mEmptyStateTextView visible
                    mLoadingIndicator.setVisibility(View.GONE);
                    mEmptyView.setText(R.string.no_internet);
                }

            }
        });


        if (isConnected) {
            getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);
        } else {
            mLoadingIndicator.setVisibility(View.GONE);
            mEmptyView.setText("No Internet Connectivity");
        }

    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        booksUrl = QueryUtils.buildUrl(mQueryEditText.getText().toString());
        return new BooksLoader(this, booksUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        mQueryEditText.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.GONE);
        // Set empty state text to display "No books found."
        mEmptyView.setText(R.string.no_books);
        mAdapter.clear();

        if (mAdapter != null && !mAdapter.isEmpty()) {
            mAdapter.addAll(books);
        }
        mEmptyView.setText("No books found");
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }

    private void checkInternetConnection(ConnectivityManager cm) {
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting())
            isConnected = true;
        else
            isConnected = false;


    }

}