package jp.inara.sample.curcleui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class CurcleView extends View {
    
    static final int r = 300;
    static final int r2 = 80;
    Paint mPaint;
    RectF mOval = new RectF(0, 0, r * 2, r * 2);
    float mCurrentX = 0;
    float mCurrentY = 0;
    boolean mIsTouch = false;
    int mTouchArea = -1;

    
    public CurcleView(Context context) {
        super(context);
        init();
    }
    
    public CurcleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(r * 2, (int)(r + r * 0.1));
    }
    
    public void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 外側の円
        mPaint.setColor(Color.GRAY);
        canvas.drawCircle(r, r, r, mPaint);

        mPaint.setColor(Color.LTGRAY);
        mPaint.setStrokeWidth(4);

        float touchDeg = 0;
        float touchRadius = 0;
        if (mIsTouch) {
            float x = mCurrentX - r;
            float y = -(mCurrentY - r);

            double phi = Math.atan2(y, x);
            touchDeg = (float) (phi * 180 / Math.PI);
            touchRadius = (float) Math.sqrt(x * x + y * y);
        }

        int area = 0;
        mTouchArea = -1;
        for (float degree = -22.5f; degree < 180; degree += 45) {
            double phi = (double) (degree * Math.PI / 180);

            int x = (int) (r * Math.cos(phi)) + r;
            int y = -(int) (r * Math.sin(phi)) + r;

            // タッチした座標のエリアを赤色にする
            if (touchDeg > degree && touchDeg <= degree + 45 && touchRadius > r2 && touchRadius < r) {
                mPaint.setColor(Color.RED);

                int nx = (int) (r * Math.cos(phi + 45 * Math.PI / 180)) + r;
                int ny = -(int) (r * Math.sin(phi + 45 * Math.PI / 180)) + r;

                Path path = new Path();
                path.moveTo(nx, ny);
                path.lineTo(r, r);
                path.lineTo(x, y);
                path.arcTo(mOval, -degree, -45);

                canvas.drawPath(path, mPaint);

                mTouchArea = area;
            }

            // 区切り線
            if (degree > 0) {
                mPaint.setColor(Color.LTGRAY);
                canvas.drawLine(x, y, r, r, mPaint);
            }

            area++;
        }

        // 内側の円
        mPaint.setColor(Color.LTGRAY);
        canvas.drawCircle(r, r, r2, mPaint);

        mPaint.setColor(Color.GRAY);
        canvas.drawCircle(r, r, r2 - 4, mPaint);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        mCurrentX = event.getX();
        mCurrentY = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsTouch = true;
                break;

            case MotionEvent.ACTION_MOVE:
                mIsTouch = true;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsTouch = false;
                if(mTouchArea >= 0) {
                    onClick();
                }
                break;
        }

        invalidate();
        return true;
    }

    private void onClick() {
        Toast.makeText(getContext(), "touchArea = " + mTouchArea, Toast.LENGTH_SHORT).show();
    }
}
