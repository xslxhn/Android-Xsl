package com.example.administrator.xsltest.model.chart.YAxisValueFormatter;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * 件ValueFormatter
 */
public class PieceYAxisValueFormatter implements YAxisValueFormatter, Serializable {

    private DecimalFormat mFormat;

    public PieceYAxisValueFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0");
        mFormat.setParseIntegerOnly(true);
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return mFormat.format(value) + " 单";
    }
}
