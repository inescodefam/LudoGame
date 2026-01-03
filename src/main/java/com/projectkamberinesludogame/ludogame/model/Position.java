package com.projectkamberinesludogame.ludogame.model;


import java.io.Serializable;

public class Position implements Serializable {
    private Integer position;

    @Override
    public String toString() {
        return "Position {" +
                "position = " + position +
                '}';
    }

    public Position() {}

    public int getPosition(Integer position) {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Position(int position) {
        this.position = position;
    }
}
