package com.projectkamberinesludogame.ludogame.model;

public enum Theme {
    DEFAULT("Default",
            "linear-gradient(to bottom, #94BCEB, #EBA996)",
            "#FFFCD6",
            "#D9D5AB"),

    DARK("Dark Mode",
            "linear-gradient(to bottom, #596775, #27173b)",
            "#36265c",
            "#2C3E50"),

    NATURE("Nature",
            "linear-gradient(to bottom, #abe36b, #0a540d)",
            "#E8F5E9",
            "#C8E6C9"),

    SUNSET("Sunset",
            "linear-gradient(to bottom, #FF6B6B, #FFE66D)",
            "#FFF3E0",
            "#FFCC80"),

    OCEAN("Ocean",
            "linear-gradient(to bottom, #68ade8, #094070)",
            "#E1F5FE",
            "#B3E5FC"),

    PURPLE("Purple Dream",
            "linear-gradient(to bottom, #9C27B0, #E1BEE7)",
            "#F3E5F5",
            "#E1BEE7");

    private final String displayName;
    private final String backgroundGradient;
    private final String cellColor;
    private final String cellBorderColor;

    Theme(String displayName, String backgroundGradient, String cellColor, String cellBorderColor) {
        this.displayName = displayName;
        this.backgroundGradient = backgroundGradient;
        this.cellColor = cellColor;
        this.cellBorderColor = cellBorderColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBackgroundGradient() {
        return backgroundGradient;
    }

    public String getCellColor() {
        return cellColor;
    }

    public String getCellBorderColor() {
        return cellBorderColor;
    }
}