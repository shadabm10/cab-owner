package com.project.sketch.ugo.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.project.sketch.ugo.R;

import java.util.ArrayList;

/**
 * Created by ANDRIOD on 6/16/2016.
 */
public class SlidingImage_Adapter extends PagerAdapter {



    private LayoutInflater inflater;
    private Context context;
    ArrayList<Integer> Banner = new ArrayList<>();




  //  ArrayList<Integer> banner_image = new ArrayList<>();

    public SlidingImage_Adapter(Context _context, ArrayList<Integer> Banner_image) {
        this.context = _context;
        this.Banner = Banner_image;
        inflater = LayoutInflater.from(context);


    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public int getCount() {
        return Banner.size();
    }


    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.vp_image, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image_display);

        imageView.setImageResource(Banner.get(position));


       // String image = Banner.get(position);


        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
