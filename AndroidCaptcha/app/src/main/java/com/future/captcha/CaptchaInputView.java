package com.future.captcha;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * @author luchao
 */
public class CaptchaInputView extends EditText {
	private int borderColor;
	private float borderWidth;
	private float borderRadius;

	private int passwordLength = 4;
	private int passwordColor;
	private float passwordWidth;
	private float passwordRadius;

	private Paint passwordPaint = new Paint(ANTI_ALIAS_FLAG);
	private Paint borderPaint = new Paint(ANTI_ALIAS_FLAG);
	private Paint linePaint = new Paint(ANTI_ALIAS_FLAG);


	private final int defaultContMargin = 5;
	private final int defaultSplitLineWidth = 3;

	private TextChangeListener mTextChangeListener;
	private float mDefaultInputViewTextSize, mDefaultInputViewPadding, mDefaultInputTextSize;
	private float mCursorWidth;
	private int mCursorHeight;
	private float mDefalutMargin = 10;
	private boolean mPwdVisiable = true;
	private String mInputText;
	private List<RectF> rectList;
	private Context mContext;
	private int mSelectIndex = 0;
	private Handler mCursorHandler;
	private CursorRunnable mCursorRunnable;
	static final int CURSOR_DELAY_TIME = 500;

	public CaptchaInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		final Resources res = getResources();
		final int defaultBorderColor = res.getColor(R.color.default_ev_border_color);
		final float defaultBorderWidth = res.getDimension(R.dimen.default_ev_border_width);
		final float defaultBorderRadius = res.getDimension(R.dimen.default_ev_border_radius);
		final int defaultPasswordLength = res.getInteger(R.integer.default_ev_password_length);
		final int defaultPasswordColor = res.getColor(R.color.default_ev_password_color);
		final float defaultPasswordWidth = res.getDimension(R.dimen.default_ev_password_width);
		final float defaultPasswordRadius = res.getDimension(R.dimen.default_ev_password_radius);
		final float defaultInputViewTextSize = res.getDimension(R.dimen.default_input_text_view_size);
		final float defaultInputViewPadding = res.getDimension(R.dimen.default_input_text_view_padding);
		final float defaultInputTextSize = res.getDimension(R.dimen.default_input_text_size);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PasswordInputView, 0, 0);
		try {
			borderColor = a.getColor(R.styleable.PasswordInputView_captchaBorderColor, defaultBorderColor);
			borderWidth = a.getDimension(R.styleable.PasswordInputView_captchaBorderWidth, defaultBorderWidth);
			borderRadius = a.getDimension(R.styleable.PasswordInputView_captchaBorderRadius, defaultBorderRadius);
			passwordLength = a.getInt(R.styleable.PasswordInputView_captchaLength, defaultPasswordLength);
			passwordColor = a.getColor(R.styleable.PasswordInputView_captchaColor, defaultPasswordColor);
			passwordWidth = a.getDimension(R.styleable.PasswordInputView_captchaWidth, defaultPasswordWidth);
			passwordRadius = a.getDimension(R.styleable.PasswordInputView_captchaRadius, defaultPasswordRadius);
			mDefaultInputViewTextSize =  a.getDimension(R.styleable.PasswordInputView_captchaViewSize, defaultInputViewTextSize);
			mDefaultInputViewPadding = a.getDimension(R.styleable.PasswordInputView_captchaViewSize, defaultInputTextSize);
			mDefaultInputTextSize = a.getDimension(R.styleable.PasswordInputView_captchaTextSize, defaultInputViewPadding);
		} finally {
			a.recycle();
		}

		mCursorWidth = mContext.getResources().getDimension(R.dimen.captcha_cursor_width);
		mCursorHeight = (int) mContext.getResources().getDimension(R.dimen.captcha_cursor_height);

		borderPaint.setStrokeWidth(borderWidth);
		borderPaint.setColor(borderColor);
		linePaint.setColor(getResources().getColor(R.color.select_border_color));
		linePaint.setStrokeWidth(mCursorWidth);        //绘制直线

		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setAntiAlias(true);

		passwordPaint.setColor(passwordColor);
		passwordPaint.setTextSize(mDefaultInputTextSize);
		rectList = new ArrayList<>();

		mCursorHandler = new Handler();
		mCursorRunnable = new CursorRunnable();
		mCursorHandler.post(mCursorRunnable);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = passwordLength * (int)mDefaultInputViewTextSize + (int)mDefaultInputViewPadding * 3 + (int)mDefalutMargin*2;
		int height = (int)mDefaultInputViewTextSize + (int)mDefalutMargin*2;
		setMeasuredDimension(width, height);
	}

	private class CursorRunnable implements Runnable {
		private boolean mCancelled = false;
		private boolean mCursorVisible = false;
		@Override
		public void run() {
			if (mCancelled) {
				return;
			}
			postInvalidate();
			postDelayed(this, CURSOR_DELAY_TIME);
		}

		void cancel() {
			if (!mCancelled) {
				mCursorHandler.removeCallbacks(this);
				mCancelled = true;
			}
		}

		public boolean getCursorVisiable() {
			return mCursorVisible = !mCursorVisible;
		}
	}


	public void stopCursor() {
		if(mCursorRunnable != null && mCursorHandler != null) {
			mCursorRunnable.cancel();
		}
	}

//	点击事件的处理
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		float x = event.getX();
//		float y = event.getY();
//		switch (event.getAction()) {
//			case MotionEvent.ACTION_DOWN:
//			for(int i = 0; i< rectList.size(); i++) {
//				RectF rectF = rectList.get(i);
//				if(rectF.contains(x, y)) {
//					mSelectIndex = i;
//					postInvalidate();
//
//					Log.d("draw", "index="+i);
//					break;
//				}
//			}
//			break;
//		}
//		return super.onTouchEvent(event);
//	}

	@Override
	protected void onDraw(Canvas canvas) {
		rectList.clear();
		//边框
		int left = (int)mDefalutMargin;
		int top = (int)mDefalutMargin;
		for(int i = 0; i < passwordLength; i++) {
			if(i < mSelectIndex) {
				//彩色边框
				borderPaint.setColor(getResources().getColor(R.color.select_border_color));
			} else {
				//灰色边框
				borderPaint.setColor(borderColor);
			}
			RectF rectF = new RectF(left, top, mDefaultInputViewTextSize + left, mDefaultInputViewTextSize + top);
			rectList.add(rectF);
			canvas.drawRoundRect(rectF, 0, 0, borderPaint);
			left+= mDefaultInputViewPadding + mDefaultInputViewTextSize;
		}

		//内容,密码可见
		int textLeft = (int)mDefalutMargin + (int)mDefaultInputViewTextSize/2;
		if(mPwdVisiable) {
			for(int i = 0; i < mInputText.length(); i++) {
				String text = mInputText.substring(i, i + 1);
				int textWidth = !TextUtils.isEmpty(text) ? getTextWidth(passwordPaint, text)/2 : 0;
				canvas.drawText(text, textLeft - textWidth, mDefaultInputViewTextSize/2 + mDefaultInputTextSize/2, passwordPaint);
				textLeft+= mDefaultInputViewPadding + mDefaultInputViewTextSize;
			}
		} else {
			for(int i = 0; i < mInputText.length(); i++) {
				String text = mInputText.substring(i, i + 1);
				int textWidth = !TextUtils.isEmpty(text) ? getTextWidth(passwordPaint, "*")/2 : 0;
				canvas.drawText("*", textLeft - textWidth, mDefaultInputViewTextSize/2 + mDefaultInputTextSize/2 + 5, passwordPaint);
				textLeft+= mDefaultInputViewPadding + mDefaultInputViewTextSize;
			}
		}

		//光标
		if(mSelectIndex < passwordLength && mCursorRunnable.getCursorVisiable()) {
			int cursorLeft = (int) mDefalutMargin;
			int cursorTop = (int) mDefalutMargin + ((int) mDefaultInputViewTextSize - mCursorHeight) / 2;
			int startX = cursorLeft + (int) mDefaultInputViewTextSize / 2 + mSelectIndex * (int) mDefaultInputViewTextSize + mSelectIndex * (int) mDefaultInputViewPadding;
			int stopX = startX;
			int startY = cursorTop;
			int stopY = startY + mCursorHeight;
			canvas.drawLine(startX, startY, stopX, stopY, linePaint);
		}

	}

	public int getTextWidth(Paint paint, String str) {
		int iRet = 0;
		if (str != null && str.length() > 0) {
			int len = str.length();
			float[] widths = new float[len];
			paint.getTextWidths(str, widths);
			for (int j = 0; j < len; j++) {
				iRet += (int) Math.ceil(widths[j]);
			}
		}
		return iRet;
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		if (mTextChangeListener != null) {
			mTextChangeListener.onTextChanged(text, start, lengthBefore, lengthAfter);
		}
		mInputText = text.toString();
		if(mInputText.length() > 0) {
			mSelectIndex = mInputText.length();
		} else {
			mSelectIndex = 0;
		}
		postInvalidate();
	}

	public void setPwdVisiable(boolean pwdVisiable) {
		this.mPwdVisiable = pwdVisiable;
	}

	public void setTextLength(int length) {
		this.passwordLength = length;
		postInvalidate();
	}

	public int getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(int borderColor) {
		this.borderColor = borderColor;
		borderPaint.setColor(borderColor);
		invalidate();
	}

	public float getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
		borderPaint.setStrokeWidth(borderWidth);
		invalidate();
	}

	public float getBorderRadius() {
		return borderRadius;
	}

	public void setBorderRadius(float borderRadius) {
		this.borderRadius = borderRadius;
		invalidate();
	}

	public int getPasswordLength() {
		return passwordLength;
	}

	public void setPasswordLength(int passwordLength) {
		this.passwordLength = passwordLength;
		invalidate();
	}

	public int getPasswordColor() {
		return passwordColor;
	}

	public void setPasswordColor(int passwordColor) {
		this.passwordColor = passwordColor;
		passwordPaint.setColor(passwordColor);
		invalidate();
	}

	public float getPasswordWidth() {
		return passwordWidth;
	}

	public void setPasswordWidth(float passwordWidth) {
		this.passwordWidth = passwordWidth;
		passwordPaint.setStrokeWidth(passwordWidth);
		invalidate();
	}

	public float getPasswordRadius() {
		return passwordRadius;
	}

	public void setPasswordRadius(float passwordRadius) {
		this.passwordRadius = passwordRadius;
		invalidate();
	}

	public void setTextChangeListener(TextChangeListener mTextChangeListener) {
		this.mTextChangeListener = mTextChangeListener;
	}

	public interface TextChangeListener {
		void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter);
	}
}
