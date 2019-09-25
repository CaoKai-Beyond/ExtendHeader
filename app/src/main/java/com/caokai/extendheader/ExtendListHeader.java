package com.caokai.extendheader;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author caokai
 * @time 2019/9/6
 */
public class ExtendListHeader extends ExtendLayout{
    boolean arrivedListHeight = false;
    float containerHeight = ((float)getDesityFloat(40.0f));
    float listHeight = ((float) getDesityFloat(90.0f));
    private ExpendPoint  mExpendPoint;
    private LinearLayout banner;
    public  int getDesityFloat(float f) {
        return (int) ((Resources.getSystem().getDisplayMetrics().density * f) + 0.5f);
    }

    public ExtendListHeader(Context context) {
        super(context);
    }

    public ExtendListHeader(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
    @Override
    public void bindView(View view) {
        this.banner =findViewById(R.id.banner);
        this.mExpendPoint = findViewById(R.id.expend_point);
    }


    @Override
    public View createLoadingView(Context context, AttributeSet attributeSet) {
        return LayoutInflater.from(context).inflate(R.layout.extend_header, null);
    }

    @Override
    public int getContentSize() {
        return (int) this.containerHeight;
    }

    @Override
    public int getListSize() {
        return (int) this.listHeight;
    }

    @Override
    public void onReset() {
        this.mExpendPoint.setVisibility(View.VISIBLE);
        this.mExpendPoint.setAlpha(1.0f);
        this.mExpendPoint.setTranslationY(0.0f);
        this.banner.setTranslationY(0.0f);
        this.arrivedListHeight = false;
    }

    @Override
    public void onReleaseToRefresh() {
    }

    @Override
    public void onPullToRefresh() {
    }

    @Override
    public void onArrivedListHeight() {
        this.arrivedListHeight = true;
    }

    @Override
    public void onRefreshing() {
    }

    @Override
    public void onPull(int i) {
        if (!this.arrivedListHeight) {
            this.mExpendPoint.setVisibility(View.VISIBLE);
            float abs = ((float) Math.abs(i)) / this.containerHeight;
            int abs2 = Math.abs(i) - ((int) this.containerHeight);
            if (abs <= 1.0f) {
                this.mExpendPoint.setPercent(abs);
                this.mExpendPoint.setTranslationY((float) (((-Math.abs(i)) / 2) + (this.mExpendPoint.getHeight() / 2)));
                this.banner.setTranslationY(-this.containerHeight);
            } else {
                abs = Math.min(1.0f, ((float) abs2) / (this.listHeight - this.containerHeight));
                this.mExpendPoint.setTranslationY(((float) (((-((int) this.containerHeight)) / 2) + (this.mExpendPoint.getHeight() / 2))) + ((((float) ((int) this.containerHeight)) * abs) / 2.0f));
                this.mExpendPoint.setPercent(1.0f);
                this.mExpendPoint.setAlpha(Math.max(1.0f - (abs * 2.0f), 0.0f));
                this.banner.setTranslationY((-(1.0f - abs)) * this.containerHeight);
            }
        }
        if (((float) Math.abs(i)) >= this.listHeight) {
            this.mExpendPoint.setVisibility(View.INVISIBLE);
            this.banner.setTranslationY((-(((float) Math.abs(i)) - this.listHeight)) / 2.0f);
        }
    }
}