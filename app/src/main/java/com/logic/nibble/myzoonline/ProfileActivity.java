package com.logic.nibble.myzoonline;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.hbb20.CountryCodePicker;
import com.logic.nibble.myzoonline.api.APIClient;
import com.logic.nibble.myzoonline.api.APIService;
import com.logic.nibble.myzoonline.global.Constant;
import com.logic.nibble.myzoonline.helper.Medium;
import com.logic.nibble.myzoonline.helper.MySession;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements Validator.ValidationListener {
    MySession appSession;
    TextView label_profile_digital_id;
    ProgressDialog progressDialog;
    CircleImageView profile_image;

    @NotEmpty(message = "Please enter shop name")
    EditText text_edit_shop;
    @NotEmpty (message = "Please enter mobile number")
    EditText text_edit_mobile;
    @NotEmpty (message = "Please enter address")
    EditText text_edit_address1;
    @NotEmpty (message = "Please enter owner first name")
    EditText text_edit_firstname;
    @NotEmpty (message = "Please enter owner last name")
    EditText text_edit_lastname;
    @Checked(message = "Please select gender")
    RadioGroup radio_group_gender;
    EditText text_owner_edit_mobile;
    EditText text_edit_address2;
    @NotEmpty (message = "Please enter city")
    EditText text_edit_city;
    @NotEmpty (message = "Please enter state")
    EditText text_edit_state;
    @NotEmpty (message = "Please enter zipcode")
    EditText text_edit_zipcode;
    EditText text_edit_email;
    Button button_save_profile;
    CountryCodePicker edit_mobile_code, edit_owner_mobile_code;
    RadioButton rbmale, rbfemale,radio_gender;
    Validator validator;
    SwipeRefreshLayout profile_refresh_layout;
    ShimmerFrameLayout shimmer_view_container;
    ScrollView profile_scroll_container;

    @Override
    protected void onRestart() {
        super.onRestart();
        Medium.authSession(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
        appSession = new MySession(getApplicationContext());
        Medium.authSession(getApplicationContext());
        initViews();
        validator = new Validator(this);
        validator.setValidationListener(this);
        edit_mobile_code.registerCarrierNumberEditText(text_edit_mobile);
        edit_owner_mobile_code.registerCarrierNumberEditText(text_owner_edit_mobile);

        profile_refresh_layout.setColorSchemeResources(R.color.colorPrimary);
        bindEvents();
        fetchProfile();

//        Set feed

    }

    private void initViews() {
        label_profile_digital_id = findViewById(R.id.label_profile_digital_id);
        profile_image = findViewById(R.id.profile_image);
        text_edit_shop = findViewById(R.id.text_edit_shop);
        text_edit_mobile = findViewById(R.id.text_edit_mobile);
        text_edit_address1 = findViewById(R.id.text_edit_address1);
        text_edit_address2 = findViewById(R.id.text_edit_address2);
        text_edit_city = findViewById(R.id.text_edit_city);
        text_edit_state = findViewById(R.id.text_edit_state);
        text_edit_firstname = findViewById(R.id.text_edit_firstname);
        text_edit_lastname = findViewById(R.id.text_edit_lastname);
        text_owner_edit_mobile = findViewById(R.id.text_owner_edit_mobile);
        text_edit_email = findViewById(R.id.text_edit_email);
        radio_group_gender = findViewById(R.id.radio_group_gender);
        text_edit_zipcode = findViewById(R.id.text_edit_zipcode);
        button_save_profile = findViewById(R.id.button_save_profile);
        edit_mobile_code = findViewById(R.id.edit_mobile_code);
        edit_owner_mobile_code = findViewById(R.id.edit_owner_mobile_code);
        rbmale = findViewById(R.id.rbmale);
        rbfemale = findViewById(R.id.rbfemale);
        profile_refresh_layout = findViewById(R.id.profile_refresh_layout);
        shimmer_view_container = findViewById(R.id.shimmer_view_container);
        profile_scroll_container = findViewById(R.id.profile_scroll_container);
    }

    private void bindEvents() {
        button_save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startActivity(new Intent(getApplicationContext(), ConfirmActivity.class));
                if (Medium.isNetworkConnected(getApplicationContext())) {
                    validator.validate();
                } else {
                    Medium.prepareMessage(ProfileActivity.this, "No Internet Connection.");
                }

            }
        });

        profile_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Medium.isNetworkConnected(getApplicationContext())) {
                    fetchProfile();
                    profile_refresh_layout.setRefreshing(false);
                } else {
                    Medium.prepareMessage(ProfileActivity.this, "No Internet Connection.");
                    profile_refresh_layout.setRefreshing(false);
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

                        JSONObject jsonObject = jsonObj.getJSONObject("data");
                        try {
                            Picasso.get()
                                    .load(jsonObject.getString("shop_logo_image"))
                                    .into(profile_image);
                        } catch (Exception e) {

                        }
                        label_profile_digital_id.setText(jsonObject.getString("shop_digital_id"));
                        text_edit_shop.setText(jsonObject.getString("shop_name"));
                        text_edit_address1.setText(jsonObject.getString("shop_address_1"));
                        text_edit_address2.setText(jsonObject.getString("shop_addresss_2"));
                        text_edit_city.setText(jsonObject.getString("shop_city"));
                        text_edit_state.setText(jsonObject.getString("shop_state"));
                        text_edit_firstname.setText(jsonObject.getString("shop_owner_firstname"));
                        text_edit_lastname.setText(jsonObject.getString("shop_owner_lastname"));
                        text_edit_email.setText(jsonObject.getString("shop_email"));
                        text_edit_zipcode.setText(jsonObject.getString("shop_zipcode"));
                        if (Integer.parseInt(jsonObject.getString("shop_owner_gender")) == 1) {
                            rbmale.setChecked(true);
                            rbfemale.setChecked(false);
                        } else {
                            rbmale.setChecked(false);
                            rbfemale.setChecked(true);
                        }

                        try{
                            edit_owner_mobile_code.setFullNumber(jsonObject.getString("shop_owner_mobile_country_code")+jsonObject.getString("shop_owner_mobile_no"));

                            edit_mobile_code.setFullNumber(jsonObject.getString("shop_mobile_country_code")+jsonObject.getString("shop_mobile_no"));

                        }catch (Exception e){

                        }
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
                Medium.prepareMessage(ProfileActivity.this, "Something went wrong..!");
            }
        });
    }

    public void setView(boolean flag){
        if(flag){
            profile_scroll_container.setVisibility(View.GONE);
            button_save_profile.setVisibility(View.GONE);
            shimmer_view_container.setVisibility(View.VISIBLE);
            shimmer_view_container.startShimmerAnimation();
        }else{
            profile_scroll_container.setVisibility(View.VISIBLE);
            button_save_profile.setVisibility(View.VISIBLE);
            shimmer_view_container.setVisibility(View.GONE);
            shimmer_view_container.startShimmerAnimation();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_change_password:
                startActivity(new Intent(ProfileActivity.this,UpdatePassword.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onValidationSucceeded() {


        if (!edit_mobile_code.isValidFullNumber()){
            text_edit_mobile.setError("Please enter valid mobile no.");
            return;
        }

        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Updating....");
        progressDialog.show();

        //Get data : PP

        String country_code = edit_mobile_code.getSelectedCountryCode().toString();
        String full_mobile_no = edit_mobile_code.getFullNumber().toString();
        String mobile_no = text_edit_mobile.getText().toString();
        String first_name = text_edit_firstname.getText().toString();
        String last_name = text_edit_lastname.getText().toString();
        String shop_name = text_edit_shop.getText().toString();
        String address1 = text_edit_address1.getText().toString();
        String address2 = text_edit_address2.getText().toString();
        String city = text_edit_city.getText().toString();
        String state = text_edit_state.getText().toString();
        String email = text_edit_email.getText().toString();
        String zipcode = text_edit_zipcode.getText().toString();
//        String password = text_register_password.getText().toString();
        int gender = 0;
        int genderId = (int) radio_group_gender.getCheckedRadioButtonId();
        radio_gender = findViewById(genderId);
        if (radio_gender != null){
            if (radio_gender.getText().toString().equalsIgnoreCase("male")){
                gender = 1;
            }else{
                gender = 2;
            }
        }
        Log.d("MOBILE NO-"+country_code+" GENDER- "+ gender,mobile_no);
        APIService apiService = APIClient.getClient(getApplicationContext()).create(APIService.class);
        Call<ResponseBody> call = apiService.updateShopProfile(Constant.xapi,appSession.getShop_id(),appSession.getAccess_token(),first_name,last_name,shop_name,mobile_no,country_code,full_mobile_no,String.valueOf(gender),city,state,address1,address2,email,zipcode);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String jsonString = response.body().string();
                    JSONObject jsonObj = new JSONObject(jsonString);
                    int responseStatus = Medium.renderResponse(getApplicationContext(), jsonObj, true);
                    Log.d("PARTH DEBUG", "Response status: " + responseStatus);

                    if (responseStatus == 1) {

                        progressDialog.dismiss();
                        Medium.prepareMessage(ProfileActivity.this,  jsonObj.getString("api_message"));

                    } else if (responseStatus == 2) {
                        progressDialog.dismiss();
                    } else if (responseStatus == 0) {
                        progressDialog.dismiss();
                        finish();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }finally {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String jsonString = call.toString();
                t.printStackTrace();
                progressDialog.dismiss();
                Medium.prepareMessage(ProfileActivity.this, "Something went wrong..!");
            }
        });
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Medium.prepareMessage(ProfileActivity.this, message.toString());
            }
        }
    }
}
