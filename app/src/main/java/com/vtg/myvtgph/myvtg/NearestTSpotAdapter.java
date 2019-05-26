package com.vtg.myvtgph.myvtg;

import android.content.Context;
import android.support.v4.view.AsyncLayoutInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NearestTSpotAdapter extends BaseAdapter {
    Context context;

    public NearestTSpotAdapter(Context context, ArrayList<NearestTSpot> nearestTSpotArrayList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.nearestTSpotArrayList = nearestTSpotArrayList;
    }

    LayoutInflater inflater;
    ArrayList<NearestTSpot> nearestTSpotArrayList = new ArrayList<NearestTSpot>();
    @Override
    public int getCount() {
        return nearestTSpotArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return nearestTSpotArrayList.get(position);
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
            convertView = inflater.inflate(R.layout.nearest_tspot_item,null);
            vh.placeImg = convertView.findViewById(R.id.imgPlaceImg);
            vh.placeRating = convertView.findViewById(R.id.placeRatingview);
            vh.txtPlacehist = convertView.findViewById(R.id.txtPlaceHist);
            vh.txtPlacename = convertView.findViewById(R.id.txtPlacename);
            vh.txtDistance = convertView.findViewById(R.id.txtDistance);
            convertView.setTag(vh);
        }else vh = (ViewHolder) convertView.getTag();
        vh.txtPlacename.setText(nearestTSpotArrayList.get(position).getPlace_name());
        vh.txtPlacehist.setText(nearestTSpotArrayList.get(position).getPlace_history());
        vh.placeRating.setRating(nearestTSpotArrayList.get(position).getRating());
        vh.txtDistance.setText(String.format("Distance : %.2f",nearestTSpotArrayList.get(position).getDistance()));

        Picasso.get()
                .load(nearestTSpotArrayList.get(position).getPlace_img())
                .placeholder(R.drawable.picplaceholder)
                .error(R.drawable.picplaceholder)
                .into(vh.placeImg);
        return convertView;
    }
    static class ViewHolder{
        TextView txtPlacename,txtPlacehist,txtDistance;
        RatingBar placeRating;
        CircleImageView placeImg;
    }
}
