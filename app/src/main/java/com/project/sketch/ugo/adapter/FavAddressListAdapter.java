package com.project.sketch.ugo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.database.DatabaseHelper;
import com.project.sketch.ugo.database.FavDataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 2/5/18.
 */

public class FavAddressListAdapter extends RecyclerView.Adapter<FavAddressListAdapter.FavHolder>{

    private List<FavDataModel> mfavDataModels;
    Context context;
    private DatabaseHelper databaseHelper;

    public FavAddressListAdapter(Context context_, List<FavDataModel> favDataModels) {
        this.context = context_;
        mfavDataModels = new ArrayList<>(favDataModels);
        databaseHelper = new DatabaseHelper(context_);

    }

    @Override
    public FavHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fav_adress_list_item, viewGroup, false);
        context = viewGroup.getContext();
        return new FavHolder(view);
    }

    @Override
    public void onBindViewHolder(final FavHolder holder, final int i) {

        final FavDataModel favDataModel = mfavDataModels.get(i);

        holder.tv_fav_title.setText(favDataModel.getTITLE());
        holder.tv_address.setText(favDataModel.getADDRESS());

        if (favDataModel.getTYPE().matches("home")){

            holder.iv_icon.setImageResource(R.mipmap.icon_home);

        }else if (favDataModel.getTYPE().matches("work")){

            holder.iv_icon.setImageResource(R.mipmap.icon_office);

        }else {

            holder.iv_icon.setImageResource(R.mipmap.green_marker);

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mViewClickListener.onImageClicked(i);

            }
        });


        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDeleteDialog(favDataModel.getID(), i);

            }
        });




    }

    @Override
    public int getItemCount() {
        return mfavDataModels.size();
    }

    /** Filter Logic**/
    public void animateTo(List<FavDataModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);

    }

    private void applyAndAnimateRemovals(List<FavDataModel> newModels) {
        for (int i = mfavDataModels.size() - 1; i >= 0; i--) {
            final FavDataModel model = mfavDataModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<FavDataModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final FavDataModel model = newModels.get(i);
            if (!mfavDataModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<FavDataModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final FavDataModel model = newModels.get(toPosition);
            final int fromPosition = mfavDataModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public FavDataModel removeItem(int position) {
        final FavDataModel model = mfavDataModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, FavDataModel model) {
        mfavDataModels.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final FavDataModel model = mfavDataModels.remove(fromPosition);
        mfavDataModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    class FavHolder extends RecyclerView.ViewHolder{
        private TextView tv_fav_title;
        private TextView tv_address;
        private ImageView iv_icon;
        private ImageView iv_delete;


        public FavHolder(View itemView) {
            super(itemView);
            tv_fav_title = (TextView) itemView.findViewById(R.id.tv_fav_title);
            tv_address= (TextView) itemView.findViewById(R.id.tv_address);
            iv_icon= (ImageView) itemView.findViewById(R.id.iv_icon);
            iv_delete= (ImageView) itemView.findViewById(R.id.iv_delete);

        }
    }


    private void showDeleteDialog(final String id, final int position){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle("Are you sure you want to delete?");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                databaseHelper.deleteDataUsingId(id);

                mfavDataModels.remove(position);

                notifyDataSetChanged();

                dialog.dismiss();
            }
        });
        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();


    }




    private ViewClickListener mViewClickListener;
    public interface ViewClickListener {
        void onImageClicked(int position);
    }

    public void setViewClickListener (ViewClickListener viewClickListener) {
        mViewClickListener = viewClickListener;
    }


}
