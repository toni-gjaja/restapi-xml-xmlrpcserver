package com.iis.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class MotoDTO implements Serializable {

    @JsonProperty("make")
    String make;
    @JsonProperty("model")
    String model;
    @JsonProperty("year")
    String year;
    @JsonProperty("power")
    String power;
    @JsonProperty("engine")
    String engine;
    @JsonProperty("torque")
    String torque;

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
