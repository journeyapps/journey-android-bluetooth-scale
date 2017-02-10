package com.journeyapps.bluetoothscale;

public interface ScaleReading {
    double getWeight();

    boolean isFault();

    boolean isOverweight();

    Unit getUnit();

    boolean isStable();

    boolean isZero();

    enum Unit {
        KG, LB;

        @Override
        public String toString() {
            switch(this) {
                case KG:
                    return "kg";
                case LB:
                    return "lb";
            }
            throw new IllegalArgumentException();
        }
    }
}
