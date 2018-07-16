package com.example.harsh.bookholics;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.net.URL;
import java.util.List;

public class BooksLoader extends AsyncTaskLoader<List<Book>> {
    private URL mURL;

    public BooksLoader(Context context, URL url) {
        super(context);
        mURL = url;
    }

    @Override
    public List<Book> loadInBackground() {
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
