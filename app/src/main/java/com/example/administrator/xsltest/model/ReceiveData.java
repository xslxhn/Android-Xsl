package com.example.administrator.xsltest.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by NH on 2016/3/28.
 */
@DatabaseTable(tableName = "tb_receive_data")
public class ReceiveData {

    /**
     * id
     */
    @DatabaseField(generatedId = true)
    private int id;


    @DatabaseField(columnName = "second")
    private double second;


    @DatabaseField(columnName = "pressure")
    private float pressure;

    public ReceiveData() {
    }

    public ReceiveData(int id) {
        this.id = id;
    }

    public ReceiveData(float pressure, double second) {
        this.pressure = pressure;
        this.second = second;
    }



    public double getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }
}
