package com.future.captcha;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by luchao on 16/7/5.
 */
public class TimerTextView extends TextView {
    private CountDownTimer timer;

    private TimerListener timerListener;
    private boolean isTiming = false;
    public TimerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTimer();
    }

    private void initTimer() {
        timer = new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                setText(millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                isTiming = false;
                setEnabled(true);
                timerListener.onFinish();
            }

        };
    }

    public void startTimer(TimerListener timerListener) {
        if (!isTiming){
            timer.cancel();
        }
        isTiming = true;
        setEnabled(false);
        this.timerListener = timerListener;
        timer.start();
    }

    public boolean isTiming() {
        return isTiming;
    }

    public interface TimerListener {
        void onFinish();
    }

    public void cancel() {
        timer.cancel();
        isTiming = false;
    }
}
