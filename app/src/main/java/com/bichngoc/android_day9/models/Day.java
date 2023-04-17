package com.bichngoc.android_day9.models;

import java.util.ArrayList;

public class Day {
    private ArrayList<DataHours> dataHours;
    private String date;
    private boolean isExpand;

    public Day() {
    }

    public Day(ArrayList<DataHours> dataHours, String date) {
        this.dataHours = dataHours;
        this.date = date;
    }

    public Day(ArrayList<DataHours> dataHours, String date, boolean isExpand) {
        this.dataHours = dataHours;
        this.date = date;
        this.isExpand = isExpand;
    }

    public ArrayList<DataHours> getDataHours() {
        return dataHours;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public void setDataHours(ArrayList<DataHours> dataHours) {
        this.dataHours = dataHours;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
