package com.rb.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author lrod
 */
public class Cycle {

    private long id;
    private Date init;
    private Date end;
    private BigDecimal initialBalance;
    private int status;

    public static final int CLOSED = 0;
    public static final int OPENED = 1;

    public Cycle() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getInit() {
        return init;
    }

    public void setInit(Date init) {
        this.init = init;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isOpened() {
        return status == OPENED;
    }

}
