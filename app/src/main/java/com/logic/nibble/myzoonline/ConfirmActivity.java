package com.logic.nibble.myzoonline;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.logic.nibble.myzoonline.api.APIClient;
import com.logic.nibble.myzoonline.api.APIService;
import com.logic.nibble.myzoonline.events.OTPTextWatcher;
import com.logic.nibble.myzoonline.global.Constant;
import com.logic.nibble.myzoonline.helper.Medium;
import com.logic.nibble.myzoonline.helper.MySession;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmActivity extends AppCompatActivity {
    EditText otp_1, otp_2, otp_3, otp_4;
    Button button_confirm;
    TextView text_verification_message;
    TextView label_resend_timer, label_resend;
    ProgressDialog progressDialog;
    MySession appSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        Toolbar toolbar = findViewById(R.id.confirm_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("OTP Verification");

        initViews();
        bindEvents();

        appSession = new MySession(getApplicationContext());
        if (appSession.getShop_id() != "") {
            String message = "Hi " + Medium.capitaliseName(appSession.getShop_owner_firstname()) + ", We sent a verification \ncode to +" + appSession.getMasked_number();
            text_verification_message.setText(message);
        } else {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            finish();
        }
        startTimerForResend();
    }


    public void initViews() {
        otp_1 = findViewById(R.id.otp_1);
        otp_2 = findViewById(R.id.otp_2);
        otp_3 = findViewById(R.id.otp_3);
        otp_4 = findViewById(R.id.otp_4);
        text_verification_message = findViewById(R.id.text_verification_message);
        label_resend_timer = findViewById(R.id.label_resend_timer);
        label_resend = findViewById(R.id.label_resend);
        button_confirm = findViewById(R.id.button_confirm);
    }

    public void bindEvents() {
        EditText[] edit = {otp_1, otp_2, otp_3, otp_4};
        otp_1.addTextChangedListener(new OTPTextWatcher(edit, otp_1));
        otp_2.addTextChangedListener(new OTPTextWatcher(edit, otp_2));
        otp_3.addTextChangedListener(new OTPTextWatcher(edit, otp_3));
        otp_4.addTextChangedListener(new OTPTextWatcher(edit, otp_4));

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String OTP = otp_1.getText().toString().trim() + otp_2.getText().toString().trim() + otp_3.getText().toString().trim() + otp_4.getText().toString().trim();
                if (OTP.length() == 4) {
                    verification_otp(OTP);
                } else {
                    Medium.prepareMessage(getApplicationContext(), "Please type verification code.");
                }
            }
        });

        label_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendOTP();
            }
        });
    }

    public void startTimerForResend() {
        label_resend.setVisibility(View.GONE);
        label_resend_timer.setVisibility(View.VISIBLE);
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {

                long sec = (millisUntilFinished / 1000) % 60;
                label_resend_timer.setText("Resend code in " + String.valueOf(sec) + " seconds");
            }

            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                label_resend.setVisibility(View.VISIBLE);
                label_resend_timer.setVisibility(View.GONE);
                label_resend_timer.setText("");
            }
        }.start();
    }

    public void resendOTP() {
        progressDialog = new ProgressDialog(ConfirmActivity.this);
        progressDialog.setMessage("Resending...");
        progressDialog.show();
        APIService apiService = APIClient.getClient(getApplicationContext()).create(APIService.class);
        Call<ResponseBody> call = apiService.authShopResendOTP(Constant.xapi.toString(), String.valueOf(appSession.getShop_id()));
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
                        Toast.makeText(getApplicationContext(), "" + jsonObj.getString("api_message"), Toast.LENGTH_LONG).show();
                        startTimerForResend();

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
                Medium.prepareMessage(ConfirmActivity.this, "Something went wrong..!");
            }
        });


    }

    public void verification_otp(String OTP) {
        {
            progressDialog = new ProgressDialog(ConfirmActivity.this);
            progressDialog.setMessage("Verifying...");
            progressDialog.show();

            APIService apiService = APIClient.getClient(getApplicationContext()).create(APIService.class);
            Call<ResponseBody> call = apiService.authShopOTP(Constant.xapi, appSession.getShop_id(), OTP);
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
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_NEW_TASK);
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
                    Medium.prepareMessage(ConfirmActivity.this, "Something went wrong..!");
                }
            });
        }
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
}
