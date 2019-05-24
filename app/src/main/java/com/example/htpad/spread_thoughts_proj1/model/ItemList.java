package com.example.htpad.spread_thoughts_proj1.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.widget.Button;

public class ItemList {


    private String tittle;
    private String auther;
    private String imgString;
    private String contents;
    private String time;


    private String replier;
    private String reply_contents;
    private String profile_imgString;
    private int user_code_for_permission;

    public ItemList (@Nullable String tittle, @Nullable String auther, @Nullable String imgString, @Nullable String contents, @Nullable String time){
        this.tittle=tittle;
        this.auther=auther;
        this.imgString=imgString;
        this.contents=contents;
        this.time = time;
    }
    public ItemList (@Nullable String replier, @Nullable String reply_contents, @Nullable String profile_imgString, @Nullable int user_code_for_permission){
        this.replier=replier;
        this.reply_contents=reply_contents;
        this.profile_imgString=profile_imgString;
        this.user_code_for_permission=user_code_for_permission;

    }


    //name 리턴하는 메소드.
    public String getTittle(){
        return tittle;
    }
    public String getAuther(){
        return auther;
    }
    public String getImgString(){
        return imgString;
    }
    public String getContents(){
        return contents;
    }
    public String getTime(){
        return time;
    }

    public String getReplier(){
        return replier;
    }
    public String getReply_contents(){
        return reply_contents;
    }

    public String getProfile_imgString(){
        return profile_imgString;
    }

    public int getUser_code_for_permission(){
        return user_code_for_permission;
    }





}
