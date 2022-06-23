package com.kodilla.TTT;


import javafx.scene.control.Button;

import java.io.Serializable;
import java.util.Arrays;

public class SaveGameData implements Serializable {

    private String[][] data = new String[3][3];

    public void setData(String[][] data) {
        this.data = data;
    }

    public String[][] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "SaveGameData{" +
                "data=" + "\n" +
                Arrays.toString(data[0]) + "\n" +
                Arrays.toString(data[1]) + "\n" +
                Arrays.toString(data[2]) + "\n" +

                '}';
    }

    public void addButton(Button button, int x, int y) {

        String text = (String) button.getUserData();
        System.out.println("x =" + x + "y=" + y + " ->  " + text);
        if (text != null) {
            data[x][y] = text;

        } else {
            data[x][y] = "*";
        }
    }
}
