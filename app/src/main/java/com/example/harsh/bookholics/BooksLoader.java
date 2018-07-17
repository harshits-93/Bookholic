package com.example.harsh.bookholics;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.net.URL;
import java.util.List;

public class BooksLoader extends AsyncTaskLoader<List<Book>> {
    private URL mURL;
    private static final String LOG_TAG = BooksLoader.class.getName();

    public BooksLoader(Context context, URL url) {
        super(context);
        mURL = url;
    }

    @Override
    public List<Book> loadInBackground() {
        Log.e(LOG_TAG, "IN LOAD IN BACKGROUND");
        if (mURL == null) {
            return null;
        }
        List<Book> booksList = QueryUtils.fetchBookData(mURL);
        return booksList;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
