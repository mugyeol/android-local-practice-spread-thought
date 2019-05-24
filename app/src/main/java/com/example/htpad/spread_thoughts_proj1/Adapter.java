package com.example.htpad.spread_thoughts_proj1;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.htpad.spread_thoughts_proj1.model.ItemList;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private Context mcontext;
    static int RQ_TO_READ=333;

    private static final String TAG = "Adapter";

    public Adapter(Context context, ArrayList<ItemList> itemLists) {
        this.mcontext = context;
        this.itemLists = itemLists;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.my_timeline_items,parent,false);
        return new ViewHolder(view);
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tittle,contents,mark_time;
        ImageView imgView;
        View view;
        public ViewHolder(View itemView) {
            super(itemView);
            tittle = itemView.findViewById(R.id.tittle_mytimeline);
            contents = itemView.findViewById(R.id.contents_mytimeline);
            imgView = itemView.findViewById(R.id.img_mytimeline);
            view = itemView.findViewById(R.id.timelinelinear);
            mark_time = itemView.findViewById(R.id.written_date_mypage);

        }
    }

    ArrayList<ItemList> itemLists;

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final ItemList itemControl= itemLists.get(position);
        holder.tittle.setText(itemControl.getTittle());
        holder.contents.setText(itemControl.getContents());

        String yyyy = itemControl.getTime().substring(0,4);
        String MM = itemControl.getTime().substring(5,7);
        String dd = itemControl.getTime().substring(8,10);
        String time_nicely = Factory.getmonth(MM)+" "+dd+"."+yyyy;

        holder.mark_time.setText(time_nicely);

        if (itemControl.getImgString()!=null) {
//            ExifInterface exif = null;
//            try {
//                exif = new ExifInterface(itemControl.getImgString());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            int exifDegree = Factory.exifOrientationToDegrees(exifOrientation);
//
//            Bitmap bitmap = BitmapFactory.decodeFile(itemControl.getImgString());
//            int limit_height = (int)(bitmap.getHeight()*(1024.0/bitmap.getWidth()));
//            Bitmap scaled = Bitmap.createScaledBitmap(bitmap,1024,limit_height,true);
//            holder.imgView.setImageBitmap(Factory.rotate(scaled, exifDegree));
            Glide.with(mcontext).load(itemControl.getImgString()).into(holder.imgView);

        }else if (itemControl.getImgString()==null){
            Bitmap bitmap = BitmapFactory.decodeFile(itemControl.getImgString());
            holder.imgView.setImageBitmap(bitmap);
//            Toast.makeText(mcontext, "사진 x", Toast.LENGTH_SHORT).show(); /// null인데도 사진 셋 되서 위에 2라인 추가.
        }


        SharedPreferences pref_user= mcontext.getSharedPreferences("USER_INFO",MODE_PRIVATE);

        SharedPreferences prefs = mcontext.getSharedPreferences("My_pref"+pref_user.getInt("key_user",10), MODE_PRIVATE); //+index로 user 구분.);
        SharedPreferences.Editor editor = prefs.edit();

        //shared에 저장!! 여기가 저장소임. (timeline이나 mypage oncreate에서 여기서 저장된 데이터 호출 & add).
        //add 와 수정 시에 추가/변경된 데이터 저장.
        editor.putString("arrayPREF_tittle"+(position+1),itemControl.getTittle());
        editor.putString("arrayPREF_imgstr"+(position+1),itemControl.getImgString());
        editor.putString("arrayPREF_contents"+(position+1),itemControl.getContents()); //array size와 맞추기 위해서.
        editor.putString("arrayPREF_author"+(position+1),itemControl.getAuther());      //실제 포지션(0부터 시작하는 && 수정 삭제 시에 적용하는)과 pref에 position은 1 차이있음. 전자가 1 적음.
        editor.putString("TIME"+(position+1),itemControl.getTime());                    //어레이 0번 아이템 데이터 삭제 위해서는 pref key +1삭제 해야됨.
        editor.putInt("arrayPREF_size",itemLists.size()); //
        editor.apply();







        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext,ReadPart.class);
                intent.putExtra("key_tittle",itemControl.getTittle());
                intent.putExtra("key_position",position);
                intent.putExtra("key_imgStr",itemControl.getImgString());
                intent.putExtra("key_contents",itemControl.getContents());
                intent.putExtra("key_author",itemControl.getAuther());
                ((Activity)mcontext).startActivityForResult(intent,RQ_TO_READ);
            }
        });
    }

    @Override
    public int getItemCount() {

        return itemLists.size();
    }




}


