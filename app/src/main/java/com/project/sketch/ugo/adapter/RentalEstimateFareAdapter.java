package com.project.sketch.ugo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.apiModel5.RentalRate;
import com.project.sketch.ugo.utils.GlobalClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developer on 15/3/18.
 */

public class RentalEstimateFareAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<RentalRate> rateArrayList;
    private ArrayList<Boolean> booleanArrayList;

    private ViewClickListener mViewClickListener;
    private GlobalClass globalClass;


    public RentalEstimateFareAdapter(Context context_, List<RentalRate> rentalRates) {
        super();
        this.context = context_;
        layoutInflater = LayoutInflater.from(context_);
        this.rateArrayList = rentalRates;
        booleanArrayList = new ArrayList<>();
        globalClass = (GlobalClass) context_.getApplicationContext();

        setBooleanData();

    }

    public void setBooleanData(){
        for (int i = 0; i < rateArrayList.size(); i++){
            booleanArrayList.add(false);
        }
    }


    @Override
    public int getCount() {
        return rateArrayList.size();
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

        convertView = layoutInflater.inflate(R.layout.rental_fare_list_item, null);
        holder = new ViewHolder();
        holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
        holder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
        holder.tv_guide_charge = (TextView) convertView.findViewById(R.id.tv_guide_charge);
        holder.tv_estimate_fare = (TextView) convertView.findViewById(R.id.tv_estimate_fare);
        holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
        holder.rl_main = (RelativeLayout) convertView.findViewById(R.id.rl_main);



        holder.tv_time.setText((int)rateArrayList.get(position).getTrip_duration()
                + " " + rateArrayList.get(position).getDuration_unit());

        holder.tv_distance.setText((int)rateArrayList.get(position).getTrip_distance()
                + " " + rateArrayList.get(position).getDistance_unit());

        holder.tv_guide_charge.setText("*Guider charge : ₹"
                + (int)rateArrayList.get(position).getGuide_charge()+"(Excluded)");


        if (globalClass.getCoupon_applied().matches("Y")){
            float amt = rateArrayList.get(position).getEstimated_fare()
                    - globalClass.getCoupon_amount();
            holder.tv_estimate_fare.setText("₹ "+(int)amt);
        }else {
            holder.tv_estimate_fare.setText("₹ "+(int)rateArrayList.get(position).getEstimated_fare());
        }


        holder.checkbox.setChecked(booleanArrayList.get(position));
        holder.checkbox.setClickable(false);

        holder.rl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mViewClickListener.onItemClicked(position);

                setSelectedData(position, true);

            }
        });


        return convertView;
    }


    private class ViewHolder {
        TextView tv_time;
        TextView tv_distance;
        TextView tv_guide_charge;
        TextView tv_estimate_fare;
       // RadioButton radio_select;
        CheckBox checkbox;
        RelativeLayout rl_main;

    }


    public void setSelectedData(int position, boolean boo){
        booleanArrayList.clear();
        setBooleanData();
        booleanArrayList.set(position, boo);
        notifyDataSetChanged();
    }



    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


    public interface ViewClickListener {
        void onItemClicked(int position);
    }


    public void setViewClickListener (ViewClickListener viewClickListener) {
        mViewClickListener = viewClickListener;
    }
}
