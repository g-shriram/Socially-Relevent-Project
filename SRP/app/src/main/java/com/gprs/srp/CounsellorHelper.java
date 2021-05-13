package com.gprs.srp;


import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class CounsellorHelper {

    UserRegistrationHelper u;
    String message, time, counsellor, fixedtime;
    int rank;
    double pos,neg;
    String status;


    public CounsellorHelper(UserRegistrationHelper u, String message, String time, String status, float pos, float neg, int rank) {
        this.u = u;
        this.message = message;
        this.time = time;
        this.status = status;
        this.rank = rank;
        this.pos = pos;
        this.neg = neg;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public double getPos() {
        return pos;
    }

    public void setPos(double pos) {
        this.pos = pos;
    }

    public double getNeg() {
        return neg;
    }

    public void setNeg(double neg) {
        this.neg = neg;
    }

    public String getCounsellor() {
        return counsellor;
    }

    public void setCounsellor(String counsellor) {
        this.counsellor = counsellor;
    }

    public String getFixedtime() {
        return fixedtime;
    }

    public void setFixedtime(String fixedtime) {
        this.fixedtime = fixedtime;
    }

    public CounsellorHelper() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserRegistrationHelper getU() {
        return u;
    }

    public void setU(UserRegistrationHelper u) {
        this.u = u;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
