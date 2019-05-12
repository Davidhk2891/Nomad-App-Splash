package com.nomadapp.splash.model.objects.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.imagehandler.GlideImagePlacement;
import com.nomadapp.splash.model.objects.MySplasher;
import com.nomadapp.splash.model.objects.users.SplasherSelector;
import com.nomadapp.splash.ui.activity.carownerside.WashReqParamsActivity;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;

import java.util.ArrayList;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by David on 6/5/2018 for Splash.
 */
public class SplasherListAdapter extends ArrayAdapter<MySplasher> {

    private Context context;
    private int layoutResource;
    private MySplasher splasher;
    private ArrayList<MySplasher> mData;

    private ToastMessages toastMessages = new ToastMessages();
    private SplasherSelector splasherSelector;

    //Constructor
    public SplasherListAdapter(Context ctx, int resource, ArrayList<MySplasher> data) {
        super(ctx, resource, data);

        this.context = ctx;
        layoutResource = resource;
        mData = data;
        splasherSelector = new SplasherSelector(ctx);

        notifyDataSetChanged();
    }

    public MySplasher getSplasher() {
        return splasher;
    }

    public void setSplasher(MySplasher splasher) {
        this.splasher = splasher;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Nullable
    @Override
    public MySplasher getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getPosition(@Nullable MySplasher item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View row = convertView;
        final ViewHolder holder;
        GlideImagePlacement glideImagePlacement = new GlideImagePlacement(context);

        if ((row == null) || (row.getTag() == null)){

            LayoutInflater theInflator = LayoutInflater.from(context);
            row = theInflator.inflate(layoutResource, null);
            holder = new ViewHolder();
            holder.mSplasherCardView = row.findViewById(R.id.splasherCardView);
            holder.mSplasherName = row.findViewById(R.id.rowSName);
            holder.mSplasherPrice = row.findViewById(R.id.rowSPrice);
            holder.mSplasherNumWashes = row.findViewById(R.id.rowSNumWashes);
            holder.mSelectSplasher = row.findViewById(R.id.selectSplasher);
            holder.mSplasherRatingBar = row.findViewById(R.id.rowSRatingBar);
            holder.mSplasherRatingBar.setEnabled(false);
            holder.mSplasherRatingBar.setClickable(false);
            holder.mProfPicUri = row.findViewById(R.id.splasherRowThumbNail);
            row.setTag(holder);

        }else{
            holder = (ViewHolder) row.getTag();
        }

        holder.holderSplasher = getItem(position);

        if (holder.holderSplasher != null){

            if (!WashReqParamsActivity.individuallyChecked){
                holder.mSplasherCardView.setCardBackgroundColor(holder.holderSplasher
                        .getSplasherCardColor());
                holder.mSplasherName.setTextColor(holder.holderSplasher.getSplasherTxtColor());
                holder.mSplasherNumWashes.setTextColor(holder.holderSplasher.getSplasherTxtColor());
            }

            holder.mSplasherName.setText(holder.holderSplasher.getSplasherUsername());
            holder.mSplasherPrice.setText(holder.holderSplasher.getCarOwnerPrice());
            holder.mSplasherNumWashes.setText(holder.holderSplasher.getSplasherNumOfWashes());
            int fixedProgress = (holder.holderSplasher.getSplasherAvgRating()) * 2;
            holder.mSplasherRatingBar.setProgress(fixedProgress);
            glideImagePlacement.roundImagePlacementFromString(holder.holderSplasher
                        .getSplasherProfPic(),holder.mProfPicUri);
        }else{
            toastMessages.productionMessage(context.getApplicationContext()
                    ,"Some elements from the splasher list could not be retrieved." +
                            " Check your internet connection and try again",1);
        }

        //select splasher button
        holder.mSelectSplasher.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!WashReqParamsActivity.allListSelected) {
                    Log.i("green1", "ran");
                    Log.i("green2", holder.holderSplasher.getSplasherUsername());
                    splasherSelector.selectSplashers(holder.mSplasherCardView, holder.mSplasherName
                            , holder.mSplasherNumWashes
                            , holder.holderSplasher.getSplasherActualName()
                            , holder.holderSplasher.getSplasherPrice()
                            , holder.holderSplasher.getCarOwnerPrice());
                    splasherSelector.splasherListCheckerToOrder
                            (((WashReqParamsActivity) context).cFinallyOrder);
                }
            }
        });
        return row;
    }

    public static class ViewHolder{
        MySplasher holderSplasher;
        CardView mSplasherCardView;
        TextView mSplasherName;
        TextView mSplasherPrice;
        TextView mSplasherNumWashes;
        MaterialRatingBar mSplasherRatingBar;
        ImageView mProfPicUri;
        Button mSelectSplasher;
        //int mId;
    }
}
