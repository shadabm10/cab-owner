package com.project.sketch.ugo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.datamodel.CabVariant;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.GlobalClass;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Developer on 1/19/18.
 */

public class RecyclerCabAdapter extends RecyclerView.Adapter<RecyclerCabAdapter.MyViewHolder> {

    private ArrayList<CabVariant> cabVariantArrayList;
    private Context context;
    int selectedPosition = 0;
    GlobalClass globalClass;

    private ViewClickListener mViewClickListener;

    TextView tv_reaching_time;

    public RecyclerCabAdapter(Context context_, ArrayList<CabVariant> data) {
        this.context = context_;
        this.cabVariantArrayList = data;

        globalClass = (GlobalClass)context_.getApplicationContext();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_car_name, tv_reaching_time;
        ImageView image_car_icon;
        RelativeLayout rl_background;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tv_car_name = (TextView) itemView.findViewById(R.id.tv_car_name);
            this.tv_reaching_time = (TextView) itemView.findViewById(R.id.tv_reaching_time);
            //this.textViewVersion = (TextView) itemView.findViewById(R.id.textViewVersion);
            this.image_car_icon = (ImageView) itemView.findViewById(R.id.image_car_icon);
            this.rl_background = (RelativeLayout) itemView.findViewById(R.id.rl_background);


        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.circle_list_item, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);



        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView textViewName = holder.tv_car_name;
        tv_reaching_time = holder.tv_reaching_time;
        RelativeLayout rl_background = holder.rl_background;
        // TextView textViewVersion = holder.textViewVersion;
        ImageView image_car_icon = holder.image_car_icon;

        textViewName.setText(cabVariantArrayList.get(listPosition).getCab_name());

       // Log.d(Constants.TAG, "data = "
              //  +cabVariantArrayList.get(listPosition).getCab_reaching_time());
       // Log.d(Constants.TAG, "position = "+listPosition);


        if (cabVariantArrayList.get(listPosition).getNoOfcab() == 0){

            tv_reaching_time.setText("No cab found");

        }else {

            tv_reaching_time.setText(
                    cabVariantArrayList.get(listPosition).getCab_reaching_time()
                    +" min away");

        }


        if (selectedPosition == listPosition){

            rl_background.setBackgroundResource(R.drawable.circle_green);
           // image_car_icon.setImageResource(R.mipmap.car_white);

            Picasso.with(context).load(cabVariantArrayList.get(listPosition).getSelectImage()).error(R.mipmap.avatar).into( image_car_icon, new Callback() {
                @Override
                public void onSuccess() {
                    //  Log.d("TAG", "onSuccess");
                }
                @Override
                public void onError() {
                    //  Toast.makeText(mactivity, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });


            globalClass.Selected_Cab_Id = cabVariantArrayList.get(listPosition).getCab_id();
            globalClass.Selected_Cab_Name = cabVariantArrayList.get(listPosition).getCab_name();
            globalClass.Selected_Cab_Img = cabVariantArrayList.get(listPosition).getSelectImage();

        }else {

            rl_background.setBackgroundResource(R.drawable.circle_white);
          //  image_car_icon.setImageResource(R.mipmap.car_green);

            Picasso.with(context).load(cabVariantArrayList.get(listPosition).getUnselectImage()).error(R.mipmap.avatar).into( image_car_icon, new Callback() {
                @Override
                public void onSuccess() {
                    //  Log.d("TAG", "onSuccess");
                }
                @Override
                public void onError() {
                    //  Toast.makeText(mactivity, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mViewClickListener.onImageClicked(listPosition);

                selectedPosition = listPosition;
                notifyDataSetChanged();

                globalClass.Selected_Cab_Id = cabVariantArrayList.get(listPosition).getCab_id();
                globalClass.Selected_Cab_Name = cabVariantArrayList.get(listPosition).getCab_name();

                Log.d(Constants.TAG, " On CLick = "+listPosition);
            }
        });


    }

    @Override
    public int getItemCount() {
        return cabVariantArrayList.size();
    }


    public void notifyData(){
        notifyDataSetChanged();
    }


    public interface ViewClickListener {
        void onImageClicked(int position);
    }


    public void setViewClickListener (ViewClickListener viewClickListener) {
        mViewClickListener = viewClickListener;
    }







}
