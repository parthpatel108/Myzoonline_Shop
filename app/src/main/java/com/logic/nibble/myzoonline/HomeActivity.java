package com.logic.nibble.myzoonline;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.logic.nibble.myzoonline.alert.SweetAlertDialog;
import com.logic.nibble.myzoonline.api.APIClient;
import com.logic.nibble.myzoonline.api.APIService;
import com.logic.nibble.myzoonline.global.Constant;
import com.logic.nibble.myzoonline.helper.Medium;
import com.logic.nibble.myzoonline.helper.MySession;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    TextView text_digital_username, text_shop_name;
    ImageView image_qrcode;
    MySession appSession;
    CardView card_my_profile;
    SwipeRefreshLayout home_refresh_layout;
    ShimmerFrameLayout home_shimmer_view_container;
    ScrollView home_scroll_container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dashboard");
        appSession = new MySession(getApplicationContext());
        Medium.authSession(getApplicationContext());

        initViews();
        bindEvents();
        home_refresh_layout.setColorSchemeResources(R.color.colorPrimary);
        text_digital_username.setText(appSession.getShop_digital_id().toString());
        text_shop_name.setText(appSession.getShop_name().toString());
        if (!appSession.getQrurl().equalsIgnoreCase("na")){
            Picasso.get()
                    .load(appSession.getQrurl().toString())
                    .into(image_qrcode);
        }

    }

    public void initViews() {
        text_digital_username = findViewById(R.id.text_digital_username);
        text_shop_name = findViewById(R.id.text_shop_name);
        image_qrcode= findViewById(R.id.image_qrcode);
        card_my_profile = findViewById(R.id.card_my_profile);
        home_refresh_layout = findViewById(R.id.home_refresh_layout);
        home_shimmer_view_container = findViewById(R.id.home_shimmer_view_container);
        home_scroll_container = findViewById(R.id.home_scroll_container);
    }

    public void bindEvents() {
        card_my_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
            }
        });
        home_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Medium.isNetworkConnected(getApplicationContext())) {
                    fetchProfile();
                    home_refresh_layout.setRefreshing(false);
                } else {
                    Medium.prepareMessage(HomeActivity.this, "No Internet Connection.");
                    home_refresh_layout.setRefreshing(false);
                }
            }
        });
    }
    private void fetchProfile() {
//        progressDialog = new ProgressDialog(ProfileActivity.this);
//        progressDialog.setMessage("Viewing..");
//        progressDialog.show();
        setView(true);

        APIService apiService = APIClient.getClient(getApplicationContext()).create(APIService.class);
        Call<ResponseBody> call = apiService.shopProfile(Constant.xapi, appSession.getAccess_token(), appSession.getShop_id());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String jsonString = response.body().string();
                    JSONObject jsonObj = new JSONObject(jsonString);
                    int responseStatus = Medium.renderResponse(getApplicationContext(), jsonObj, true);
                    Log.d("PARTH DEBUG", "Response status: " + responseStatus);

                    if (responseStatus == 1) {
//                        Toast.makeText(getApplicationContext(), jsonObj.getString("api_message"), Toast.LENGTH_LONG).show();

//                        JSONObject jsonObject = jsonObj.getJSONObject("data");
//                        try {
//                            Picasso.get()
//                                    .load(jsonObject.getString("shop_logo_image"))
//                                    .into(profile_image);
//                        } catch (Exception e) {
//
//                        }
//                        label_profile_digital_id.setText(jsonObject.getString("shop_digital_id"));
//                        text_edit_shop.setText(jsonObject.getString("shop_name"));
//                        text_edit_address1.setText(jsonObject.getString("shop_address_1"));
//                        text_edit_address2.setText(jsonObject.getString("shop_addresss_2"));
//                        text_edit_city.setText(jsonObject.getString("shop_city"));
//                        text_edit_state.setText(jsonObject.getString("shop_state"));
//                        text_edit_firstname.setText(jsonObject.getString("shop_owner_firstname"));
//                        text_edit_lastname.setText(jsonObject.getString("shop_owner_lastname"));
//                        text_edit_email.setText(jsonObject.getString("shop_email"));
//                        text_edit_zipcode.setText(jsonObject.getString("shop_zipcode"));
//                        if (Integer.parseInt(jsonObject.getString("shop_owner_gender")) == 1) {
//                            rbmale.setChecked(true);
//                            rbfemale.setChecked(false);
//                        } else {
//                            rbmale.setChecked(false);
//                            rbfemale.setChecked(true);
//                        }
//
//                        try{
//                            edit_owner_mobile_code.setFullNumber(jsonObject.getString("shop_owner_mobile_country_code")+jsonObject.getString("shop_owner_mobile_no"));
//
//                            edit_mobile_code.setFullNumber(jsonObject.getString("shop_mobile_country_code")+jsonObject.getString("shop_mobile_no"));
//
//                        }catch (Exception e){
//
//                        }
//                        progressDialog.dismiss();
                        setView(false);
                    } else if (responseStatus == 2) {
//                        progressDialog.dismiss();
                        setView(false);
                    } else if (responseStatus == 0) {
//                        progressDialog.dismiss();
                        setView(false);
                        finish();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    setView(false);
//                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    setView(false);
//                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String jsonString = call.toString();
                t.printStackTrace();
//                progressDialog.dismiss();
                setView(false);
                Medium.prepareMessage(HomeActivity.this, "Something went wrong..!");
            }
        });
    }

    public void setView(boolean flag){
        if(flag){
            home_scroll_container.setVisibility(View.GONE);
            home_shimmer_view_container.setVisibility(View.VISIBLE);
            home_shimmer_view_container.startShimmerAnimation();
        }else{
            home_scroll_container.setVisibility(View.VISIBLE);
            home_shimmer_view_container.setVisibility(View.GONE);
            home_shimmer_view_container.startShimmerAnimation();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Medium.authSession(getApplicationContext());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.WARNING_TYPE, SweetAlertDialog.SIMLE)
                        .setTitleText("Are you sure want to logout ?")
                        .setContentText("You will be returned to login screen")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                appSession.logout();
                                finish();
                            }
                        })
                        .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();

                return true;
        }
        return (super.onOptionsItemSelected(item));
    }
}
