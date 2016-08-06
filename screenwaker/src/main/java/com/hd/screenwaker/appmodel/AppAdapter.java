package com.hd.screenwaker.appmodel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hd.screenwaker.R;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {
    private List<AppInfo> mAppInfos;
    private int mItemResId;
    private LayoutInflater mInflater;

    // for reusable, we pass resource id of item view here
    public AppAdapter(List<AppInfo> appList, int itemResId) {
        mAppInfos = appList;
        mItemResId = itemResId;
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mInflater == null){
            mInflater = LayoutInflater.from(parent.getContext());
        }
        View v = mInflater.inflate(mItemResId, parent, false);
        return new AppViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        AppInfo appInfo = mAppInfos.get(position);
        holder.mTextView.setText(appInfo.getName());
        getBitmap(appInfo.getIcon())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(holder.mImageView::setImageBitmap);
                /*public interface Action1<Bitmap> extends Action {
                    void call(Bitmap bm) {
                        holder.mImageView.setImageBitmap(bm);
                    }
                }*/
    }

    private Observable<Bitmap> getBitmap(String icon) {
        return Observable.create(subscriber -> {
            subscriber.onNext(BitmapFactory.decodeFile(icon));
            subscriber.onCompleted();
        });
    }

    @Override
    public int getItemCount() {
        return mAppInfos!= null ? mAppInfos.size() : 0;
    }

    public void addApps(List<AppInfo> infos) {
        mAppInfos.clear();
        mAppInfos.addAll(infos);
        notifyDataSetChanged();
    }

    public void addApp(int position, AppInfo info) {
        if(position < 0) {
            position = 0;
        }
        mAppInfos.add(position, info);
        notifyItemInserted(position);
    }

    class AppViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ImageView mImageView;

        public AppViewHolder(View itemView) {
            super(itemView);

            // find view
            mTextView = (TextView) itemView.findViewById(R.id.app_name_tv);
            mImageView = (ImageView) itemView.findViewById(R.id.app_icon_iv);
        }
    }
}
