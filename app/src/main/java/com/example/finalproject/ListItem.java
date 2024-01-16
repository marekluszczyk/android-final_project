package com.example.finalproject;
import java.io.Serializable;

public class ListItem implements Serializable {
    public String name;
    public int sum;

    public ListItem() {
    }

    public ListItem(String name, int sum) {
        this.name = name;
        this.sum = sum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getName() {
        return name;
    }

    public int getSum() {
        return sum;
    }
}
