package com.example.controllers;

public class Time {
    String string;
    int  integer;
    public Time(String string,int integer){
this.string=string;
this.integer=integer;

    }
    public String toString(){
        return this.string;
    }
    public int getInteger() {
        return integer;
    }
}
