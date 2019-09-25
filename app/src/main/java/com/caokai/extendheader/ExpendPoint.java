package com.caokai.extendheader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author caokai
 * @time 2019/9/6
 */
public class ExpendPoint extends View {
    Paint mPaint;
    float maxDist;
    float maxRadius;
    float percent;
    Vibrator mVibrator;
    public ExpendPoint(Context context) {
        this(context, null);
    }

    public ExpendPoint(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ExpendPoint(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.maxRadius = 15.0f;
        this.maxDist = 60.0f;
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(-7829368);
        mVibrator= (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void setMaxRadius(int i) {
        this.maxRadius = (float) i;
    }

    public void setMaxDist(float f) {
        this.maxDist = f;
    }

    public void setPercent(float f) {
        if (f != this.percent) {
            if(f==1.0f){
                mVibrator.vibrate(10);
            }
            this.percent = f;
            invalidate();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.maxRadius = ((float) getHeight()) / 2.0f;
        float width = ((float) getWidth()) / 2.0f;
        float height = ((float) getHeight()) / 2.0f;
        if (this.percent <= 0.5f) {
            this.mPaint.setAlpha(255);
            canvas.drawCircle(width, height, (this.percent * 2.0f) * this.maxRadius, this.mPaint);
            return;
        }
        float f = (this.percent - 0.5f) / 0.5f;
        canvas.drawCircle(width, height, this.maxRadius - ((this.maxRadius / 2.0f) * f), this.mPaint);
        canvas.drawCircle(width - (this.maxDist * f), height, this.maxRadius / 2.0f, this.mPaint);
        canvas.drawCircle(width + (f * this.maxDist), height, this.maxRadius / 2.0f, this.mPaint);
    }
}
