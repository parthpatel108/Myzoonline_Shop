package com.logic.nibble.myzoonline;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.logic.nibble.myzoonline.api.APIClient;
import com.logic.nibble.myzoonline.api.APIService;
import com.logic.nibble.myzoonline.global.Constant;
import com.logic.nibble.myzoonline.helper.Medium;
import com.logic.nibble.myzoonline.helper.MySession;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener {
    TextView label_signup;
    Button button_login;
    @NotEmpty(message = "Please Enter Username")
    EditText text_username;
    @NotEmpty(message = "Please Enter Password")
    EditText text_password;
    ProgressDialog progressDialog;
    String username = "";
    String password = "";
    Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        validator = new Validator(this);
        validator.setValidationListener(this);
        bindEvents();

    }


    public void initViews() {
        label_signup = findViewById(R.id.label_signup);
        button_login = findViewById(R.id.button_login);
        text_username = findViewById(R.id.text_username);
        text_password = findViewById(R.id.text_password);
    }

    public void bindEvents() {
        label_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });


//        Login click
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Medium.isNetworkConnected(getApplicationContext())) {
                    validator.validate();
                } else {
                    Medium.prepareMessage(LoginActivity.this, "No Internet Connection.");
                }
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Authenticating....");
        progressDialog.show();

        //Get data : PP
        username = text_username.getText().toString().trim();
        password = text_password.getText().toString().trim();
        APIService apiService = APIClient.getClient(getApplicationContext()).create(APIService.class);
        Call<ResponseBody> call = apiService.shopLogin(Constant.xapi, username, password);
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

                        //Create session : PP
                        MySession appSession = new MySession(LoginActivity.this);
                        appSession.setShop_id(jsonObject.getString("shop_id"));
                        appSession.setShop_owner_firstname(jsonObject.getString("shop_owner_firstname"));
                        appSession.setShop_owner_lastname(jsonObject.getString("shop_owner_lastname"));
                        appSession.setShop_name(jsonObject.getString("shop_name"));
                        appSession.setShop_digital_id(jsonObject.getString("shop_digital_id"));
                        appSession.setQrurl(jsonObject.getString("qrcode_image_path"));
                        appSession.setAccess_token(jsonObject.getString("access_token"));
                        appSession.setIs_login(true);
                        progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "" + jsonObj.getString("api_message"), Toast.LENGTH_LONG).show();
                        finish();

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
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String jsonString = call.toString();
                t.printStackTrace();
                progressDialog.dismiss();
                Medium.prepareMessage(LoginActivity.this, "Something went wrong..!");
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
                Medium.prepareMessage(LoginActivity.this, message.toString());
            }
        }
    }
}
