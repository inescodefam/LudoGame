package com.projectkamberinesludogame.ludogame.model;

import java.io.Serializable;

public class Piece implements  Serializable {
    private static final long serialVersionUID = 1L;

    public int id;
    public int color;
    public int position;

    public Piece() {}

    Piece(int id, int color, int position) {
        this.id = id;
        this.color = color;
        this.position = position;
    }
}
