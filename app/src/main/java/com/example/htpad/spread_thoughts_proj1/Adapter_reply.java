package com.example.htpad.spread_thoughts_proj1;

import android.content.Context;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Adapter_reply extends RecyclerView.Adapter<Adapter_reply.ItemViewHolder>{

 Context reply_context;
ArrayList<ItemList> itemLists_reply;


public Adapter_reply(Context context, ArrayList<ItemList> itemLists_reply) {
        this.reply_context = context;
        this.itemLists_reply = itemLists_reply;
        }

    private ItemClick itemClick;
    public interface ItemClick {
        public void onClick(View view,int position);
    }

    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }


@Override
public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(reply_context).inflate(R.layout.replies_item,parent,false);
        return new ItemViewHolder(view);
        }
class ItemViewHolder extends RecyclerView.ViewHolder{
    TextView contents_reply,replier,tv_comment;
    View reply_containing_layout;
    ImageView imgView_profile;
    public ItemViewHolder(View itemView) {
        super(itemView);
        contents_reply = itemView.findViewById(R.id.contents_replies);
        replier = itemView.findViewById(R.id.replier_replies);
        imgView_profile = itemView.findViewById(R.id.profile_replies);
        reply_containing_layout = (View) itemView.findViewById(R.id.reply_container);
        tv_comment = (TextView) itemView.findViewById(R.id.swipe_to_delete);
    }
}

    @Override
    public void onBindViewHolder(Adapter_reply.ItemViewHolder holder, int position) {
        final int Position = position;
        final ItemList itemControl_reply= itemLists_reply.get(position);


        SharedPreferences pref_user = reply_context.getSharedPreferences("USER_INFO", MODE_PRIVATE);

        //밀어서 삭제하기 해당 유저에게만 보이도록 하기.
        if (pref_user.getInt("key_user",0)==itemControl_reply.getUser_code_for_permission()) {
            holder.tv_comment.setVisibility(View.VISIBLE);

        }else{
            holder.tv_comment.setVisibility(View.INVISIBLE);
        }

        holder.contents_reply.setText(itemControl_reply.getReply_contents());
        holder.replier.setText(itemControl_reply.getReplier());


                                                                                                                //endindex 는 exclusive임. 해당 index 포함 x
            if(itemControl_reply.getProfile_imgString()!=null && itemControl_reply.getProfile_imgString().substring(0,4).equals("http")) {
                Picasso.with(reply_context).load(itemControl_reply.getProfile_imgString()).fit().into(holder.imgView_profile);


             } else {
                if (itemControl_reply.getProfile_imgString() != null) {
//                    ExifInterface exif = null;
//                    try {
//                        exif = new ExifInterface(itemControl_reply.getProfile_imgString());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//                    int exifDegree = Factory.exifOrientationToDegrees(exifOrientation);
//
//                    Bitmap bitmap = BitmapFactory.decodeFile(itemControl_reply.getProfile_imgString());
//                    int limit_height = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
//                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, limit_height, true);
//                    holder.imgView_profile.setImageBitmap(Factory.rotate(scaled, exifDegree));
                    Glide.with(reply_context).load(itemControl_reply.getProfile_imgString()).into(holder.imgView_profile);


                } else if (itemControl_reply.getProfile_imgString() == null) {
//            Bitmap bitmap = BitmapFactory.decodeFile(itemControl_reply.getProfile_imgString());
//            holder.imgView_profile.setImageBitmap(bitmap);
//                    Toast.makeText(reply_context, "사진 x", Toast.LENGTH_SHORT).show(); /// null인데도 사진 셋 되서 위에 2라인 추가.
                }
            }

        SharedPreferences pref_reply = reply_context.getSharedPreferences("REPLIES_DATA"+Replies.post_position,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref_reply.edit();
        editor.putString("reply_user_name"+(position+1),itemControl_reply.getReplier());
        editor.putString("reply_img_str"+(position+1),itemControl_reply.getProfile_imgString());
        editor.putString("reply_contents"+(position+1),itemControl_reply.getReply_contents());
        editor.putInt("reply_usercode"+(position+1),itemControl_reply.getUser_code_for_permission());
        editor.putInt("last_arraysize",itemLists_reply.size()); //
        editor.apply();





    }

    @Override
    public int getItemCount() {
        return itemLists_reply.size();
    }


}
