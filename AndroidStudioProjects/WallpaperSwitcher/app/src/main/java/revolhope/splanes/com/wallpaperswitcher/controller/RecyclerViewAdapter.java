package revolhope.splanes.com.wallpaperswitcher.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import java.util.ArrayList;

import revolhope.splanes.com.wallpaperswitcher.R;
import revolhope.splanes.com.wallpaperswitcher.callback.OnClickListener;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.Holder> {

    private OnClickListener callback;
    private ArrayList<Bitmap> dataset;
    private ArrayList<Holder> holders;
    private Context context;
    private boolean isSelecting;

    private ArrayList<Uri> uriList;

    public RecyclerViewAdapter(Context context, OnClickListener callback){
        this.callback = callback;
        this.context = context;
        holders = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {

        if(dataset != null){
            return position != (dataset.size()-1) ? 0 : 1;
        }
        else return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.holder_img, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        if(position < dataset.size()){
            holder.iv.setImageBitmap(dataset.get(position));
            holders.add(holder);
        }
    }

    @Override
    public int getItemCount() {
        if(dataset == null) return 0;
        return dataset.size();
    }

    public void setUris(ArrayList<Uri> uriList){
        this.uriList = uriList;
    }

    public void setDataset(ArrayList<Bitmap> dataset){
        if(dataset != null)
            this.dataset = dataset;
        else
            this.dataset = new ArrayList<>();
    }

    private void startAnim(){

        for(Holder holder : holders){
            if(holder != null){

                RotateAnimation rotate = new RotateAnimation(-1, 1, Animation.RELATIVE_TO_SELF,
                        0.5f,  Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(75);
                rotate.setRepeatCount(Animation.INFINITE);
                rotate.setRepeatMode(Animation.REVERSE);
                holder.iv.startAnimation(rotate);
            }
        }
    }

    public void stopAnim(){
        for(Holder holder : holders){
            if(holder != null){
                holder.iv.clearAnimation();
                holder.iv_checked.setVisibility(View.GONE);
            }
        }
        isSelecting = false;
    }

    public void recycleSelectedItems(){

        for(Holder h : holders){
            if(h != null){
                if(h.isSelected){
                    try {
                        dataset.remove(((BitmapDrawable) h.iv.getDrawable()).getBitmap());
                    }catch (Exception e) { e.printStackTrace(); }
                }
            }
        }
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder{

        boolean isSelected;
        ImageView iv_checked;
        ImageView iv;

        Holder(View view){
            super(view);

            isSelected = false;
            iv = view.findViewById(R.id.imageView);
            iv_checked = view.findViewById(R.id.iv_checked);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(isSelecting){

                        if(isSelected){
                            iv_checked.setVisibility(View.GONE);
                            isSelected = false;
                        }else{
                            iv_checked.setVisibility(View.VISIBLE);
                            isSelected = true;
                        }

                    }else{
                        callback.onSimpleClick(((BitmapDrawable)iv.getDrawable()).getBitmap());
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if(!isSelecting) {
                        iv_checked.setVisibility(View.VISIBLE);
                        isSelected = true;
                        isSelecting = true;
                        startAnim();
                        callback.onLongClick();
                    }
                    return true;
                }
            });
        }
    }
}
