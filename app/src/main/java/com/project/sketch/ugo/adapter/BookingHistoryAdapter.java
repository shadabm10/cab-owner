package com.project.sketch.ugo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.apiModel3.BookHisSingle;
import com.project.sketch.ugo.screen.BookingHistoryDetails;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by ANDROID on 1/10/2018.
 */

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.HistoryHolder>  {

    private List<BookHisSingle> bookHisSingleList;
    Context context;

    public BookingHistoryAdapter(Context context_, List<BookHisSingle> countryModel) {
        this.context = context_;
        bookHisSingleList = new ArrayList<>(countryModel);

    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_booking_history, viewGroup, false);
        context = viewGroup.getContext();
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(final HistoryHolder holder, final int i) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        BookHisSingle bookHisSingle = bookHisSingleList.get(i);

        holder.tv_id_.setText(bookHisSingle.getInvoice_number());

        String date = dateConversion(bookHisSingle.getCreated_date());
        holder.tv_date.setText(date);

        holder.tv_pickup_address.setText(bookHisSingle.getPick_address());

        setStatus(bookHisSingle.getBooking_status(), holder.tv_status);


        Picasso.with(context).load(bookHisSingle.getDriver_image()).error(R.mipmap.avatar).into(holder.icon_, new Callback() {
            @Override
            public void onSuccess() {
                //  Log.d("TAG", "onSuccess");
            }
            @Override
            public void onError() {
                //  Toast.makeText(mactivity, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                moveToHistory(bookHisSingleList.get(i));
            }
        });





    }

    @Override
    public int getItemCount() {
        return bookHisSingleList.size();
    }

    /** Filter Logic**/
    public void animateTo(List<BookHisSingle> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);

    }

    private void applyAndAnimateRemovals(List<BookHisSingle> newModels) {
        for (int i = bookHisSingleList.size() - 1; i >= 0; i--) {
            final BookHisSingle model = bookHisSingleList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<BookHisSingle> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final BookHisSingle model = newModels.get(i);
            if (!bookHisSingleList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<BookHisSingle> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final BookHisSingle model = newModels.get(toPosition);
            final int fromPosition = bookHisSingleList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public BookHisSingle removeItem(int position) {
        final BookHisSingle model = bookHisSingleList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, BookHisSingle model) {
        bookHisSingleList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final BookHisSingle model = bookHisSingleList.remove(fromPosition);
        bookHisSingleList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    class HistoryHolder extends RecyclerView.ViewHolder{
        private TextView tv_id_;
        private TextView tv_pickup_address;
        private TextView tv_status;
        private CircleImageView icon_ ;
        private TextView tv_date;


        public HistoryHolder(View itemView) {
            super(itemView);
            tv_id_ = (TextView) itemView.findViewById(R.id.id_);
            tv_pickup_address= (TextView) itemView.findViewById(R.id.tv_pickup);
            tv_status= (TextView) itemView.findViewById(R.id.tv_status);
            icon_ = (CircleImageView) itemView.findViewById(R.id.imageView);
            tv_date= (TextView) itemView.findViewById(R.id.tv_date);


        }
    }

    public String dateConversion(String date){
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date newDate = null;
        try {
            newDate = spf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("EEE, MMM dd, yyyy hh:mm aa");
        date = spf.format(newDate);
        return date;
    }

    private void moveToHistory(BookHisSingle bookHisSingle_){

        Intent intent = new Intent(context, BookingHistoryDetails.class);
        intent.putExtra("data", bookHisSingle_);
        context.startActivity(intent);

    }


    public void setStatus(String status, TextView textView){

        switch (status){
            case "completed" :
                textView.setText("Completed");
                textView.setTextColor(context.getResources().getColor(R.color.status_green));

                break;
            case "cancelled" :
                textView.setText("Cancelled");
                textView.setTextColor(context.getResources().getColor(R.color.status_red));

                break;

            default:
                textView.setText("");
                break;


        }

    }

}

