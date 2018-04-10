package com.ckj.worktime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yr.worktime.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kaijianchen on 2018/4/4.
 */

public class TimeInfosAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<HashMap<String, String>> mList;

    public TimeInfosAdapter(Context context, ArrayList list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.timeinfo_item, null);

            holder = new ViewHolder();
            holder.numTV = (TextView) convertView.findViewById(R.id.search_no);
            holder.dateTV = (TextView) convertView.findViewById(R.id.search_date);
            holder.beginTimeTV = (TextView) convertView.findViewById(R.id.search_time1);
            holder.endTimeTV = (TextView) convertView.findViewById(R.id.search_time2);
            holder.workTimeTV = (TextView) convertView.findViewById(R.id.work_time);
            holder.workType = (TextView) convertView.findViewById(R.id.work_type);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String search_no = String.valueOf(mList.get(position).get("search_no"));
        String search_date = String.valueOf(mList.get(position).get("search_date"));
        String search_time1 = String.valueOf(mList.get(position).get("search_time1"));
        String search_time2 = String.valueOf(mList.get(position).get("search_time2"));
        String work_time = String.valueOf(mList.get(position).get("work_time"));
        String work_type = String.valueOf(mList.get(position).get("search_type"));
        holder.numTV.setText(search_no);
        holder.dateTV.setText(search_date);
        holder.beginTimeTV.setText(search_time1);
        holder.endTimeTV.setText(search_time2);
        holder.workTimeTV.setText(work_time);
        holder.workType.setText(getWorkType(Integer.valueOf(work_type)));

        return convertView;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class ViewHolder {
        TextView numTV;
        TextView dateTV;
        TextView beginTimeTV;
        TextView endTimeTV;
        TextView workTimeTV;
        TextView workType;
    }

    private String getWorkType(int work_type){
        String type;
        switch (work_type){
            case 1:
                type = mContext.getResources().getString(R.string.normal);
                break;
            case 2:
                type = mContext.getResources().getString(R.string.weekJob);
                break;
            case 4:
                type = mContext.getResources().getString(R.string.restAllday);
                break;
            default:
                type = mContext.getResources().getString(R.string.normal);
                break;
        }
        return type;
    }

}
