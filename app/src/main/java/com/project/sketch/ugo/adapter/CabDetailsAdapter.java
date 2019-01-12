package com.project.sketch.ugo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.apiModel7.CabData;

import java.util.List;

/**
 * Created by developer on 28/3/18.
 */

public class CabDetailsAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<CabData> cabDataList;


    public CabDetailsAdapter(Context context_,
                                        List<CabData> cabDataList_) {
        super();
        this.context = context_;
        layoutInflater = LayoutInflater.from(context_);
        this.cabDataList = cabDataList_;

    }


    @Override
    public int getCount() {
        return cabDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        convertView = layoutInflater.inflate(R.layout.frag_cab_details_listitem, null);
        holder = new ViewHolder();
        holder.tv_cab_name = (TextView) convertView.findViewById(R.id.tv_cab_name);
        holder.tv_car_name = (TextView) convertView.findViewById(R.id.tv_car_name);


        holder.tv_cab_name.setText(cabDataList.get(position).getName());

        holder.tv_car_name.setText(getConcatString(cabDataList.get(position).getCar()));


        return convertView;
    }


    private class ViewHolder {
        TextView tv_cab_name;
        TextView tv_car_name;

    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }



    public String getConcatString(List<String> list){

        StringBuilder commaSepValueBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++){
            commaSepValueBuilder.append(list.get(i));

            if ( i != list.size()-1){
                commaSepValueBuilder.append(", ");
            }
        }

        return commaSepValueBuilder.toString();
    }





}
