package com.zinn.verbrauchanalyse.ui.home;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.zinn.verbrauchanalyse.R;
import com.zinn.verbrauchanalyse.databinding.FragmentHomeBinding;
import com.zinn.verbrauchanalyse.pojos.DataPoint;
import com.zinn.verbrauchanalyse.pojos.Element;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.zinn.verbrauchanalyse.ui.constants.Constants.DATA_POINTS;
import static java.util.Objects.nonNull;

public class HomeFragment extends Fragment {

    private final static String ROOT_DIRECTORY = "/storage/emulated/0/";
    private final static String BACKUP_FOLDER = "verbrauchsanalyse";
    private static String BACKUP_FILE;

    private FragmentHomeBinding binding;

    private DatePickerDialog datePicker;

    private Date date;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        setBindings();
        return binding.getRoot();
    }

    private void setBindings() {
        binding.calcElectric.setVisibility(View.GONE);
        binding.calcGas.setVisibility(View.GONE);
        binding.calcWater.setVisibility(View.GONE);
        datePicker = new DatePickerDialog(requireContext());
        binding.imageViewDate.setOnClickListener(v -> datePicker.show());
        binding.textViewDate.setOnClickListener(v -> datePicker.show());
        datePicker.getDatePicker().setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {
            binding.textViewDate.setText(dayOfMonth + "." + ++monthOfYear + "." + year);
            final Calendar calendar = new GregorianCalendar();
            calendar.set(year, monthOfYear - 1, dayOfMonth);
            date = calendar.getTime();
        });
        setTextWatcher();
        binding.showCharts.setOnClickListener(v -> NavHostFragment
                .findNavController(this).navigate(R.id.action_nav_home_to_graphFragment));
        binding.calcElectric.setOnClickListener(v -> saveValue(Element.ELECTRICITY,
                Double.parseDouble(binding.electric.getText().length() > 6 ?
                        binding.electric.getText().toString().substring(0, 6) :
                        binding.electric.getText().toString())));
        binding.calcGas.setOnClickListener(v -> saveValue(Element.GAS,
                Double.parseDouble(binding.gas.getText().length() > 6 ?
                        binding.gas.getText().toString().substring(0, 6) :
                        binding.gas.getText().toString())));
        binding.calcWater.setOnClickListener(v -> saveValue(Element.WATER,
                Double.parseDouble(binding.water.getText().length() > 6 ?
                        binding.water.getText().toString().substring(0, 6) :
                        binding.water.getText().toString())));
    }

    private void saveValue(final Element element, final double value) {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        boolean checkIfDateExists = validate(element, value, sdf);
        if (checkIfDateExists) return;
        switch (element) {
            case ELECTRICITY:
                DATA_POINTS.add(new DataPoint()
                        .setDate(date)
                        .setElectricityMeter(value)
                        .setGasMeter(0D)
                        .setWaterMeter(0D));
                break;

            case GAS:
                DATA_POINTS.add(new DataPoint()
                        .setDate(date)
                        .setElectricityMeter(0D)
                        .setGasMeter(value)
                        .setWaterMeter(0D));
                break;

            case WATER:
                DATA_POINTS.add(new DataPoint()
                        .setDate(date)
                        .setElectricityMeter(0D)
                        .setGasMeter(0D)
                        .setWaterMeter(value));
                break;

            default:
                //error
                break;
        }
        Collections.sort(DATA_POINTS);
        for (int index = 0; index < DATA_POINTS.size(); index++) {
            DATA_POINTS.get(index).setIndex(index);
        }
        recalculateValues();
        writeToFile();
    }

    private void writeToFile() {
        BACKUP_FILE = (new Date().toString() + "backup.csv").replace(" ", "");
        final String semicolon = ";";
        // Change directory here
        final File file = new File(this.getContext().getFilesDir(), BACKUP_FOLDER);
        if (!file.exists()) file.mkdirs();
        final File backupFile = new File(file, BACKUP_FILE);
        try (FileWriter writer = new FileWriter(backupFile)) {
            writer.append("Datum;Stromzähler;Gaszähler;Wasserzähler;Stromverbrauch;Gasverbrauch;Wasserverbrauch;" + "\n");
            for (DataPoint dataPoint : DATA_POINTS) {
                writer.append(dataPoint.getDate()
                        + semicolon
                        + dataPoint.getElectricityMeter()
                        + semicolon
                        + dataPoint.getGasMeter()
                        + semicolon
                        + dataPoint.getWaterMeter()
                        + semicolon
                        + dataPoint.getPowerConsumption()
                        + semicolon
                        + dataPoint.getGasConsumption()
                        + semicolon
                        + dataPoint.getWaterConsumption()
                        + "\n");
            }
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void recalculateValues() {
        for (int index = 0; index < DATA_POINTS.size(); index++) {
            if (index == 0) {
                DATA_POINTS.get(index).setPowerConsumption(0D)
                        .setGasConsumption(0D)
                        .setWaterConsumption(0D);
            } else {
                double elecConsumption = getConsumption(DATA_POINTS.get(index - 1).getElectricityMeter(),
                        DATA_POINTS.get(index).getElectricityMeter(),
                        DATA_POINTS.get(index - 1).getDate(),
                        DATA_POINTS.get(index).getDate());
                double gasConsumption = getConsumption(DATA_POINTS.get(index - 1).getGasMeter(),
                        DATA_POINTS.get(index).getGasMeter(),
                        DATA_POINTS.get(index - 1).getDate(),
                        DATA_POINTS.get(index).getDate());
                double waterConsumption = getConsumption(DATA_POINTS.get(index - 1).getWaterMeter(),
                        DATA_POINTS.get(index).getWaterMeter(),
                        DATA_POINTS.get(index - 1).getDate(),
                        DATA_POINTS.get(index).getDate());
                DATA_POINTS.get(index).setPowerConsumption(elecConsumption)
                        .setGasConsumption(gasConsumption)
                        .setWaterConsumption(waterConsumption);
            }
        }
    }

    private double getConsumption(final Double meter, final Double meterCurrent, final Date date, final Date dateCurrent) {

        double sub = meterCurrent - meter;
        long days = Duration.between(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(dateCurrent.toInstant(), ZoneId.systemDefault())).toDays();
        return sub / days;
    }

    private boolean validate(final Element element, final double value, final SimpleDateFormat sdf) {
        boolean dataPointExists = false;
        for (final DataPoint dP : DATA_POINTS) {
            if (sdf.format(dP.getDate()).equals(sdf.format(date))) {
                switch (element) {
                    case ELECTRICITY:
                        dP.setElectricityMeter(value);
                        writeToFile();
                        return true;

                    case GAS:
                        dP.setGasMeter(value);
                        writeToFile();
                        return true;

                    case WATER:
                        dP.setWaterMeter(value);
                        writeToFile();
                        return true;

                    default:
                        //error
                        return dataPointExists;
                }
            }
        }
        return dataPointExists;
    }

    private void setTextWatcher() {
        binding.electric.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (nonNull(binding.electric.getText().toString()) &&
                        !binding.electric.getText().toString().equals("")
                        && nonNull(date)) {
                    binding.calcElectric.setVisibility(View.VISIBLE);
                } else binding.calcElectric.setVisibility(View.GONE);
            }
        });

        binding.gas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (nonNull(binding.gas.getText().toString()) &&
                        !binding.gas.getText().toString().equals("")
                        && nonNull(date)) {
                    binding.calcGas.setVisibility(View.VISIBLE);
                } else binding.calcGas.setVisibility(View.GONE);
            }
        });

        binding.water.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (nonNull(binding.water.getText().toString()) &&
                        !binding.water.getText().toString().equals("")
                        && nonNull(date)) {
                    binding.calcWater.setVisibility(View.VISIBLE);
                } else binding.calcWater.setVisibility(View.GONE);
            }
        });
    }
}
