package com.github.onsdigital.babbage.highcharts;

/**
 * Created by bren on 23/06/15.
 */
public class Value {

    private String name;
    private Double y;

    public String getName() {
        return name;
    }

    public Value setName(String name) {
        this.name = name;
        return this;
    }

    public Double getY() {
        return y;
    }

    public Value setY(Double y) {
        this.y = y;
        return this;
    }
}
