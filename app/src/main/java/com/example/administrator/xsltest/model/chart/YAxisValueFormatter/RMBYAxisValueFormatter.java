package com.example.administrator.xsltest.model.chart.YAxisValueFormatter;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.io.Serializable;
import java.text.DecimalFormat;

public class RMBYAxisValueFormatter implements YAxisValueFormatter, Serializable {

    private DecimalFormat mFormat;

    public RMBYAxisValueFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0");
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return "￥ " + mFormat.format(value);
    }
}
