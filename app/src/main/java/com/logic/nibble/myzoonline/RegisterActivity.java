package com.logic.nibble.myzoonline;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;
import com.logic.nibble.myzoonline.api.APIClient;
import com.logic.nibble.myzoonline.api.APIService;
import com.logic.nibble.myzoonline.global.Constant;
import com.logic.nibble.myzoonline.helper.Medium;
import com.logic.nibble.myzoonline.helper.MySession;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements Validator.ValidationListener {
    TextView label_login;
    Button button_register;
    RadioButton radio_gender;
    @Checked (message = "Please select gender")
    RadioGroup radio_group_gender;
    @NotEmpty (message = "Please enter owner first name")
    EditText text_firstname;
    @NotEmpty (message = "Please enter owner last name")
    EditText text_lastname;
    @NotEmpty (message = "Please enter shop name")
    EditText text_shop;
    @NotEmpty (message = "Please enter mobile number")
    EditText text_mobile;
    @Password (message = "Please enter password")
    EditText text_register_password;
    @ConfirmPassword
    EditText text_register_confirm_password;
    CountryCodePicker mobile_code;
    Validator validator;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        validator = new Validator(this);
        validator.setValidationListener(this);
        initViews();
        bindEvents();

        // Register mobile number with country code picker : PP
        mobile_code.registerCarrierNumberEditText(text_mobile);
//        mobile_code.getSelectedCountryCode();
    }

    public void initViews() {
        label_login = findViewById(R.id.label_login);
        button_register = findViewById(R.id.button_register);
        radio_group_gender = findViewById(R.id.radio_group_gender);
        text_firstname = findViewById(R.id.text_firstname);
        text_lastname = findViewById(R.id.text_lastname);
        text_shop = findViewById(R.id.text_shop);
        text_register_password = findViewById(R.id.text_register_password);
        text_register_confirm_password = findViewById(R.id.text_register_confirm_password);
        text_mobile = findViewById(R.id.text_mobile);
        mobile_code = findViewById(R.id.mobile_code);
    }


    public void bindEvents() {
        label_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startActivity(new Intent(getApplicationContext(), ConfirmActivity.class));
                if (Medium.isNetworkConnected(getApplicationContext())) {
                    validator.validate();
                } else {
                    Medium.prepareMessage(RegisterActivity.this, "No Internet Connection.");
                }

            }
        });

    }

    public boolean validateAndRegister() {

        return false;
    }

    @Override
    public void onValidationSucceeded() {


        if (!mobile_code.isValidFullNumber()){
            text_mobile.setError("Please enter valid mobile no.");
            return;
        }

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Processing....");
        progressDialog.show();

        //Get data : PP

        String country_code = mobile_code.getSelectedCountryCode().toString();
        String full_mobile_no = mobile_code.getFullNumber().toString();
        String mobile_no = text_mobile.getText().toString();
        String first_name = text_firstname.getText().toString();
        String last_name = text_lastname.getText().toString();
        String shop_name = text_shop.getText().toString();
        String password = text_register_password.getText().toString();
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
        Call<ResponseBody> call = apiService.createShopAccount(Constant.xapi,first_name,last_name,shop_name,mobile_no,country_code,full_mobile_no,String.valueOf(gender),password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String jsonString = response.body().string();
                    JSONObject jsonObj = new JSONObject(jsonString);
                    int responseStatus = Medium.renderResponse(getApplicationContext(), jsonObj, true);
                    Log.d("PARTH DEBUG", "Response status: " + responseStatus);

                    if (responseStatus == 1) {

                        JSONObject jsonObject = jsonObj.getJSONObject("data");
                        //Create session : PP
                        MySession appSession = new MySession(RegisterActivity.this);
                        appSession.setShop_id(jsonObject.getString("shop_id"));
                        appSession.setShop_owner_firstname(jsonObject.getString("shop_owner_firstname"));
                        appSession.setShop_owner_lastname(jsonObject.getString("shop_owner_lastname"));
                        appSession.setShop_name(jsonObject.getString("shop_name"));
                        appSession.setShop_digital_id(jsonObject.getString("shop_digital_id"));
                        appSession.setMasked_number(jsonObject.getString("masked_number"));
                        //appSession.setQrurl(jsonObject.getString("qrcode_image_path"));
                        //appSession.setAccess_token(jsonObject.getString("access_token"));
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), jsonObj.getString("api_message"), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), ConfirmActivity.class);
                        startActivity(intent);
//                        finish();

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
                Medium.prepareMessage(RegisterActivity.this, "Something went wrong..!");
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
                Medium.prepareMessage(RegisterActivity.this, message.toString());
            }
        }
    }
}
