package com.caokai.extendheader;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * @author caokai
 * @time 2019/9/6
 */
public class ExtendListView extends ListView {
    private float lastY;
    private float mLastMotionY;
    private PullExtendLayout mPullExtendLayout;

    public ExtendListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ExtendListView(Context context) {
        super(context);
    }

    public ExtendListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setPullExtendLayout(PullExtendLayout pullExtendLayout) {
        this.mPullExtendLayout = pullExtendLayout;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            this.mLastMotionY = motionEvent.getY();
           getParent().requestDisallowInterceptTouchEvent(true);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            Log.d("dispatchTouchEvent",getFirstVisiblePosition()+"");
            if (this.lastY > motionEvent.getY() && getFirstVisiblePosition()==0) {
                float y = motionEvent.getY() - this.mLastMotionY;
                this.mLastMotionY = motionEvent.getY();
                if (VERSION.SDK_INT >= 19) {
                    if (!canScrollList(1) || (this.mPullExtendLayout != null && this.mPullExtendLayout.isReadyForPullDown(y))) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        return false;
                    }
                } else if (this.mPullExtendLayout != null && this.mPullExtendLayout.isReadyForPullDown(y)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
            } else if (motionEvent.getY() > this.lastY && getFirstVisiblePosition()==0) {
                if (VERSION.SDK_INT < 19) {
                    return false;
                }
                if (!canScrollList(-1)) {
                   getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
            }
        }
        this.lastY = motionEvent.getY();
        return super.dispatchTouchEvent(motionEvent);
    }
}
