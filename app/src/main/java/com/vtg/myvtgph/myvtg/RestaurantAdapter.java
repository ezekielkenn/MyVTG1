package com.vtg.myvtgph.myvtg;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RestaurantAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
    private LayoutInflater inflater;

    public RestaurantAdapter(Context context, ArrayList<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return restaurants.size();
    }

    @Override
    public Object getItem(int position) {
        return restaurants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView==null){
            vh = new ViewHolder();
            convertView = inflater.inflate(R.layout.restaurant,null);
            vh.address = convertView.findViewById(R.id.restAddress);
            vh.name = convertView.findViewById(R.id.restName);
            vh.openclose = convertView.findViewById(R.id.restOpen);
            vh.img = convertView.findViewById(R.id.restImg);
            vh.rating = convertView.findViewById(R.id.restRating);
            convertView.setTag(vh);
        }else vh = (ViewHolder) convertView.getTag();
        if(restaurants.get(position).getRating()!=-1.0f) {
            vh.rating.setVisibility( View.VISIBLE );
            vh.rating.setRating( restaurants.get( position ).getRating() );
        }else{
            vh.rating.setRating( 0.0f );
            vh.rating.setVisibility( View.GONE );
        }
        vh.name.setText(restaurants.get(position).getRestaurantName());
        vh.address.setText(restaurants.get(position).getRestaurantAddress());
        if(!restaurants.get( position ).isOpen().equals( "null" )) {
            vh.openclose.setVisibility( View.VISIBLE );
            if (restaurants.get( position ).isOpen().equals( "true" )) {
                vh.openclose.setText( "OPENED" );
                vh.openclose.setTextColor( Color.GREEN );
            } else {
                vh.openclose.setText( "CLOSED" );
                vh.openclose.setTextColor( Color.RED );
            }
        }else{
            vh.openclose.setVisibility( View.GONE );
        }
        Picasso.get()
                .load(restaurants.get(position).getRestaurantIcon())
                .placeholder(R.drawable.picplaceholder)
                .error(R.drawable.loads)
                .into(vh.img);

        return convertView;
    }
    static class ViewHolder{
        TextView name,address,openclose;
        ImageView img;
        RatingBar rating;
    }
}
