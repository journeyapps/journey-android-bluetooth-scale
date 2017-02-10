package com.journeyapps.bluetoothscale;

public class HiweighScaleReading implements ScaleReading {
    final private double weight;
    final private boolean fault;
    final private boolean overweight;
    final private Unit unit;
    final private boolean stable;
    final private boolean zero;

    public HiweighScaleReading(double weight, boolean fault, boolean overweight, Unit unit, boolean stable, boolean zero) {
        this.weight = weight;
        this.fault = fault;
        this.overweight = overweight;
        this.unit = unit;
        this.stable = stable;
        this.zero = zero;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public boolean isFault() {
        return fault;
    }

    @Override
    public boolean isOverweight() {
        return overweight;
    }

    @Override
    public Unit getUnit() {
        return unit;
    }

    @Override
    public boolean isStable() {
        return stable;
    }

    @Override
    public boolean isZero() {
        return zero;
    }
}
