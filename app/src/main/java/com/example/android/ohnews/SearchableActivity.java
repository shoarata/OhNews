package com.example.android.ohnews;

import android.app.DownloadManager;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.LoaderManager.LoaderCallbacks;

import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.view.View.GONE;
import static com.example.android.ohnews.QueryUtils.getQuery;

public class SearchableActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<News>> {
    ListView listView;
    NewsAdapter newsAdapter;
    String query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //reuse activity main xml hiding the FloatingActionButton
        setContentView(R.layout.activity_main);
        ((FloatingActionButton)findViewById(R.id.search_FAB)).setVisibility(GONE);

        // setting up list view with adapter and its empty view
        listView = (ListView) findViewById(R.id.list_view);
        newsAdapter = new NewsAdapter(this,new ArrayList<News>());
        listView.setAdapter(newsAdapter);
        listView.setEmptyView(findViewById(R.id.empty_text_view));
        // get intent, extract the query
        Intent intent = getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            // clean blank spaces from query
            query  = QueryUtils.getQuery(intent.getStringExtra(SearchManager.QUERY));

            // start loader
            LoaderManager loaderManager = getLoaderManager();
            if(internetConnectionAvailable()){
                loaderManager.initLoader(0,null,this);
            }
            else{
                ((TextView)findViewById(R.id.empty_text_view)).setText(getString(R.string.no_internet));
                ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(GONE);
            }
        }

    }
    /**
     * tells if connection to internet is available
     * @return
     */
    public boolean internetConnectionAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public Loader<ArrayList<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // get the section preference
        String section = sharedPreferences.getString(getString(R.string.section_preference_key),getString(R.string.default_section_value));
        Uri baseuri = Uri.parse(QueryUtils.GUARDIAN_QUERY_URL);
        // add query parameters to base url
        Uri.Builder builder = baseuri.buildUpon();
        builder.appendQueryParameter("section",section);
        builder.appendQueryParameter("q",query);
        //new Loader
        return new NewsLoader(this,builder.toString());
    }

    /**
     * update UI when the loader has finished loading
     * @param loader
     * @param newses
     */
    @Override
    public void onLoadFinished(Loader<ArrayList<News>> loader, ArrayList<News> newses) {
        ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(GONE);
        ((TextView)findViewById(R.id.empty_text_view)).setText(getString(R.string.no_news_found));
        if(newses != null && !newses.isEmpty()) {
            newsAdapter.addAll(newses);
        }
    }

    /**
     * clear the adapter data
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<ArrayList<News>> loader) {
        newsAdapter.clear();
    }

}
