package com.example.htpad.spread_thoughts_proj1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.htpad.spread_thoughts_proj1.model.ItemList;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.util.ArrayList;


public class Adapter_Every extends RecyclerView.Adapter<Adapter_Every.ItemViewHolder>{

    private Context mcontext;
    static int RQ_TO_READ_common=323;
    private Picasso mPicasso;
    private Target mTarget;
    float transparency2 = 0x0D;
    int transparency3 = 0x3D;


    public Adapter_Every(Context context, ArrayList<ItemList> itemLists_every) {
        this.mcontext = context;
        this.itemLists_every = itemLists_every;
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.every_timeline_items,parent,false);
        return new ItemViewHolder(view);
    }
    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView tittle,auther,by;
        ImageView imgView;
        FrameLayout frameLayout;
        public ItemViewHolder(View itemView) {
            super(itemView);
            tittle = itemView.findViewById(R.id.tittle_everytimeline);
            auther = itemView.findViewById(R.id.auther_everytimeline);
            imgView = itemView.findViewById(R.id.img_everytimeline);
            frameLayout = itemView.findViewById(R.id.every_frame);
            by = itemView.findViewById(R.id.timelineevery_by);

        }
    }
    ArrayList<ItemList> itemLists_every;

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(2000);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    });
    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        final ItemList itemControl_every= itemLists_every.get(position);
        final RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.frameLayout.setBackgroundColor(mcontext.getResources().getColor(R.color.transparentblack));
                return false;
            }
        };
            //이미지 백그라운드, 텍스트 컬러 초기화.
        holder.auther.setTextColor(mcontext.getResources().getColor(R.color.text_onIMG));
        holder.tittle.setTextColor(mcontext.getResources().getColor(R.color.text_onIMG));
        holder.by.setTextColor(mcontext.getResources().getColor(R.color.text_onIMG));
//        holder.imgView.setBackgroundColor(mcontext.getResources().getColor(R.color.TEXT_COMEOUT));
        holder.frameLayout.setBackgroundColor(mcontext.getResources().getColor(R.color.transparent));


        holder.tittle.setText(itemControl_every.getTittle());
        holder.auther.setText(itemControl_every.getAuther());

        if (itemControl_every.getImgString()!=null) {
//            ExifInterface exif = null;
//            try {
//                exif = new ExifInterface(itemControl_every.getImgString());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            int exifDegree = Factory.exifOrientationToDegrees(exifOrientation);
//
//            Bitmap bitmap = BitmapFactory.decodeFile(itemControl_every.getImgString());
//            int limit_height = (int)(bitmap.getHeight()*(1024.0/bitmap.getWidth()));
//            Bitmap scaled = Bitmap.createScaledBitmap(bitmap,1024,limit_height,true);

//            holder.imgView.setImageBitmap(Factory.rotate(scaled, exifDegree));
            //            holder.imgView.setAlpha(transparency2);


//            thread.start();
            Glide.with(mcontext).load(itemControl_every.getImgString())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.loading_animation)
                            .error(R.drawable.loading_animation)
                            .fitCenter()
                            .centerCrop())
                    .listener(requestListener).into(holder.imgView);



//            holder.frameLayout.setBackgroundColor(mcontext.getResources().getColor(R.color.transparentblack));


//            holder.frameLayout.setBackgroundColor(mcontext.getResources().getColor(R.color.imageBacground));
//            Drawable drawable = holder.imgView.getDrawable();
//            drawable.setAlpha(Factory.transparency);
            holder.auther.setTextColor(mcontext.getResources().getColor(R.color.TEXT_COMEOUT));
            holder.tittle.setTextColor(mcontext.getResources().getColor(R.color.TEXT_COMEOUT));
            holder.by.setTextColor(mcontext.getResources().getColor(R.color.TEXT_COMEOUT));

//            holder.imgView.setBackgroundColor(mcontext.getResources().getColor(R.color.imageBacground));
        }else if (itemControl_every.getImgString()==null){
            Bitmap bitmap = BitmapFactory.decodeFile(itemControl_every.getImgString());
            holder.imgView.setImageBitmap(bitmap);
//            Toast.makeText(mcontext, "사진 x", Toast.LENGTH_SHORT).show(); /// null인데도 사진 셋 되서 위에 2라인 추가.
        }

    holder.imgView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mcontext, Read_every.class);
            intent.putExtra("key_tittle", itemControl_every.getTittle());
            intent.putExtra("key_post_position", itemControl_every.getTime());
            intent.putExtra("key_imgStr", itemControl_every.getImgString());
            intent.putExtra("key_contents", itemControl_every.getContents());
            intent.putExtra("key_author", itemControl_every.getAuther());

            ((Activity) mcontext).startActivityForResult(intent, RQ_TO_READ_common);
        }
    });
}

    @Override
    public int getItemCount() {
        return itemLists_every.size();
    }


}
