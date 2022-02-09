package com.example.workwithmaps;

import android.content.Context;
import android.content.SharedPreferences;

public class Pref {
    Context context;
    SharedPreferences sp;
    public Pref(Context context) {
        this.context = context;
        sp=context.getSharedPreferences("MyData",Context.MODE_PRIVATE);
    }

    public String getUserName(){
        return sp.getString("username","NA");
    }
    public String getPhoneNo(){
        return sp.getString("phoneno","NA");
    }
}
