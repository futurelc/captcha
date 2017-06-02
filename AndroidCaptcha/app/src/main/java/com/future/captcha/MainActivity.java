package com.future.captcha;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mBtn;
    private CaptchaDialog captchaDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtn = (Button) findViewById(R.id.show);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captchaDialog = new CaptchaDialog(MainActivity.this);
                captchaDialog.setPhoneNumber("1234567890");
                captchaDialog.setInputListener(new CaptchaDialog.InputListener() {
                    @Override
                    public void inputComplete(String text) {

                    }
                });
                captchaDialog.setReSendCaptchaListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        captchaDialog.startTimer();
                    }
                });
                captchaDialog.setErrorMsg("短信验证码有误,请稍后再试!!!");
                captchaDialog.show();
            }
        });


    }
}
