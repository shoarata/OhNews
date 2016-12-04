package com.example.android.ohnews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.R.attr.focusedMonthDateColor;
import static android.R.attr.resource;
import static android.view.View.Z;

/**
 * Created by arata on 29/11/2016.
 */

public class NewsAdapter extends ArrayAdapter<News> {
    public NewsAdapter(Context context, ArrayList<News> news) {
        super(context, 0,news);
    }

    private class ViewHolder{
        TextView title;
        TextView section;
        TextView year;
        TextView date;
    }

    /**
     * gets year from date string
     */
    private String getYear(String date){
        return date.substring(0,4);
    }

    /**
     * gets the day and month from date String
     */
    private  String getDate(String date){
        String day = "";
        String month = "";

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date d =  formatter.parse(date);
            formatter = new SimpleDateFormat("dd");
            day = formatter.format(d);
            formatter = new SimpleDateFormat("MMM");
            month = formatter.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day + " " + month;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.section = (TextView) convertView.findViewById(R.id.item_section);
            viewHolder.year = (TextView) convertView.findViewById(R.id.item_year);
            viewHolder.date = (TextView) convertView.findViewById(R.id.item_date);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        News current = getItem(position);
        viewHolder.title.setText(current.getTitle());
        viewHolder.section.setText(current.getSection());
        viewHolder.year.setText(getYear(current.getDate()));
        viewHolder.date.setText(getDate(current.getDate()));

        return convertView;
    }
}
