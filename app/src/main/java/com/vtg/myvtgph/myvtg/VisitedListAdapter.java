package com.vtg.myvtgph.myvtg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisitedListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Visits> visits=new ArrayList<Visits>();
    private LayoutInflater inflater;

    public VisitedListAdapter(Context context, ArrayList<Visits> visits) {
        this.context = context;
        this.visits = visits;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return visits.size();
    }

    @Override
    public Object getItem(int position) {
        return visits.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.visit_item,null);
            vh =new ViewHolder();
            vh.myRate = convertView.findViewById(R.id.placeUserRate);
            vh.placeImg = convertView.findViewById(R.id.placeImg);
            vh.txtDate = convertView.findViewById(R.id.placeVisitedDate);
            vh.txtHistory = convertView.findViewById(R.id.placeHistory);
            vh.txtName = convertView.findViewById(R.id.placeName);
            convertView.setTag(vh);
        }else vh = (ViewHolder) convertView.getTag();

        vh.txtName.setText(visits.get(position).getPlaceName());
        vh.txtHistory.setText(visits.get(position).getPlaceHistory());
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String date = DATE_FORMAT.format(visits.get(position).getDateVisited().toDate());
        vh.txtDate.setText(date);
        Picasso.get()
                .load(visits.get(position).getPlaceImgUrl())
                .placeholder(R.drawable.loads)
                .error(R.drawable.uploadpic)
                .into(vh.placeImg);
        vh.placeImg.setTag(visits.get(position));
        vh.myRate.setRating(visits.get(position).getMyRate());

        return convertView;
    }
    static class ViewHolder{
        TextView txtHistory,txtName,txtDate;
        RatingBar myRate;
        CircleImageView placeImg;
    }
}
