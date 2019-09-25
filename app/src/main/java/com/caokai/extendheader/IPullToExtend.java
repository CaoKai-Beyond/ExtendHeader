package com.caokai.extendheader;

/**
 * @author caokai
 * @time 2019/9/6
 */

public interface IPullToExtend {
    ExtendLayout getFooterExtendLayout();

    ExtendLayout getHeaderExtendLayout();

    boolean isPullLoadEnabled();

    boolean isPullRefreshEnabled();

    void setPullLoadEnabled(boolean z);

    void setPullRefreshEnabled(boolean z);
}
