package com.gprs.srp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class report extends AppCompatActivity {

    private SQLiteHelper helper;
    SQLiteDatabase db;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    List<BarEntry> yVals1 ;
    List<BarEntry> yVals2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        yVals1 = new ArrayList<BarEntry>();
        yVals2 = new ArrayList<BarEntry>();
        pref = getSharedPreferences("user", 0); // 0 - for private mode
        editor=pref.edit();

        TextView rank=findViewById(R.id.rank);
        rank.setText("Rank : "+pref.getInt("status",0));
        helper=new SQLiteHelper(this);
        db=helper.getReadableDatabase();
        getData();
        drawChart();
    }

    private void drawChart() {
        BarChart barChart = findViewById(R.id.barChart);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        XAxis xl = barChart.getXAxis();
        xl.setGranularity(1f);
        xl.setCenterAxisLabels(true);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(30f);
        barChart.getAxisRight().setEnabled(false);

        //data
        float groupSpace = 0.04f;
        float barSpace = 0.02f;
        float barWidth = 0.46f;






        BarDataSet set1, set2;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) barChart.getData().getDataSetByIndex(1);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "Positive");
            set1.setColor(Color.rgb(104, 241, 175));
            set2 = new BarDataSet(yVals2, "Negative");
            set2.setColor(Color.rgb(164, 228, 251));


            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            dataSets.add(set2);

            BarData data = new BarData(dataSets);
            barChart.setData(data);
        }

        barChart.getBarData().setBarWidth(barWidth);
        barChart.groupBars(0, groupSpace, barSpace);
        barChart.invalidate();

    }

    void getData(){
        Cursor c=helper.speech_reportChart(db);
        Cursor c1=helper.speech_reportChart1(db);

        c1.moveToNext();
        TextView t1=findViewById(R.id.totalLine);
        t1.setText("Total Lines : "+c1.getString(0));
        TextView t2=findViewById(R.id.totalPos);
        t2.setText("Positivity : "+c1.getString(1));
        TextView t3=findViewById(R.id.totalNeg);
        t3.setText("Negativity` : "+c1.getString(2));

        String items = "";

        int i=1;
        while (c.moveToNext()) {
            yVals1.add(new BarEntry(i, c.getFloat(0)));
            yVals2.add(new BarEntry(i, c.getFloat(1)));
            i+=1;
        }

    }
}