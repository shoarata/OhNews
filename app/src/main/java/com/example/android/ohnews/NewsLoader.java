package com.example.android.ohnews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by arata on 29/11/2016.
 */

public class NewsLoader extends AsyncTaskLoader<ArrayList<News>> {

    String queryUrl;
    public NewsLoader(Context context, String query) {
        super(context);
        queryUrl = query;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<News> loadInBackground() {
        ArrayList<News> news;
        news = QueryUtils.fetchNews(queryUrl);
        return news;
    }
}
