package com.caokai.extendheader;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

/**
 * @author caokai
 * @time 2019/9/6
 */
public class PullExtendLayout extends LinearLayout implements IPullToExtend {
    private static final int SCROLL_DURATION = 200;
    private int footerListHeight;
    public int headerListHeight;
    private int mFooterHeight;
    private ExtendLayout mFooterLayout;
    private int mHeaderHeight;
    private ExtendLayout mHeaderLayout;
    private boolean mInterceptEventEnable;
    private boolean mIsHandledTouchEvent;
    private float mLastMotionY;
    private boolean mPullLoadEnabled;
    private boolean mPullRefreshEnabled;
    View mRefreshableView;
    private SmoothScrollRunnable mSmoothScrollRunnable;
    private int mTouchSlop;
    private float offsetRadio;

    //滑动阻尼系数
    private final float SCROLL_DAMP=0.75f;

    final class SmoothScrollRunnable implements Runnable {
        private boolean mContinueRunning = true;
        private int mCurrentY = -1;
        private final long mDuration;
        private final Interpolator mInterpolator;
        private final int mScrollFromY;
        private final int mScrollToY;
        private long mStartTime = -1;

        public SmoothScrollRunnable(int i, int i2, long j) {
            this.mScrollFromY = i;
            this.mScrollToY = i2;
            this.mDuration = j;
            this.mInterpolator = new DecelerateInterpolator();
        }

        @Override
        public void run() {
            if (this.mDuration <= 0) {
                PullExtendLayout.this.setScrollTo(0, this.mScrollToY);
                return;
            }
            if (this.mStartTime == -1) {
                this.mStartTime = System.currentTimeMillis();
            } else {
                float f = (float) (this.mScrollFromY - this.mScrollToY);
                this.mCurrentY = this.mScrollFromY - Math.round(mInterpolator.getInterpolation(((float) Math.max(Math.min(((System.currentTimeMillis()
                        - this.mStartTime) * 1000) / this.mDuration, 1000), 0)) / 1000.0f) * f);
                setScrollTo(0, this.mCurrentY);
                Log.d("setScrollTo", " " + this.mCurrentY);
                if (!(mHeaderLayout == null || mHeaderHeight == 0)) {
                    mHeaderLayout.onPull(Math.abs(this.mCurrentY));
                    if (this.mCurrentY == 0) {
                        mHeaderLayout.setState(IExtendLayout.State.RESET);
                    }
                    if (Math.abs(this.mCurrentY) ==headerListHeight) {
                        mHeaderLayout.setState(IExtendLayout.State.arrivedListHeight);
                    }
                }
                if (!(mFooterLayout == null || mFooterHeight == 0)) {
                    mFooterLayout.onPull(Math.abs(mCurrentY));
                    if (mCurrentY == 0) {
                        mFooterLayout.setState(IExtendLayout.State.RESET);
                    }
                    if (Math.abs(mCurrentY) == footerListHeight) {
                        mFooterLayout.setState(IExtendLayout.State.arrivedListHeight);
                    }
                }
            }
            if (mContinueRunning && mScrollToY !=mCurrentY) {
                postDelayed(this, 16);
            }
        }

        public void stop() {
            mContinueRunning = false;
            removeCallbacks(this);
        }
    }

    public PullExtendLayout(Context context) {
        this(context, null);
    }

    public PullExtendLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PullExtendLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.offsetRadio = 1.0f;
        this.mLastMotionY = -1.0f;
        this.mPullRefreshEnabled = true;
        this.mPullLoadEnabled = true;
        this.mInterceptEventEnable = true;
        this.mIsHandledTouchEvent = false;
        setOrientation(LinearLayout.VERTICAL);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount == 2) {
            if (getChildAt(0) instanceof ExtendLayout) {
                this.mHeaderLayout = (ExtendLayout) getChildAt(0);
                this.mRefreshableView = getChildAt(1);
            } else {
                this.mRefreshableView = getChildAt(0);
                this.mFooterLayout = (ExtendLayout) getChildAt(1);
            }
        } else if (childCount == 3) {
            if (getChildAt(0) instanceof ExtendLayout) {
                this.mHeaderLayout = (ExtendLayout) getChildAt(0);
            }
            this.mRefreshableView = getChildAt(1);
            this.mFooterLayout = (ExtendLayout) getChildAt(2);
        } else {
            throw new IllegalStateException("布局异常，最多三个，最少一个");
        }
        if (this.mRefreshableView == null) {
            throw new IllegalStateException("布局异常，一定要有内容布局");
        }
        init(getContext());
    }

    public void setOffsetRadio(float f) {
        this.offsetRadio = f;
    }

    private void init(Context context) {
        this.mTouchSlop = (int) (((double) ViewConfiguration.get(context).getScaledTouchSlop()) * 1.5d);
        ViewGroup.LayoutParams layoutParams =mRefreshableView.getLayoutParams();
        layoutParams.height = 10;
        mRefreshableView.setLayoutParams(layoutParams);
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                PullExtendLayout.this.refreshLoadingViewsSize();
                PullExtendLayout.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void refreshLoadingViewsSize() {
        int listSize;
        int listSize2;
        int i = 0;
        int contentSize = this.mHeaderLayout != null ? this.mHeaderLayout.getContentSize() : 0;
        if (this.mHeaderLayout != null) {
            listSize = this.mHeaderLayout.getListSize();
        } else {
            listSize = 0;
        }
        this.headerListHeight = listSize;
        if (this.mFooterLayout != null) {
            listSize = this.mFooterLayout.getContentSize();
        } else {
            listSize = 0;
        }
        if (this.mFooterLayout != null) {
            listSize2 = this.mFooterLayout.getListSize();
        } else {
            listSize2 = 0;
        }
        this.footerListHeight = listSize2;
        if (contentSize < 0) {
            contentSize = 0;
        }
        if (listSize < 0) {
            listSize = 0;
        }
        this.mHeaderHeight = contentSize;
        this.mFooterHeight = listSize;
        contentSize = this.mHeaderLayout != null ? this.mHeaderLayout.getMeasuredHeight() : 0;
        if (this.mFooterLayout != null) {
            i = this.mFooterLayout.getMeasuredHeight();
        }
        setPadding(getPaddingLeft(), -contentSize, getPaddingRight(), -i);
    }

    @Override
    public final void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        refreshLoadingViewsSize();
        refreshRefreshableViewSize(i, i2);
        post(new Runnable() {
            @Override
            public void run() {
                PullExtendLayout.this.requestLayout();
            }
        });
    }

    public void refreshRefreshableViewSize(int i, int i2) {
        LayoutParams layoutParams = (LayoutParams) this.mRefreshableView.getLayoutParams();
        if (layoutParams.height != i2) {
            layoutParams.height = i2;
            this.mRefreshableView.requestLayout();
        }
    }

    @Override
    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (!isInterceptTouchEventEnabled()) {
            return false;
        }
        if (!isPullLoadEnabled() && !isPullRefreshEnabled()) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            this.mIsHandledTouchEvent = false;
            return false;
        } else if (action != MotionEvent.ACTION_DOWN && this.mIsHandledTouchEvent) {
            return true;
        } else {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    this.mLastMotionY = motionEvent.getY();
                    this.mIsHandledTouchEvent = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float y = motionEvent.getY() - this.mLastMotionY;
                    if (Math.abs(y) > ((float) this.mTouchSlop)) {
                        this.mLastMotionY = motionEvent.getY();
                        if (isPullRefreshEnabled() || isPullLoadEnabled()) {
                            if (Math.abs(getScrollYValue()) > 0 || y > 0.5f || y < -0.5f) {
                                z = true;
                            }
                            this.mIsHandledTouchEvent = z;
                            if (this.mIsHandledTouchEvent) {
                                this.mRefreshableView.onTouchEvent(motionEvent);
                                break;
                            }
                        }
                    }
                    break;
            }
            return this.mIsHandledTouchEvent;
        }
    }

    @Override
    public final boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.mLastMotionY = motionEvent.getY();
                this.mIsHandledTouchEvent = false;
                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!this.mIsHandledTouchEvent) {
                    return false;
                }
                this.mIsHandledTouchEvent = false;
                if (isReadyForPullDown(0.0f)) {
                    resetHeaderLayout();
                    return false;
                } else if (!isReadyForPullUp(0.0f)) {
                    return false;
                } else {
                    resetFooterLayout();
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                float y = motionEvent.getY() - this.mLastMotionY;
                this.mLastMotionY = motionEvent.getY();
                if (isPullRefreshEnabled() && isReadyForPullDown(y)) {
                    pullHeaderLayout(y / this.offsetRadio);
                    if (!(this.mFooterLayout == null || this.mFooterHeight == 0)) {
                        this.mFooterLayout.setState(IExtendLayout.State.RESET);
                        return true;
                    }
                } else if (isPullLoadEnabled() && isReadyForPullUp(y)) {
                    pullFooterLayout(y / this.offsetRadio);
                    if (!(this.mHeaderLayout == null || this.mHeaderHeight == 0)) {
                        this.mHeaderLayout.setState(IExtendLayout.State.RESET);
                        return true;
                    }
                } else {
                  //  this.mIsHandledTouchEvent = false;
                    return false;
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void setPullRefreshEnabled(boolean z) {
        this.mPullRefreshEnabled = z;
    }

    @Override
    public void setPullLoadEnabled(boolean z) {
        this.mPullLoadEnabled = z;
    }

    @Override
    public boolean isPullRefreshEnabled() {
        return this.mPullRefreshEnabled && this.mHeaderLayout != null;
    }

    @Override
    public boolean isPullLoadEnabled() {
        return this.mPullLoadEnabled && this.mFooterLayout != null;
    }

    @Override
    public ExtendLayout getHeaderExtendLayout() {
        return this.mHeaderLayout;
    }

    @Override
    public ExtendLayout getFooterExtendLayout() {
        return this.mFooterLayout;
    }

    public void doPullRefreshing(final boolean z, long j) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                PullExtendLayout.this.smoothScrollTo(-PullExtendLayout.this.mHeaderHeight, (long) (z ? SCROLL_DURATION : 0), 0);
            }
        }, j);
    }

    public boolean isReadyForPullDown(float f) {
        return getScrollYValue() < 0 || (getScrollYValue() == 0 && f > 0.0f);
    }

    public boolean isReadyForPullUp(float f) {
        return getScrollYValue() > 0 || (getScrollYValue() == 0 && f < 0.0f);
    }

    public long getSmoothScrollDuration() {
        return SCROLL_DURATION;
    }

    public void pullHeaderLayout(float f) {
        f=f*SCROLL_DAMP;
        int scrollYValue = getScrollYValue();
        if (f >= 0.0f || ((float) scrollYValue) - f < 0.0f) {
            setScrollBy(0, -((int) f));
            scrollYValue = Math.abs(getScrollYValue());
            if (this.mHeaderLayout != null && this.mHeaderHeight != 0) {
                if (scrollYValue >= this.headerListHeight) {
                    this.mHeaderLayout.setState(IExtendLayout.State.arrivedListHeight);
                    setOffsetRadio(2.0f);
                } else {
                    setOffsetRadio(1.0f);
                }
                this.mHeaderLayout.onPull(scrollYValue);
                return;
            }
            return;
        }
        setScrollTo(0, 0);
        if (this.mHeaderLayout != null && this.mHeaderHeight != 0) {
            this.mHeaderLayout.setState(IExtendLayout.State.RESET);
            this.mHeaderLayout.onPull(0);
        }
    }

    public void pullFooterLayout(float f) {
        int scrollYValue = getScrollYValue();
        if (f <= 0.0f || ((float) scrollYValue) - f > 0.0f) {
            setScrollBy(0, -((int) f));
            scrollYValue = Math.abs(getScrollYValue());
            if (this.mFooterLayout != null && this.mFooterHeight != 0) {
                if (scrollYValue >= this.footerListHeight) {
                    this.mFooterLayout.setState(IExtendLayout.State.arrivedListHeight);
                    setOffsetRadio(3.0f);
                } else {
                    setOffsetRadio(1.0f);
                }
                this.mFooterLayout.onPull(Math.abs(getScrollYValue()));
                return;
            }
            return;
        }
        setScrollTo(0, 0);
        if (this.mFooterLayout != null && this.mFooterHeight != 0) {
            this.mFooterLayout.setState(IExtendLayout.State.RESET);
            this.mFooterLayout.onPull(0);
        }
    }

    public void resetHeaderLayout() {
        int abs = Math.abs(getScrollYValue());
        if (abs < this.mHeaderHeight) {
            smoothScrollTo(0);
        } else if (abs >= this.mHeaderHeight) {
            smoothScrollTo(-this.headerListHeight);
        }
    }

    public void resetFooterLayout() {
        int abs = Math.abs(getScrollYValue());
        if (abs < this.mFooterHeight) {
            smoothScrollTo(0);
        } else if (abs >= this.mFooterHeight) {
            smoothScrollTo(this.footerListHeight);
        }
    }

    public void closeExtendHeadAndFooter() {
        smoothScrollTo(0);
    }

    private void setScrollTo(int i, int i2) {
        scrollTo(i, i2);
    }

    private void setScrollBy(int i, int i2) {
        scrollBy(i, i2);
    }

    private int getScrollYValue() {
        return getScrollY();
    }

    public void smoothScrollTo(int i) {
        smoothScrollTo(i, getSmoothScrollDuration(), 0);
    }

    private void smoothScrollTo(int i, long j, long j2) {
        if (this.mSmoothScrollRunnable != null) {
            this.mSmoothScrollRunnable.stop();
        }
        int scrollYValue = getScrollYValue();
        Object obj = scrollYValue != i ? 1 : null;
        if (obj != null) {
            this.mSmoothScrollRunnable = new SmoothScrollRunnable(scrollYValue, i, j);
        }
        if (obj == null) {
            return;
        }
        if (j2 > 0) {
            postDelayed(this.mSmoothScrollRunnable, j2);
        } else {
            post(this.mSmoothScrollRunnable);
        }
    }

    private void setInterceptTouchEventEnabled(boolean z) {
        this.mInterceptEventEnable = z;
    }

    private boolean isInterceptTouchEventEnabled() {
        return this.mInterceptEventEnable;
    }
}
