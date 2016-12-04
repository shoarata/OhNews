package com.example.android.ohnews;

import android.app.LoaderManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.app.LoaderManager.LoaderCallbacks;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<News>> {

    ListView listView;
    NewsAdapter newsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // setting up list view with adapter
        listView = (ListView) findViewById(R.id.list_view);
        newsAdapter = new NewsAdapter(this,new ArrayList<News>());
        listView.setAdapter(newsAdapter);
        listView.setEmptyView(findViewById(R.id.empty_text_view));
        // listener to open a detailed webpage of the clicked news
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News current = newsAdapter.getItem(position);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(current.getUrl()));
                if(i.resolveActivity(getPackageManager())!=null)
                    startActivity(i);
            }
        });
        // attach listener to floation action button to call de search dialog
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.search_FAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchRequested();
            }
        });

        // loader
        LoaderManager loaderManager = getLoaderManager();
        //if internet connection available start load content if not tell the user
        if(internetConnectionAvailable()){
            loaderManager.initLoader(0,null,this);
        }
        else{
            ((TextView)findViewById(R.id.empty_text_view)).setText(getString(R.string.no_internet));
            ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
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
    /**
     * open the preference settings
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //LOADER

    /**
     * create a new News Loader or use an existing one
     * @param i
     * @param bundle
     * @return
     */
    @Override
    public Loader<ArrayList<News>> onCreateLoader(int i, Bundle bundle) {SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // get the section preference
        String section = sharedPreferences.getString(getString(R.string.section_preference_key),getString(R.string.default_section_value));
        Uri baseuri = Uri.parse(QueryUtils.GUARDIAN_QUERY_URL);
        // add query parameters to base url
        Uri.Builder builder = baseuri.buildUpon();
        builder.appendQueryParameter("section",section);
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
