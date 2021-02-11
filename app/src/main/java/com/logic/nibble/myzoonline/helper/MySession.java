package com.logic.nibble.myzoonline.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.logic.nibble.myzoonline.LoginActivity;

public class MySession {

    Context context;
    SharedPreferences preference;
    private String shop_id;
    private String shop_name;
    private String shop_owner_firstname;
    private String shop_owner_lastname;
    private String access_token;
    private String shop_digital_id;
    private String qrurl = "na";

    private boolean is_login = false;
    private String masked_number;

    public MySession(Context context) {
        this.context = context;
        this.preference = context.getSharedPreferences("com.pdev.app.loggedin", Context.MODE_PRIVATE);
    }

    public boolean isIs_login() {
        return preference.getBoolean("is_login",false);
    }

    public void setIs_login(boolean is_login) {
        this.is_login = is_login;
        preference.edit().putBoolean("is_login", is_login).commit();
    }

    public String getMasked_number() {
        return preference.getString("masked_number", "");
    }

    public void setMasked_number(String masked_number) {
        this.masked_number = masked_number;
        preference.edit().putString("masked_number", masked_number).commit();
    }

    public String getQrurl() {
        return preference.getString("qrurl", "na");
    }

    public void setQrurl(String qrurl) {
        this.qrurl = qrurl;
        preference.edit().putString("qrurl", qrurl).commit();
    }

    public String getShop_digital_id() {
        return preference.getString("shop_digital_id", "");
    }

    public void setShop_digital_id(String shop_digital_id) {
        this.shop_digital_id = shop_digital_id;
        preference.edit().putString("shop_digital_id", shop_digital_id).commit();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getShop_id() {
        return preference.getString("shop_id", "");
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
        preference.edit().putString("shop_id", shop_id).commit();
    }

    public String getShop_name() {
        return preference.getString("shop_name", "");
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
        preference.edit().putString("shop_name", shop_name).commit();
    }

    public String getShop_owner_firstname() {
        return preference.getString("shop_owner_firstname", "");
    }

    public void setShop_owner_firstname(String shop_owner_firstname) {
        this.shop_owner_firstname = shop_owner_firstname;
        preference.edit().putString("shop_owner_firstname", shop_owner_firstname).commit();
    }

    public String getShop_owner_lastname() {
        return preference.getString("shop_owner_lastname", "");
    }

    public void setShop_owner_lastname(String shop_owner_lastname) {
        this.shop_owner_lastname = shop_owner_lastname;
        preference.edit().putString("shop_owner_lastname", shop_owner_lastname).commit();
    }

    public String getAccess_token() {
        access_token = preference.getString("access_token", "");
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
        preference.edit().putString("access_token", access_token).commit();

    }


    public SharedPreferences getPreference() {
        return preference;
    }

    public void setPreference(SharedPreferences preference) {
        this.preference = preference;
    }

    public void logout() {
        preference.edit().clear().commit();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
