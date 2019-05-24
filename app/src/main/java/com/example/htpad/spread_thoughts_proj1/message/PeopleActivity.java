package com.example.htpad.spread_thoughts_proj1.message;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.htpad.spread_thoughts_proj1.R;
import com.example.htpad.spread_thoughts_proj1.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PeopleActivity extends Activity {


    private Button btn_close;

    void closeActivity (){
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        btn_close = (Button)findViewById(R.id.peopleActivity_previous);
        closeActivity();


        RecyclerView recyclerView = findViewById(R.id.peopleActivity_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new peopleActivityRecyclerViewAdapter());




    }

    class peopleActivityRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<UserModel> userModels;

        public peopleActivityRecyclerViewAdapter()
        {
            userModels = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("users")
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userModels.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        UserModel userModel = snapshot.getValue(UserModel.class);

                        if (userModel.uid.equals(myUid)){
                            continue;
                        }

                        userModels.add(snapshot.getValue(UserModel.class));
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people,parent,false);

            return new CustomViewHolder(view);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.peopleitem_imageview);
                textView = (TextView) view.findViewById(R.id.peopleitem_textview);

            }
        }




        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

            Glide.with(holder.itemView.getContext())
                    .load(userModels.get(position).profileImageUrl)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder)holder).imageView);


            ((CustomViewHolder)holder).textView.setText(userModels.get(position).userName);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent (PeopleActivity.this, MessageActivity.class);
                    intent.putExtra("destination_uid",userModels.get(position).uid);
                    intent.putExtra("destination_name",userModels.get(position).userName);
                    ActivityOptions activityOptions = null;
                        activityOptions = ActivityOptions.makeCustomAnimation(PeopleActivity.this,R.anim.fromright,R.anim.toleft);
                    startActivity(intent,activityOptions.toBundle());



                }
            });
        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }
    }











    public static int exifOrientationToDegrees(int exifOrientation){
        if(exifOrientation== ExifInterface.ORIENTATION_ROTATE_90){
            return 90;
        }else if(exifOrientation==ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        }else if(exifOrientation==ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
    }
    public static Bitmap rotate (Bitmap bitmap, float degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }


}
