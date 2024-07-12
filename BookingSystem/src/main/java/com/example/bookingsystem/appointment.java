package com.example.bookingsystem;

import javafx.beans.property.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;


public class appointment implements Serializable {
    private transient SimpleStringProperty time;
    private transient SimpleStringProperty monday;
    private transient SimpleStringProperty tuesday;
    private transient SimpleStringProperty wednesday;
    private transient SimpleStringProperty thursday;
    private transient SimpleStringProperty friday;
    private transient SimpleStringProperty saturday;
    private transient SimpleStringProperty sunday;
    private int weekID;

    public appointment(String time, String monday, String tuesday, String wednesday, String thursday, String friday, String saturday, String sunday) {
        this.time = new SimpleStringProperty(time);
        this.monday = new SimpleStringProperty(monday);
        this.tuesday = new SimpleStringProperty(tuesday);
        this.wednesday = new SimpleStringProperty(wednesday);
        this.thursday = new SimpleStringProperty(thursday);
        this.friday = new SimpleStringProperty(friday);
        this.saturday = new SimpleStringProperty(saturday);
        this.sunday = new SimpleStringProperty(sunday);
        this.weekID = weekID;
    }

    public int getWeekID() {
        return weekID;
    }

    public String getTime() {
        return time.get();
    }

    public String getMonday() {
        return monday.get();
    }

    public String getTuesday() {
        return tuesday.get();
    }

    public String getWednesday() {
        return wednesday.get();
    }

    public String getThursday() {
        return thursday.get();
    }

    public String getFriday() {
        return friday.get();
    }

    public String getSaturday() {
        return saturday.get();
    }

    public String getSunday() {
        return sunday.get();
    }

    public void setMonday(String monday) {
        this.monday.set(monday);
    }

    public void setTuesday(String tuesday) {
        this.tuesday.set(tuesday);
    }

    public void setWednesday(String wednesday) {
        this.wednesday.set(wednesday);
    }

    public void setThursday(String thursday) {
        this.thursday.set(thursday);
    }

    public void setFriday(String friday) {
        this.friday.set(friday);
    }

    public void setSaturday(String saturday) {
        this.saturday.set(saturday);
    }

    public void setSunday(String sunday) {
        this.sunday.set(sunday);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(time.get());
        out.writeUTF(monday.get());
        out.writeUTF(tuesday.get());
        out.writeUTF(wednesday.get());
        out.writeUTF(thursday.get());
        out.writeUTF(friday.get());
        out.writeUTF(saturday.get());
        out.writeUTF(sunday.get());
        out.writeInt(weekID);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.time = new SimpleStringProperty(in.readUTF());
        this.monday = new SimpleStringProperty(in.readUTF());
        this.tuesday = new SimpleStringProperty(in.readUTF());
        this.wednesday = new SimpleStringProperty(in.readUTF());
        this.thursday = new SimpleStringProperty(in.readUTF());
        this.friday = new SimpleStringProperty(in.readUTF());
        this.saturday = new SimpleStringProperty(in.readUTF());
        this.sunday = new SimpleStringProperty(in.readUTF());
        this.weekID = in.readInt();
    }

    public boolean isEmpty() {
        return monday.get().isEmpty() && tuesday.get().isEmpty() && wednesday.get().isEmpty() && thursday.get().isEmpty() && friday.get().isEmpty() && saturday.get().isEmpty() && sunday.get().isEmpty();
    }


}

//    public SimpleStringProperty timeProperty() {
//        return time;
//    }
//    public SimpleStringProperty mondayProperty() {return monday; }
//    public SimpleStringProperty tuesdayProperty() {
//        return tuesday;
//    }
//    public SimpleStringProperty wednesdayProperty() {
//        return wednesday;
//    }
//    public SimpleStringProperty thursdayProperty() {
//        return thursday;
//    }
//    public SimpleStringProperty fridayProperty() {
//        return friday;
//    }
//    public SimpleStringProperty saturdayProperty() {
//        return saturday;
//    }
//    public SimpleStringProperty sundayProperty() {
//        return sunday;
//    }



