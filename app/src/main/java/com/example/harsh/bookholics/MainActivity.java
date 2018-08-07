package com.example.harsh.bookholics;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final int BOOK_LOADER_ID = 1;

    //Global variable for complete url
    URL booksUrl;

    //TextView to be shown whenever there is no item on the screen
    TextView mEmptyView;

    //A Progress Bar which is sown while loading takes place.
    ProgressBar mLoadingIndicator;

    //Search View instance
    SearchView mSearchView;

    //Variable to check Internet connection
    boolean isConnected;

    //Instance of BookAdapter class
    private BookAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declaration and initialization of ConnectivityManager for checking internet connection
        final ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        checkInternetConnection(cm);

//        if (isConnected) {
//            getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);
//        } else {
//            mLoadingIndicator.setVisibility(View.GONE);
//            mEmptyView.setText("No Internet Connectivity");
//        }

        //Getting references of required xml elements
        mLoadingIndicator = findViewById(R.id.loading_indicator);
        mEmptyView = findViewById(R.id.emptyView);
        mSearchView = findViewById(R.id.querySearchView);

        /*Setting an Empty view in the ListView so that it
        is shown when there is no item in the list
        */
        ListView listView = findViewById(R.id.listView);
        listView.setEmptyView(mEmptyView);

        //Attaching listView with BookAdapter
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

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                checkInternetConnection(cm);
                if (isConnected) {
                    //Initializing the loader
                    getLoaderManager().initLoader(BOOK_LOADER_ID, null, MainActivity.this);
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                } else {
                    // Clear the adapter of previous book data
                    mAdapter.clear();
                    // Set mEmptyStateTextView visible
                    mLoadingIndicator.setVisibility(View.GONE);
                    mEmptyView.setText(R.string.no_internet);
                }


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mEmptyView.setVisibility(View.GONE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
                //getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                return true;
            }
        });

    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        if (TextUtils.isEmpty(mSearchView.getQuery().toString().trim())) {
            return null;
        } else {
            //Build complete url based on user input
            booksUrl = QueryUtils.buildUrl(mSearchView.getQuery().toString().trim());
            //create new loader with url passed to it.
            return new BooksLoader(this, booksUrl);
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        mLoadingIndicator.setVisibility(View.GONE);

        //To clear all the previous content in adapter so as to load new data
        mAdapter.clear();

        if (mAdapter != null && !mAdapter.isEmpty()) {
            mAdapter.addAll(books);
        }

        // Set empty state text to display "No books found."
        mEmptyView.setText(R.string.no_books);

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
