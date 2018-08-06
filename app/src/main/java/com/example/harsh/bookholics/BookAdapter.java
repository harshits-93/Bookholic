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
            //We must create or inflate a new list item here
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        /*Getting Book object in ArrayAdapter a.k.a BookAdapter
         based on its index or position and store it in currentBook variable.
         */
        Book currentBook = getItem(position);

        //Retrieving information such as title,author and imageUrl from that object.
        String bookTitle = currentBook.getmTitle();
        String bookAuthor = currentBook.getmAuthor();
        String imageUrl = currentBook.getmImageUrl();

        //Getting references of title,author,image from the ListItem which is being clicked
        TextView titleTextView = listItemView.findViewById(R.id.title);
        TextView authorTextView = listItemView.findViewById(R.id.author);
        ImageView bookImage = listItemView.findViewById(R.id.image);


        // Setting author,title,image on clicked ListItem
        bookImage.setImageURI(Uri.parse(imageUrl));
        titleTextView.setText(bookTitle);
        authorTextView.setText(bookAuthor);

        return listItemView;
    }
}
