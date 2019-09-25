package com.caokai.extendheader;

/**
 * @author caokai
 * @time 2019/9/6
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;


public abstract class ExtendLayout extends FrameLayout implements IExtendLayout {
    private State mCurState;
    private State mPreState;

    public abstract View createLoadingView(Context context, AttributeSet attributeSet);

    @Override
    public abstract int getContentSize();

    public abstract int getListSize();

    public ExtendLayout(Context context) {
        this(context, null);
    }

    public ExtendLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ExtendLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCurState = State.NONE;
        this.mPreState = State.NONE;
        init(context, attributeSet);
    }

    public void init(Context context, AttributeSet attributeSet) {
        View createLoadingView = createLoadingView(context, attributeSet);
        if (createLoadingView == null) {
            throw new NullPointerException("Loading view can not be null.");
        }
        addView(createLoadingView, new LayoutParams(-1, -1));
        bindView(createLoadingView);
    }

    public void bindView(View view) {
    }

    @Override
    public void setState(State state) {
        if (this.mCurState != state) {
            this.mPreState = this.mCurState;
            this.mCurState = state;
            onStateChanged(state, this.mPreState);
        }
    }

    @Override
    public State getState() {
        return this.mCurState;
    }

    @Override
    public void onPull(int i) {
    }

    public State getPreState() {
        return this.mPreState;
    }

    public void onStateChanged(State state, State state2) {
        switch (state) {
            case RESET:
                onReset();
                return;
            case beyondListHeight:
                onReleaseToRefresh();
                return;
            case startShowList:
                onRefreshing();
                return;
            case arrivedListHeight:
                onArrivedListHeight();
                return;
            default:
                return;
        }
    }

    public void onReset() {
    }

    public void onPullToRefresh() {
    }

    public void onReleaseToRefresh() {
    }

    public void onRefreshing() {
    }

    public void onArrivedListHeight() {
    }
}
