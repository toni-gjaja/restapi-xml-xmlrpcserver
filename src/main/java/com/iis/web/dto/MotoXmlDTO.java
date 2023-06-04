package com.iis.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Moto")
@NoArgsConstructor
@AllArgsConstructor
public class MotoXmlDTO {

    @XmlElement
    String make;
    @XmlElement
    String model;
    @XmlElement
    String year;
    @XmlElement
    String power;
    @XmlElement
    String engine;
    @XmlElement
    String torque;

}
