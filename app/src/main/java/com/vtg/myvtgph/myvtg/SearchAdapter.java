package com.vtg.myvtgph.myvtg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<TSpot> Tspots=new ArrayList<TSpot>();

    @Override
    public int getCount() {
        return Tspots.size();
    }

    public SearchAdapter(Context context,ArrayList<TSpot> tspots) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.Tspots = tspots;
    }

    @Override

    public Object getItem(int position) {
        return Tspots.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.search_item,null);
            vh = new ViewHolder();
            vh.place_img = convertView.findViewById(R.id.place_img);
            vh.place_name = convertView.findViewById(R.id.place_name);
            vh.place_desc = convertView.findViewById(R.id.place_desc);
            convertView.setTag(vh);
        }else vh = (ViewHolder) convertView.getTag();
        vh.place_name.setText(Tspots.get(position).getPlace_name());
        vh.place_desc.setText(Tspots.get(position).getPlace_desc());
        return convertView;
    }
    static class ViewHolder{
        TextView place_name,place_desc;
        ImageView place_img;
    }
}
