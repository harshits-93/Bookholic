package com.example.harsh.bookholics;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;


public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getName();

    private static final String GOOGLE_BOOKS_BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    private final static String PARAM_QUERY = "q";
    private final static String PARAM_MAX_RESULT = "maxResults";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Google Books dataset and return an {@link Book} object to represent a single book.
     */
    public static ArrayList<Book> fetchBookData(URL buildUrl) {

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(buildUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Book} object
        ArrayList<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the list
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    /*private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }
*/
    public static URL buildUrl(String userInput) {
        Uri builtUri = Uri.parse(GOOGLE_BOOKS_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, userInput)
                .appendQueryParameter(PARAM_MAX_RESULT, Integer.toString(15)).build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.e(LOG_TAG, "This is URL" + url);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            Scanner sc = new Scanner(inputStream);
            sc.useDelimiter("//A");
            if (sc.hasNext()) {
                output.append(sc.next());
            } else {
                return null;
            }

        }
        Log.e(LOG_TAG, "output" + output);
        return output.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing a JSON response.
     */

    private static ArrayList<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        ArrayList<Book> books = new ArrayList<>();

        try {
            // build up a list of Earthquake objects with the corresponding data.

            JSONObject jsonRootObject = new JSONObject(bookJSON);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonRootObject.optJSONArray("items");

            //loop through objects in JSONArray
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                JSONObject jsonVolumeInfoObject = jsonObject.getJSONObject("volumeInfo");
                String title = jsonVolumeInfoObject.optString("title");
                JSONObject imageLinksJsonObject = jsonVolumeInfoObject.getJSONObject("imageLinks");
                String imageUrl = imageLinksJsonObject.optString("smallThumbnail");

                String previewUrl = jsonVolumeInfoObject.optString("previewLink");
                JSONArray jsonArrayOfAuthors = jsonVolumeInfoObject.getJSONArray("authors");


                for (int j = 0; j < jsonArrayOfAuthors.length(); j++) {
                    String author = jsonArrayOfAuthors.optString(j);

                    books.add(new Book(title, author, imageUrl, previewUrl));
                }

            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        // Return the list of books
        return books;
    }


}
