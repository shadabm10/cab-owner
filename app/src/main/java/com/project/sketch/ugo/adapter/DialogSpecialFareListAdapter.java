package com.project.sketch.ugo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.apiModel7.SpecialFareData;

import java.util.List;

/**
 * Created by developer on 15/3/18.
 */

public class DialogSpecialFareListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<SpecialFareData> specialFareData;


    public DialogSpecialFareListAdapter(Context context_,
                                        List<SpecialFareData> specialFareData_) {
        super();
        this.context = context_;
        layoutInflater = LayoutInflater.from(context_);
        this.specialFareData = specialFareData_;



    }


    @Override
    public int getCount() {
        return specialFareData.size();
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
        convertView = layoutInflater.inflate(R.layout.dialog_specialfare_list_item, null);
        holder = new ViewHolder();
        holder.tv_from = (TextView) convertView.findViewById(R.id.tv_from);
        holder.tv_to = (TextView) convertView.findViewById(R.id.tv_to);
        holder.tv_cab_name = (TextView) convertView.findViewById(R.id.tv_cab_name);
        holder.tv_base_fare = (TextView) convertView.findViewById(R.id.tv_base_fare);


        holder.tv_from.setText(specialFareData.get(position).getLocation_name_from());
        holder.tv_to.setText(specialFareData.get(position).getLocation_name_to());
        holder.tv_cab_name.setText(specialFareData.get(position).getCab_name());
        holder.tv_base_fare.setText(specialFareData.get(position).getTotal_fare());



        return convertView;
    }


    private class ViewHolder {
        TextView tv_from;
        TextView tv_to;
        TextView tv_cab_name;
        TextView tv_base_fare;

    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }



}
