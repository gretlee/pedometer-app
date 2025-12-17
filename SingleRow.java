package com.demo.example.DB;


public class SingleRow {
    private float caloriesBurn;
    private int daysOfMonth;
    private int daysOfWeek;
    private int daysOfYear;
    private float distence;
    private int hours;
    private long id;
    private int mints;
    private int month;
    private int steps;
    private int stepsForWeekView;
    private int week;
    private int year;

    public SingleRow(long j, int i, int i2, float f, float f2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
        this.id = j;
        this.steps = i;
        this.mints = i2;
        this.caloriesBurn = f;
        this.distence = f2;
        this.hours = i3;
        this.week = i5;
        this.month = i4;
        this.daysOfWeek = i6;
        this.year = i9;
        this.daysOfMonth = i7;
        this.stepsForWeekView = i10;
        this.daysOfYear = i8;
    }

    public int getDaysOfYear() {
        return this.daysOfYear;
    }

    public void setDaysOfYear(int i) {
        this.daysOfYear = i;
    }

    public int getDaysOfMonth() {
        return this.daysOfMonth;
    }

    public void setDaysOfMonth(int i) {
        this.daysOfMonth = i;
    }

    public long getId() {
        return this.id;
    }

    public int getWeek() {
        return this.week;
    }

    public void setWeek(int i) {
        this.week = i;
    }

    public void setId(long j) {
        this.id = j;
    }

    public int getSteps() {
        return this.steps;
    }

    public void setSteps(int i) {
        this.steps = i;
    }

    public int getMints() {
        return this.mints;
    }

    public void setMints(int i) {
        this.mints = i;
    }

    public float getCaloriesBurn() {
        return this.caloriesBurn;
    }

    public void setCaloriesBurn(float f) {
        this.caloriesBurn = f;
    }

    public float getDistence() {
        return this.distence;
    }

    public void setDistence(float f) {
        this.distence = f;
    }

    public int getHours() {
        return this.hours;
    }

    public void setHours(int i) {
        this.hours = i;
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int i) {
        this.month = i;
    }

    public int getDaysOfWeek() {
        return this.daysOfWeek;
    }

    public void setDaysOfWeek(int i) {
        this.daysOfWeek = i;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int i) {
        this.year = i;
    }

    public int getStepsForWeekView() {
        return this.stepsForWeekView;
    }

    public void setStepsForWeekView(int i) {
        this.stepsForWeekView = i;
    }
}
