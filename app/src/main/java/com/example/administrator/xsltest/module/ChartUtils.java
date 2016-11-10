package com.example.administrator.xsltest.module;

import android.content.Context;
import android.graphics.Color;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import com.example.administrator.xsltest.model.ReceiveData;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Hong on 2015/10/29.
 */
public class ChartUtils {
    /*
    private class TYPE {
        private static final int DEF = 0x00;
        private static final int BAR = 0x01;
        private static final int MULTI_BAR = 0x02;
        private static final int STACKED_BAR = 0x03;
        private static final int PIE = 0x04;
    }
    */
    // 数据接收频率
    private static final int RECEIVE_DATA_F_HZ = 100;
    // X轴可见最大
    private static final int MAX_X_YAL = 600;
    // 屏幕滚动X轴最大显示时间
    private static final int MAX_X_SCALE_YAL = 10;
    /*
     // 描述
    private static String description;
     // 标注
    private static String label;
     // 标注
    private static String[] stackLabels;
     // Y轴单位
    private static YAxisValueFormatter yAxisValueFormatter;
     // x单位
    private static ValueFormatter valueFormatter;
     // Y轴值
    private static float[] yVals;
    private static float[][] yValss;
     // X轴值
    private static ArrayList<String> xVals;
    */
    /**
     * 获取控件---BarChart
     */
    /*
    @NonNull
    public static BarChart getBarChart(Context context, int chartType, ChartModel chartModel) {
        getValues(chartModel);
        BarChart barChart = new BarChart(context);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setDescription(description);
        barChart.setMaxVisibleValueCount(30);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(yAxisValueFormatter);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(35f);
        leftAxis.setLabelCount(7, true);
        float max = 0;
        if (chartType == TYPE.BAR) {
            max = yVals[0];
            for (int i = 1; i < yVals.length; i++) {
                if (yVals[i] > max) {
                    max = yVals[i];
                }
            }


        } else if (chartType == TYPE.MULTI_BAR) {
            max = yValss[0][0];
            for (int i = 0; i < yValss.length; i++) {
                for (int j = 0; j < stackLabels.length; j++) {
                    if (yValss[i][j] > max) {
                        max = yValss[i][j];
                    }
                }
            }
        } else if (chartType == TYPE.STACKED_BAR) {
            max = 0;
            for (int i = 0; i < yValss.length; i++) {
                int sum = 0;
                for (int j = 0; j < stackLabels.length; j++) {
                    sum += yValss[i][j];
                }
                if (sum > max) {
                    max = sum;
                }
            }
        }


        if (max < 5) {
            leftAxis.setAxisMaxValue(6);
        } else {
            int maxInt = (int) (max + max / 5);

            if (maxInt < 100) {
                while (maxInt % 6 != 0) {
                    maxInt++;
                }
            } else {
                int base = 1000000;
                while (maxInt / base == 0) {
                    base /= 10;
                }

//                maxInt = maxInt - maxInt % base;
                maxInt += base - maxInt % base;
//                while (maxInt % base)
//

                while (maxInt % (base / 10 * 6) == 0) {
                    maxInt += base / 10;
                }
            }

//            while (maxInt % 6 != 0) {
//                maxInt++;
//            }
            leftAxis.setAxisMaxValue(maxInt);
        }


        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        //y坐标值
        if (chartType == TYPE.BAR) {
            for (int i = 0; i < yVals.length; i++) {
                yVals1.add(new BarEntry(yVals[i], i));
//                leftAxis.setAxisMaxValue(150);
//                float mult = (100 + 1);
//                float val = (float) (Math.random() * mult);
//                yVals1.add(new BarEntry(val, i));
            }
        } else if (chartType == TYPE.STACKED_BAR) {

            for (int i = 0; i < yValss.length; i++) {
                yVals1.add(new BarEntry(yValss[i], i));
            }

        } else if (chartType == TYPE.MULTI_BAR) {
            for (int j = 0; j < stackLabels.length; j++) {
                ArrayList<BarEntry> tempBarEntryArray = new ArrayList();
                for (int i = 0; i < yValss.length; i++) {
                    tempBarEntryArray.add(new BarEntry(yValss[i][j], i));
                }
                BarDataSet tempBarDataSet = new BarDataSet(tempBarEntryArray, stackLabels[j]);
                tempBarDataSet.setColor(getColor(j));
                dataSets.add(tempBarDataSet);
            }
        }


        BarDataSet set1 = new BarDataSet(yVals1, label);
        set1.setColor(CUSTOMER_COLORS[0]);
        if (chartType == TYPE.STACKED_BAR) {
            set1.setColors(getColors());
        }
        set1.setStackLabels(stackLabels);
        dataSets.add(set1);


        BarData data = new BarData(xVals, dataSets);

//        if (xVals.size() > 20) {
//            MyMarkerView mv = new MyMarkerView(context, R.layout.custom_marker_view);
//            barChart.setMarkerView(mv);
//        }

        data.setValueFormatter(valueFormatter);

        Legend l = barChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        barChart.animateY(2000);
        barChart.setData(data);
        barChart.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        return barChart;
    }
    */

    /**
     * 获取控件---LineChart
     */
    public static LineChart getLineChart(Context context, List<ReceiveData> receiveData) {
        // 创建 LineChart
        LineChart lineChart = new LineChart(context);
        // 初始化
        lineChart = initLineChart(lineChart, receiveData);
        // 如果数据为空,则退出
        if (receiveData == null || receiveData.isEmpty()) {
            return lineChart;
        }
        //
        LineData data = lineChart.getData();
        if (data != null) {
            // 确保数据线设置
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = LineChartCfg();
                data.addDataSet(set);
            }
            // 加载显示数据
            int size = receiveData.size();
            data.addXValue(String.valueOf(size + 1));
            data.addEntry(new Entry(receiveData.get(size - 1).getPressure(), set.getEntryCount()), 0);
            // 刷新数据
            lineChart.notifyDataSetChanged();
            // 设置X轴最大范围
            lineChart.setVisibleXRangeMaximum(MAX_X_YAL);
            // 设置最大可视数值点数(超过这个数量不显示具体数值)
            lineChart.setMaxVisibleValueCount(MAX_X_SCALE_YAL);
        }
        return lineChart;
    }

    /**
     * 初始化控件---LineChart
     */
    private static LineChart initLineChart(LineChart lineChart, List<ReceiveData> receiveData) {
        // 设置背景色
        lineChart.setBackgroundColor(Color.rgb(128, 255, 128));
        // 设置图形区背景色
        lineChart.setDrawGridBackground(true);
        lineChart.setGridBackgroundColor(Color.rgb(255, 128, 128));
        // 设置图形区边框
        lineChart.setDrawBorders(true);
        lineChart.setBorderColor(Color.YELLOW);
        lineChart.setBorderWidth(4);
        // 设置触摸
        lineChart.setTouchEnabled(true);
        // 设置拖拽
        lineChart.setDragEnabled(true);
        // 设置缩放
        lineChart.setScaleEnabled(true);
        // 设置两指缩放(设为false x与y可以单独缩放)
        lineChart.setPinchZoom(false);
        // 设置抛掷使能
        lineChart.setDragDecelerationEnabled(true);
        // 设置抛掷减速摩擦系数(0-0.9999 0-立即停止 1-无效)
        lineChart.setDragDecelerationFrictionCoef(0.5f);
        // 设置右轴
        lineChart.getAxisRight().setEnabled(false);
        // 设置图标描述文字
        lineChart.setDescription("Description");
        lineChart.setDescriptionColor(Color.RED);
        // ----------
        // 获取X轴
        XAxis x = lineChart.getXAxis();
        // 设置X轴使能
        x.setEnabled(true);
        // 设置X轴位置
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 设置X轴颜色
        x.setAxisLineColor(Color.BLACK);
        // 设置X轴坐标文字格式
        x.setValueFormatter(new XAxisValueFormatter() {
            @Override
            public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
                String s;
                s = String.valueOf(((Double.valueOf(original)) / (double) RECEIVE_DATA_F_HZ));
                int indexPoint = s.indexOf(".");
                return s.substring(0, indexPoint + 2) + "s";
            }
        });
        // ----------
        // 获取Y轴
        YAxis y = lineChart.getAxisLeft();
        // 设置Y轴标签最大数量
        // y.setLabelCount(6, false);
        y.setLabelCount(6, true);
        // 设置Y轴位置
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        // 设置Y轴格线
        y.setDrawGridLines(false);
        // 设置Y轴颜色
        y.setAxisLineColor(Color.BLACK);
        // 设置Y轴坐标文字格式
        y.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                //DecimalFormat mFormat = new DecimalFormat("###,###,###,##0");
                //return mFormat.format(value) + "g";
                DecimalFormat mFormat = new DecimalFormat("###,###,###,##0");
                return mFormat.format(value);
            }
        });
        // ----------
        // 设置数据线
        LineData data = new LineData();
        // 设置数据线颜色
        data.setValueTextColor(Color.BLUE);
        lineChart.setData(data);
        // 设置获取图例
        lineChart.getLegend().setEnabled(false);
        // 设置无效
        lineChart.invalidate();
        // 设置layout参数
        lineChart.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        return lineChart;
    }

    /**
     * LineChart加点
     */
    public static void addEntry(LineChart lineChart, float value) {
        // 获取数据句柄
        LineData data = lineChart.getData();
        if (data != null) {
            // 确保数据配置
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = LineChartCfg();
                data.addDataSet(set);
            }
            // add a new x-value first
            int size = data.getXVals().size();
            String XValue = "1";
            if (size != 0) {
                XValue = String.valueOf(Double.valueOf(data.getXVals().get(size - 1)) + 1);
            }
            data.addXValue(XValue);
            data.addEntry(new Entry(value, set.getEntryCount()), 0);
            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(MAX_X_YAL);
            lineChart.moveViewToX(data.getXValCount() - MAX_X_YAL - 1);
        }
    }

    /**
     *
     */
    private static LineDataSet LineChartCfg() {
        LineDataSet set = new LineDataSet(null, "DataSet 1");
        // 拐角平滑处理
        set.setDrawCubic(true);
        // 设置平滑处理参数
        set.setCubicIntensity(0.2f);
        // 设置曲线填充
        set.setDrawFilled(true);
        // 线宽
        set.setLineWidth(1.8f);
        // 数据点圆圈使能/关闭
        set.setDrawCircles(false);
        // ---数据点圆圈半径
        set.setCircleRadius(4f);
        // ---数据点圆圈颜色
        set.setCircleColor(Color.WHITE);
        // 设置高亮颜色
        set.setHighLightColor(Color.rgb(244, 117, 117));
        // 设置颜色
        set.setColor(Color.BLUE);
        // 设置填充色
        set.setFillColor(Color.WHITE);
        // 设置Alpha
        set.setFillAlpha(100);
        // 水平高亮禁用
        set.setDrawHorizontalHighlightIndicator(false);
        // 设置填充格式位置
        set.setFillFormatter(new FillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return -10;
            }
        });
        return set;
    }
    /**
     *
     */
    /*
    private static void getValues(ChartModel chartModel) {
        description = chartModel.getDescription();
        yAxisValueFormatter = chartModel.getyAxisValueFormatter();
        valueFormatter = chartModel.getValueFormatter();
        xVals = chartModel.getxVals();
        yVals = chartModel.getyVals();
        yValss = chartModel.getyValss();
        label = chartModel.getLabel();
        stackLabels = chartModel.getStackLabels();
    }
    */
    /**
     *
     */
    /*
    private static int[] getColors() {

        int stacksize = yValss[0].length;

        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];

        for (int i = 0; i < stacksize; i++) {
            colors[i] = getColor(i);
        }

        return colors;
    }
    */
    /**
     *
     */
    /*
    private static int getColor(int i) {
        return CUSTOMER_COLORS[i];
    }
    */
    /**
     *
     */
    /*
    public static ArrayList<String> getXVals(int TYPE, int size) {
        ArrayList<String> strings = new ArrayList<>();
        String[] vals = null;
        switch (TYPE) {
            //最少10个
            case 0:
                if (size < 10) {
                    size = 10;
                }
                vals = new String[size];
                for (int i = 0; i < size; i++) {
                    vals[i] = String.valueOf(i);
                }
                break;
            case 1:
                vals = mWeeks;
                break;
            case 2:
                vals = mDays;
                break;
            case 3:
                Calendar calendar = Calendar.getInstance();
                int mouth = calendar.get(Calendar.MONTH);
                int season = mouth / 3;
                vals = new String[3];
                vals[0] = mMonths[season * 3];
                vals[1] = mMonths[season * 3 + 1];
                vals[2] = mMonths[season * 3 + 2];
                break;
            case 4:
                vals = mMonths;
                break;

        }

        if (vals != null) {
            strings.addAll(Arrays.asList(vals).subList(0, size));
        }
        return strings;
    }
    */
}
