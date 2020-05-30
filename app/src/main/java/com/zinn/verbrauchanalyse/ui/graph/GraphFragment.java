package com.zinn.verbrauchanalyse.ui.graph;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.zinn.verbrauchanalyse.databinding.FragmentGraphBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.zinn.verbrauchanalyse.ui.constants.Constants.DATA_POINTS;


public class GraphFragment extends Fragment {

    private FragmentGraphBinding binding;

    public GraphFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGraphBinding.inflate(inflater, container, false);
        fillTable();
        setBindings();
        binding.progressbar.setVisibility(View.GONE);
        binding.content.setVisibility(View.VISIBLE);
        return binding.getRoot();
    }

    private void setBindings() {
        binding.electricConsumption.setOnClickListener(v -> getValuesForChartElec());
        binding.gasConsumption.setOnClickListener(v -> getValuesForChartGas());
        binding.waterConsumption.setOnClickListener(v -> getValuesForChartWater());
    }

    private void getValuesForChartElec() {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < DATA_POINTS.size(); i++) {
            values.add(new Entry(Float.parseFloat(String.valueOf(i)), Float.parseFloat(DATA_POINTS.get(i).getPowerConsumption().toString())));
        }
        renderChart(values);
    }

    private void getValuesForChartGas() {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < DATA_POINTS.size(); i++) {
            values.add(new Entry(Float.parseFloat(String.valueOf(i)), Float.parseFloat(DATA_POINTS.get(i).getGasConsumption().toString())));
        }
        renderChart(values);
    }

    private void getValuesForChartWater() {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < DATA_POINTS.size(); i++) {
            values.add(new Entry(Float.parseFloat(String.valueOf(i)), Float.parseFloat(DATA_POINTS.get(i).getWaterConsumption().toString())));
        }
        renderChart(values);
    }

    private void renderChart(final ArrayList<Entry> values) {
        binding.lineChart.setTouchEnabled(true);
        binding.lineChart.setPinchZoom(true);
        final LineDataSet lineDataSet = new LineDataSet(values, "");
        final LineData lineData = new LineData(lineDataSet);
        binding.lineChart.setData(lineData);

        binding.lineChart.getLegend().setEnabled(false);
        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.animateXY(1000, 1000);

        binding.lineChart.invalidate();
    }

    private void fillTable() {
        fillFirstRow();
        fillBody();
    }

    private void fillFirstRow() {
        TableRow tbrow0 = new TableRow(requireContext());
        TextView tv = new TextView(requireContext());
        tv.setText(" Index ");
        tbrow0.addView(tv);
        TextView tv0 = new TextView(requireContext());
        tv0.setText(" Datum ");
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(requireContext());
        tv1.setText(" Stromzählerstand ");
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(requireContext());
        tv2.setText(" Gaszählerstand ");
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(requireContext());
        tv3.setText(" Wasserzählerstand ");
        tbrow0.addView(tv3);
        TextView tv4 = new TextView(requireContext());
        tv4.setText(" Stromverbrauch ");
        tbrow0.addView(tv4);
        TextView tv5 = new TextView(requireContext());
        tv5.setText(" Gasverbrauch ");
        tbrow0.addView(tv5);
        TextView tv6 = new TextView(requireContext());
        tv6.setText(" Wasserverbrauch ");
        tbrow0.addView(tv6);
        binding.table.addView(tbrow0);
    }

    private void fillBody() {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        DATA_POINTS.forEach(dataPoint -> {
            TableRow tbrow = new TableRow(requireContext());
            TextView t0v = new TextView(requireContext());
            t0v.setText(dataPoint.getIndexText());
            t0v.setGravity(Gravity.CENTER);
            tbrow.addView(t0v);
            TextView t1v = new TextView(requireContext());
            t1v.setText(sdf.format(dataPoint.getDate()));
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(requireContext());
            t2v.setText(dataPoint.getElectricityMeter().toString());
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);
            TextView t3v = new TextView(requireContext());
            t3v.setText(dataPoint.getGasMeter().toString());
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);
            TextView t4v = new TextView(requireContext());
            t4v.setText(dataPoint.getWaterMeter().toString());
            t4v.setGravity(Gravity.CENTER);
            tbrow.addView(t4v);
            TextView t5v = new TextView(requireContext());
            t5v.setText(dataPoint.getPowerConsumption().toString());
            t5v.setGravity(Gravity.CENTER);
            tbrow.addView(t5v);
            TextView t6v = new TextView(requireContext());
            t6v.setText(dataPoint.getWaterConsumption().toString());
            t6v.setGravity(Gravity.CENTER);
            tbrow.addView(t6v);
            TextView t7v = new TextView(requireContext());
            t7v.setText(dataPoint.getGasConsumption().toString());
            t7v.setGravity(Gravity.CENTER);
            tbrow.addView(t7v);
            binding.table.addView(tbrow);
        });
    }
}
