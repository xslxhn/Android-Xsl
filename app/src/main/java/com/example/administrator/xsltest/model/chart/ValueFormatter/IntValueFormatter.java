package com.example.administrator.xsltest.model.chart.ValueFormatter;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.io.Serializable;
import java.text.DecimalFormat;


public class IntValueFormatter implements ValueFormatter, Serializable {

    private DecimalFormat mFormat;

    public IntValueFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0");
    }


    public String getFormattedValue(float value) {
        return mFormat.format(value);
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if (value == 0) {
            return "";
        }
        return mFormat.format(value);
    }
}