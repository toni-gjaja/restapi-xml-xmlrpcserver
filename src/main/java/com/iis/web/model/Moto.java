package com.iis.web.model;


public class Moto {

    String make;

    String model;

    String year;

    String power;

    String engine;

    String torque;

    public Moto(String make, String model, String year, String power, String engine, String torque) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.power = power;
        this.engine = engine;
        this.torque = torque;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getYear() {
        return year;
    }

    public String getPower() {
        return power;
    }

    public String getEngine() {
        return engine;
    }

    public String getTorque() {
        return torque;
    }
}
