package com.example.localize;

/**
 * Created by Chiran on 11/4/15.
 */
public class WIFI {
    private long id;
    private String coods;
    private String values;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCoods() {
        return coods;
    }

    public void setCoods(String coods) {
        this.coods = coods;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return coods + values;
    }
}
