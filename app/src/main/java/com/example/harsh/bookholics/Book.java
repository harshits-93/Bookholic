package com.example.harsh.bookholics;

public class Book {
    private String mTitle;
    private String mAuthor;
    private String mImageUrl;
    private String mPreviewUrl;

    public Book(String mTitle, String mAuthor, String mUrl, String mPreviewUrl) {
        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
        this.mImageUrl = mUrl;
        this.mPreviewUrl = mPreviewUrl;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public String getmPreviewUrl() {
        return mPreviewUrl;
    }
}
