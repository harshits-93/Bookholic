package com.example.harsh.bookholics;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {
    public BookAdapter(@NonNull Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        Book currentBook = getItem(position);
        String bookTitle = currentBook.getmTitle();
        String bookAuthor = currentBook.getmAuthor();
        String imageUrl = currentBook.getmImageUrl();
        String previewUrl = currentBook.getmPreviewUrl();

        TextView titleTextView = listItemView.findViewById(R.id.title);
        TextView authorTextView = listItemView.findViewById(R.id.author);
        ImageView bookImage = listItemView.findViewById(R.id.image);

        bookImage.setImageURI(Uri.parse(imageUrl));
        titleTextView.setText(bookTitle);
        authorTextView.setText(bookAuthor);

        return listItemView;
    }
}
