package com.logic.nibble.myzoonline;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.logic.nibble.myzoonline.api.APIClient;
import com.logic.nibble.myzoonline.api.APIService;
import com.logic.nibble.myzoonline.global.Constant;
import com.logic.nibble.myzoonline.helper.Medium;
import com.logic.nibble.myzoonline.helper.MySession;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
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

public class UpdatePassword extends AppCompatActivity implements Validator.ValidationListener {
    MySession appSession;
    ProgressDialog progressDialog;
    Validator validator;

    CheckBox checkbox_logout_from_other_devices;
    @NotEmpty(message = "Please old password")
    EditText text_old_password;
    @Password(message = "Please enter new password")
    EditText text_change_new_password;
    @ConfirmPassword
    EditText text_change_confirm_password;

    Button button_update_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        Toolbar toolbar = findViewById(R.id.update_password_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        appSession = new MySession(getApplicationContext());
        Medium.authSession(getApplicationContext());
        initViews();
        validator = new Validator(this);
        validator.setValidationListener(this);


        bindEvents();
    }

    private void initViews() {
        checkbox_logout_from_other_devices = findViewById(R.id.checkbox_logout_from_other_devices);
        text_old_password = findViewById(R.id.text_old_password);
        text_change_new_password = findViewById(R.id.text_change_new_password);
        text_change_confirm_password = findViewById(R.id.text_change_confirm_password);
        button_update_password = findViewById(R.id.button_update_password);
    }

    private void bindEvents() {
        button_update_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startActivity(new Intent(getApplicationContext(), ConfirmActivity.class));
                if (Medium.isNetworkConnected(getApplicationContext())) {
                    validator.validate();
                } else {
                    Medium.prepareMessage(UpdatePassword.this, "No Internet Connection.");
                }
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Medium.authSession(getApplicationContext());
    }
    @Override
    public void onValidationSucceeded() {


        progressDialog = new ProgressDialog(UpdatePassword.this);
        progressDialog.setMessage("Updating....");
        progressDialog.show();

        //Get data : PP
        String old_password = text_old_password.getText().toString();
        String new_password = text_change_new_password.getText().toString();
        int logout_flag = 0;
        if(checkbox_logout_from_other_devices.isChecked()){
            logout_flag =1;
        }

        APIService apiService = APIClient.getClient(getApplicationContext()).create(APIService.class);
        Call<ResponseBody> call = apiService.updateNewPassword(Constant.xapi, appSession.getAccess_token(), appSession.getShop_id(),old_password,new_password,logout_flag);
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
                        appSession.setAccess_token(jsonObject.getString("access_token"));

                        text_old_password.setText("");
                        text_change_new_password.setText("");
                        text_change_confirm_password.setText("");
                        progressDialog.dismiss();

                        Medium.prepareMessage(UpdatePassword.this, jsonObj.getString("api_message"));
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
                } finally {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String jsonString = call.toString();
                t.printStackTrace();
                progressDialog.dismiss();
                Medium.prepareMessage(UpdatePassword.this, "Something went wrong..!");
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
                Medium.prepareMessage(UpdatePassword.this, message.toString());
            }
        }
    }
}

