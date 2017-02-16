package com.journeyapps.bluetoothscale;

import android.os.Parcel;
import android.os.Parcelable;

public class ScaleReadingParcel implements Parcelable, ScaleReading {

    final private double weight;
    final private boolean fault;
    final private boolean overweight;
    final private Unit unit;
    final private boolean stable;
    final private boolean zero;

    public ScaleReadingParcel(ScaleReading scaleReading) {
        this.weight = scaleReading.getWeight();
        this.fault = scaleReading.isFault();
        this.overweight = scaleReading.isOverweight();
        this.unit = scaleReading.getUnit();
        this.stable = scaleReading.isStable();
        this.zero = scaleReading.isZero();
    }

    private ScaleReadingParcel(Parcel source) {
        /**
         * NB: the order in which values is read must match the order in which they are written exactly:
         * `writeToParcel(Parcel dest, int flags)`
         */

        this.weight = source.readDouble();
        this.unit = ScaleReading.Unit.valueOf(source.readInt());

        boolean[] booleanValues = new boolean[4];
        source.readBooleanArray(booleanValues);
        this.fault = booleanValues[0];
        this.overweight = booleanValues[1];
        this.stable = booleanValues[2];
        this.zero = booleanValues[3];
    }

    public static final Creator<ScaleReadingParcel> CREATOR = new Creator<ScaleReadingParcel>() {
        @Override
        public ScaleReadingParcel createFromParcel(Parcel source) {
            return new ScaleReadingParcel(source);
        }

        @Override
        public ScaleReadingParcel[] newArray(int size) {
            return new ScaleReadingParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        /**
         * NB: the order in which values are written must match the order in which they are read exactly:
         * `ScaleReadingParcel(Parcel source)`
         */

        dest.writeDouble(this.weight);
        dest.writeInt(this.unit.getValue());

        dest.writeBooleanArray(new boolean[] { this.fault, this.overweight, this.stable, this.zero });
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
