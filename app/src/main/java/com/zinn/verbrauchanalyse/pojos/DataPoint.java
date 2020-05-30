package com.zinn.verbrauchanalyse.pojos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataPoint implements Comparable<DataPoint> {

    private int index;

    private Date date;

    private Double electricityMeter;

    private Double gasMeter;

    private Double waterMeter;

    private Double powerConsumption;

    private Double gasConsumption;

    private Double waterConsumption;

    public DataPoint() {
    }

    public DataPoint(int index,
                     Date date,
                     Double electricityMeter,
                     Double gasMeter,
                     Double waterMeter,
                     Double powerConsumption,
                     Double gasConsumption,
                     Double waterConsumption) {
        this.index = index;
        this.date = date;
        this.electricityMeter = electricityMeter;
        this.gasMeter = gasMeter;
        this.waterMeter = waterMeter;
        this.powerConsumption = powerConsumption;
        this.gasConsumption = gasConsumption;
        this.waterConsumption = waterConsumption;
    }

    public int getIndex() {
        return index;
    }

    public String getIndexText() {
        return "" + index;
    }

    public DataPoint setIndex(int index) {
        this.index = index;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public DataPoint setDate(Date date) {
        this.date = date;
        return this;
    }

    public DataPoint setDateWithFormat(String date) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        this.date = sdf.parse(date);
        return this;
    }

    public Double getElectricityMeter() {
        return electricityMeter;
    }

    public DataPoint setElectricityMeter(Double electricityMeter) {
        this.electricityMeter = electricityMeter;
        return this;
    }

    public Double getGasMeter() {
        return gasMeter;
    }

    public DataPoint setGasMeter(Double gasMeter) {
        this.gasMeter = gasMeter;
        return this;
    }

    public Double getWaterMeter() {
        return waterMeter;
    }

    public DataPoint setWaterMeter(Double waterMeter) {
        this.waterMeter = waterMeter;
        return this;
    }

    public Double getPowerConsumption() {
        return powerConsumption;
    }

    public DataPoint setPowerConsumption(Double powerConsumption) {
        this.powerConsumption = powerConsumption;
        return this;
    }

    public Double getGasConsumption() {
        return gasConsumption;
    }

    public DataPoint setGasConsumption(Double gasConsumption) {
        this.gasConsumption = gasConsumption;
        return this;
    }

    public Double getWaterConsumption() {
        return waterConsumption;
    }

    public DataPoint setWaterConsumption(Double waterConsumption) {
        this.waterConsumption = waterConsumption;
        return this;
    }

    @Override
    public int compareTo(DataPoint dataPoint) {
        return getDate().compareTo(dataPoint.getDate());
    }
}
