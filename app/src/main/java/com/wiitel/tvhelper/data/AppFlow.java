package com.wiitel.tvhelper.data;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

/**
 * Created by zhuchuntao on 16-12-8.
 */
@Entity(
        indexes = {
                @Index(value = "appid,currentMonth")
        }
)
public class AppFlow {

    @NotNull
    private String currentMonth;

    @NotNull
    private int appid;

    @Transient
    private long rx;
    @Transient
    private long tx;

    private long rxHistory;
    private long txHistory;

    public long getTx() {
        return this.tx;
    }

    public void setTx(long tx) {
        this.tx = tx;
    }

    public long getRx() {
        return this.rx;
    }

    public void setRx(long rx) {
        this.rx = rx;
    }

    public int getAppid() {
        return this.appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }

    public String getCurrentMonth() {
        return this.currentMonth;
    }

    public void setCurrentMonth(String currentMonth) {
        this.currentMonth = currentMonth;
    }

    public long getTxHistory() {
        return this.txHistory;
    }

    public void setTxHistory(long txHistory) {
        this.txHistory = txHistory;
    }

    public long getRxHistory() {
        return this.rxHistory;
    }

    public void setRxHistory(long rxHistory) {
        this.rxHistory = rxHistory;
    }

    @Generated(hash = 1028680205)
    public AppFlow(@NotNull String currentMonth, int appid, long rxHistory,
            long txHistory) {
        this.currentMonth = currentMonth;
        this.appid = appid;
        this.rxHistory = rxHistory;
        this.txHistory = txHistory;
    }

    @Generated(hash = 456074406)
    public AppFlow() {
    }

    public long getRxAll() {
        return this.rxHistory + this.rx;
    }

    public long getTxAll() {
        return this.txHistory + this.tx;
    }
}
