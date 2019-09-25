package com.caokai.extendheader;

/**
 * @author caokai
 * @time 2019/9/6
 */
public interface IExtendLayout {

    public enum State {
        NONE,
        RESET,
        beyondListHeight,
        startShowList,
        arrivedListHeight
    }

    int getContentSize();

    State getState();

    void onPull(int i);

    void setState(State state);
}
