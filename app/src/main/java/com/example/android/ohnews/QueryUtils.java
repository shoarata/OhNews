package com.example.android.ohnews;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static android.R.id.input;
import static android.content.ContentValues.TAG;

/**
 * Created by arata on 29/11/2016.
 */

public class QueryUtils {
    final static String  GUARDIAN_QUERY_URL = "http://content.guardianapis.com/search?&api-key=7aac4d65-c46e-472f-81c3-75619f9e34df";
    //!!! tools for makeing data requests
    /**
     * !!!
     * return query string replacing white spaces with OR
     */
    public static String getQuery(String query) {
       return query.replaceAll(" ","%20%OR%20%");
    }

    /**
     * fetch news from the Guardian API
     */
    public static ArrayList<News> fetchNews(String stringUrl){
        // create url object
        URL url = createUrl(stringUrl);
        // try to get the JSON response from server
        String JSONstring = "";
        try {
            JSONstring = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // extract News from JSON response and return
        return extractNewsFromJson(JSONstring);
    }

    /**
     * extracts News from Json String and returns an array of news
     */
    private static ArrayList<News> extractNewsFromJson(String JSONstring){
        if(JSONstring.isEmpty()){
            return null;
        }
        ArrayList<News> news = new ArrayList<>();
        try {
            JSONObject output = new JSONObject(JSONstring);
            JSONArray jsonNews = output.getJSONObject("response").getJSONArray("results");
            JSONObject current;
            for(int i =0; i < jsonNews.length(); i ++){
                current = jsonNews.getJSONObject(i);
                news.add(new News(current.getString("webTitle"),current.getString("sectionName"),current.getString("webUrl"),current.getString("webPublicationDate")));
            }
        } catch (JSONException e) {
            Log.e(TAG,"problem parsing jason string to News ");
        }
        return news;
    }
    /**
     * parse the input stream to a String
     * @param inputStream
     * @return
     */
    private static String readFromStream(InputStream inputStream){
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader  bufferReader = new BufferedReader(reader);
            try {
                String line = bufferReader.readLine();
                while(line != null){
                    output.append(line);
                    line = bufferReader.readLine();
                }
            } catch (IOException e)  {
                Log.e(TAG,"error trying to parse inputstream to String");
            }
        }
        return output.toString();
    }
    /**
     * extracts response of the server and returns it as a string
     * @param url
     * @return
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String JSONresponse= "";
        if(url == null){
            return JSONresponse;
        }

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            //getting the output
            if(httpURLConnection.getResponseCode() == 200){
                inputStream = httpURLConnection.getInputStream();
                JSONresponse = readFromStream(inputStream);
            }
            else{
                Log.d(TAG, "Error response code : " + httpURLConnection.getResponseCode());
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "error when trying to make the connection");
        }
        finally {
            if(httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return JSONresponse;
    }
    /**
     * creates a url object from a string url
     * @param stringUrl
     * @return
     */
    private static URL createUrl(String stringUrl){
        try {
            URL url = new URL(stringUrl);
            return url;
        } catch (MalformedURLException e) {
            Log.e("URL", "problems creating the url object");
            return null;
        }
    }
}

