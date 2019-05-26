package com.vtg.myvtgph.myvtg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TouristSpotListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TouristSpotList> lists=new ArrayList<TouristSpotList>();
    private LayoutInflater inflater;

    public TouristSpotListAdapter(Context context, ArrayList<TouristSpotList> lists) {
        this.context = context;
        this.lists = lists;
        this.inflater = LayoutInflater.from(context);
    }



    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.touristspot,null);
            vh =new ViewHolder();
            vh.placeImg = convertView.findViewById(R.id.placeImg);
            vh.txtName = convertView.findViewById(R.id.touristname);
            convertView.setTag(vh);
        }else vh = (ViewHolder) convertView.getTag();
        vh.txtName.setText(lists.get(position).getPlaceName());

        Picasso.get()
                .load(lists.get(position).getPlaceImgUrl())
                .placeholder(R.drawable.loads)
                .into(vh.placeImg);
//        vh.placeImg.setTag(lists.get(position));

        return convertView;

    }
    static class ViewHolder{
        TextView txtName;
        CircleImageView placeImg;
    }
}
