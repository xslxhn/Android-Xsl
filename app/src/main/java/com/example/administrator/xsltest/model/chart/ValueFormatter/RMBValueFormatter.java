package com.example.administrator.xsltest.model.chart.ValueFormatter;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.io.Serializable;
import java.text.DecimalFormat;

public class RMBValueFormatter implements ValueFormatter, Serializable {

    private DecimalFormat mFormat;

    public RMBValueFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0");
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if(value == 0) return "";
        return "￥ " + mFormat.format(value);
    }
}
