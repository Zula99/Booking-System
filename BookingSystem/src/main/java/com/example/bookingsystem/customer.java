package com.example.bookingsystem;

import java.io.Serializable;

public class customer implements Serializable{
    private String name;
    private String number;

    public customer() {
        this.name = "";
        this.number = "";
    }

    public customer(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String number() {
        return number;
    }

    public void number(String number) {
        this.number = number;
    }


    @Override
    public String toString() {
        return name;
    }

    public String getInfo(){
        return name + "\n" + number;
    }


}