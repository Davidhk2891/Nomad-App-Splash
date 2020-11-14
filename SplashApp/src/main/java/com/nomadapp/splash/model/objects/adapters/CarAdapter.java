package com.nomadapp.splash.model.objects.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.objects.MyCar;

import java.util.ArrayList;

/**
 * Created by David on 6/24/2019 for Splash.
 */
public class CarAdapter extends ArrayAdapter<MyCar> {

    private Context context;
    private int layoutResource;
    private ArrayList<MyCar> mData;

    //Standard Constructor
    public CarAdapter(Context ctx, int resource, ArrayList<MyCar> data){
        super(ctx, resource, data);

        this.context = ctx;
        layoutResource = resource;
        mData = data;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Nullable
    @Override
    public MyCar getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getPosition(@Nullable MyCar item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView,
                        @NonNull ViewGroup parent) {

        View row = convertView;
        ViewHolder holder;

        if (row == null || (row.getTag()) == null){

            LayoutInflater theInflator = LayoutInflater.from(context);
            row = theInflator.inflate(layoutResource, null);
            holder = new ViewHolder();

            holder.mBrand = row.findViewById(R.id.carBrandRow);
            holder.mModel = row.findViewById(R.id.carModelRow);
            holder.mColor = row.findViewById(R.id.carColorRow);
            holder.mPlate = row.findViewById(R.id.carPlateRow);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.holderCar = getItem(position);

        if (holder.holderCar != null) {
            holder.mBrand.setText(holder.holderCar.getBrand());
            holder.mModel.setText(holder.holderCar.getModel());
            holder.mColor.setText(holder.holderCar.getColorz());
            holder.mPlate.setText(holder.holderCar.getPlateNum());

            Log.i("orange98", "is " + holder.holderCar.getBrand()
                    + " " + holder.holderCar.getModel());
        }
        return row;
    }

    public static class ViewHolder{
        MyCar holderCar;
        TextView mBrand;
        TextView mModel;
        TextView mColor;
        TextView mPlate;
    }
}