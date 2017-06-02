package com.future.captcha;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author luchao
 * @time 17/5/25 上午10:47
 */

public class CaptchaDialog extends Dialog implements View.OnClickListener, Dialog.OnDismissListener{
    private CaptchaInputView mCaptchaInputView;
    private Builder mBuilder;
    private TextView mTvCaptchaSendTo;
    private Context mContext;
    private Activity mActivity;
    private TimerTextView mTimerTextView;
    private TextView mCaptchaErrorMsg;
    private TextView mCaptchaInputPrompt;
    private boolean isLock = false;

    //外部传入的接口
    private InputListener mInputListener;
    private View.OnClickListener mResendCaptchaListener;
    //外部传入的数据
    private String mPhoneNumber;
    private String mInputText;
    public InputMethodManager inputMethodManager;
    public CaptchaDialog(Context context) {
        this(context, null);
    }
    public CaptchaDialog(Context context, Builder builder) {
        super(context);
        setCancelable(true);
        this.mBuilder = builder;
        this.mContext = context;
        this.mActivity = (Activity) context;
        EditText e;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_captcha_dialog);
        inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        intViews();
        initData();
    }

    @Override
    public void onClick(View v) {

    }

    public void setPhoneNumber(String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
    }

    public void setCaptchaText(String text) {
        this.mInputText = text;
    }

    private void intViews() {
        mCaptchaInputView = (CaptchaInputView) findViewById(R.id.captcha_input_view);
        mCaptchaInputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() >= 4) {
                    mInputListener.inputComplete(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTvCaptchaSendTo = (TextView) findViewById(R.id.captcha_send_to);
        mTimerTextView = (TimerTextView) findViewById(R.id.captcha_timer);
        mTimerTextView.setOnClickListener(mResendCaptchaListener);
        mCaptchaErrorMsg = (TextView) findViewById(R.id.captcha_error_msg);
        mCaptchaInputView.setCursorVisible(false);
        mCaptchaInputView.setFocusable(true);
        mCaptchaInputView.setFocusableInTouchMode(true);
        mCaptchaInputView.requestFocus();
        mCaptchaInputPrompt = (TextView) findViewById(R.id.input_captcha_prompt);
        startTimer();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void setErrorMsg(String msg) {
        if(mCaptchaErrorMsg != null) {
            mCaptchaErrorMsg.setText(msg);
        }
        if(mCaptchaInputView != null) {
            mCaptchaInputView.setText("");
        }
    }

    private void initData() {
        mCaptchaInputView.setText(mInputText);
        mTvCaptchaSendTo.setText(String.format(mContext.getResources().getString(R.string.dialog_captcha_sent_to), mPhoneNumber));
        if(mBuilder != null) {
            mCaptchaInputView.setPwdVisiable(mBuilder.getCaptchaVisible());
        }
    }

    public void setInputListener(InputListener inputListener) {
        this.mInputListener = inputListener;
    }

    public void setReSendCaptchaListener(View.OnClickListener onClickListener) {
        this.mResendCaptchaListener = onClickListener;
    }

    public void startTimer() {
        if(mActivity!= null && mTimerTextView != null) {
            mTimerTextView.setBackground(null);
            mTimerTextView.startTimer(new TimerListener());
        }
    }

    class TimerListener implements TimerTextView.TimerListener {

        @Override
        public void onFinish() {
            mTimerTextView.setBackgroundResource(R.drawable.bg_captcha_input);
            mTimerTextView.setText(mContext.getResources().getString(R.string.dialog_captcha_resent));
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(mTimerTextView != null) {
            mTimerTextView.cancel();
        }
        if(mCaptchaInputView != null) {
            mCaptchaInputView.stopCursor();
        }
    }

    public interface InputListener {
        void inputComplete(String text);
    }

    public static class Builder {
        private Context mContext;
        public Builder(Context mContext) {
            this.mContext = mContext;
        }
        private boolean mCaptchaVisiable = true;
        public Builder setCaptchaVisiable(boolean mCaptchaVisiable) {
            this.mCaptchaVisiable = mCaptchaVisiable;
            return this;
        }

        boolean getCaptchaVisible() {
            return mCaptchaVisiable;
        }

        public CaptchaDialog create() {
            return new CaptchaDialog(mContext, this);
        }
    }


    private void lock() {
        mCaptchaInputView.setFocusable(false);
        mCaptchaInputView.setFocusableInTouchMode(false);
        mCaptchaInputView.setEnabled(false);
        isLock = true;
        hideSoftInput(mCaptchaInputView);
    }

    private void unLock() {
        mCaptchaInputView.setFocusable(true);
        mCaptchaInputView.setFocusableInTouchMode(true);
        mCaptchaInputView.setEnabled(true);
        mCaptchaInputView.requestFocus();
        isLock = false;
        showSoftInput();
    }

    private boolean isLock() {
        return isLock;
    }

    /**
     * 判断软键盘 弹出
     */
    public void showSoftInput() {
        if (inputMethodManager.isActive()) {
            inputMethodManager.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
        }
    }

    /**
     * 关闭软键盘 *针对于 有一个EdtxtView * @param input_email
     */
    public void hideSoftInput(EditText input_email) {
        if (inputMethodManager.isActive()) {
            // 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
            inputMethodManager.hideSoftInputFromWindow(input_email.getWindowToken(), 0);
        }
    }
}
