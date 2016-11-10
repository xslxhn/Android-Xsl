package com.example.administrator.xsltest.model.chart;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Hong on 2015/10/22.
 */
public class ChartModel implements Serializable {

    private ArrayList<String> xVals;
    private float[] yVals;
    private float[][] yValss;
    private String label;
    private String[] stackLabels;
    private boolean isSingle = false;
    private YAxisValueFormatter yAxisValueFormatter;
    private ValueFormatter valueFormatter;
    private String description;

    public ValueFormatter getValueFormatter() {
        return valueFormatter;
    }

    public void setValueFormatter(ValueFormatter valueFormatter) {
        this.valueFormatter = valueFormatter;
    }

    public ArrayList<String> getxVals() {
        return xVals;
    }

    public void setxVals(ArrayList<String> xVals) {
        this.xVals = xVals;
    }

    public float[] getyVals() {
        return yVals;
    }

    public void setyVals(float[] yVals) {
        this.yVals = yVals;
    }

    public float[][] getyValss() {
        return yValss;
    }

    public void setyValss(float[][] yValss) {
        this.yValss = yValss;
    }

    public String[] getStackLabels() {
        return stackLabels;
    }

    public void setStackLabels(String[] stackLabels) {
        this.stackLabels = stackLabels;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setIsSingle(boolean isSingle) {
        this.isSingle = isSingle;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public YAxisValueFormatter getyAxisValueFormatter() {
        return yAxisValueFormatter;
    }

    public void setyAxisValueFormatter(YAxisValueFormatter yAxisValueFormatter) {
        this.yAxisValueFormatter = yAxisValueFormatter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
