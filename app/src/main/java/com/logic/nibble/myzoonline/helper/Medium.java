package com.logic.nibble.myzoonline.helper;
/*
 *
 * Created by Parth Patel
 * Date : 19-01-2020 12:51 AM
 *
 * */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.logic.nibble.myzoonline.LoginActivity;
import com.logic.nibble.myzoonline.api.APIClient;
import com.logic.nibble.myzoonline.api.APIService;
import com.logic.nibble.myzoonline.global.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Medium {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static boolean checkPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    public static void prepareMessage(Context context, String message) {
        Toast.makeText(context, message.toString(), Toast.LENGTH_LONG).show();
    }


    public static String capitaliseName(String name) {
        name = name.toLowerCase();
        String collect[] = name.split(" ");
        String returnName = "";
        for (int i = 0; i < collect.length; i++) {
            collect[i] = collect[i].trim().toLowerCase();
            if (collect[i].isEmpty() == false) {
                returnName = returnName + collect[i].substring(0, 1).toUpperCase() + collect[i].substring(1) + " ";
            }
        }
        return returnName.trim();
    }

    //    1 => success ,0 => error, 2 =>normal/hold
    public synchronized static int renderResponse(Context context, JSONObject response, boolean isLoginRequest) {
        Log.d("PARTH DEBUG", "RENDER RESPONSE: calling common method");
        try {
            JSONObject jsonObj = response;
            int responseCode = Integer.parseInt(jsonObj.getString("status_code").trim());
            Log.d("PARTH DEBUG", "RENDER RESPONSE CODE: " + responseCode);
            if (responseCode == 200) {
                // 200 => success
                return 1;
            } else if (responseCode == 503) {
                // 503 => Invalid username or password, OTP expired
                String msg = jsonObj.getString("api_message");
                Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
                return 2;
            } else if (responseCode == 400) {
                // 400 => Missing parameters
                String msg = jsonObj.getString("api_message");
                Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
                return 2;
            } else if (responseCode == 102) {
                // 400 => Missing data or invalid parameters value (validation)
                String msg = jsonObj.getString("api_message");
                Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
                return 2;
            } else if (responseCode == 500) {
                // 500 => Internal Server Error (Something went wrong while database operation)
                String msg = jsonObj.getString("api_message");
                Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
                return 2;
            } else if (responseCode == 451 || responseCode == 403 || responseCode == 404) {
                // 403 => Session expired.
                // 404 => User is deleted or disabled.
                // 451 => Site under maintanance.
                String msg = jsonObj.getString("api_message");
                Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
                if (!isLoginRequest) {
                    MySession appSession = new MySession(context);
                    appSession.logout();
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    return 0;
                } else {
                    return 2;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Something went wrong, Please try again later.", Toast.LENGTH_LONG).show();
            return 0;
        }
        return 0;
    }

    public synchronized static void authSession(final Context context) {
        APIService apiService = APIClient.getClient(context).create(APIService.class);
        MySession appSession = new MySession(context);
        if (appSession.getShop_id() != "" && appSession.isIs_login()) {
            Call<ResponseBody> call = apiService.authShopSession(Constant.xapi.toString(), appSession.getAccess_token(), String.valueOf(appSession.getShop_id()));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        String jsonString = response.body().string();
                        JSONObject jsonObj = new JSONObject(jsonString);
                        int responseStatus = renderResponse(context, jsonObj, false);

                        if (responseStatus == 0) {
                            context.startActivity(new Intent(context, LoginActivity.class));
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        context.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    context.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
        } else {
            context.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }

}

