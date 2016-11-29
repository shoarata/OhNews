package com.example.android.ohnews;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import static android.R.attr.resource;

/**
 * Created by arata on 29/11/2016.
 */

public class NewsAdapter extends ArrayAdapter<News> {
    ArrayList<News> news;
    public NewsAdapter(Context context, ArrayList<News> news) {
        super(context, 0);
        this.news = news;
    }
}
