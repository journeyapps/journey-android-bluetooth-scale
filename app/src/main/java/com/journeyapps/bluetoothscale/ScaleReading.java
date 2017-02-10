package com.journeyapps.bluetoothscale;

public interface ScaleReading {
    double getWeight();

    boolean isFault();

    boolean isOverweight();

    Unit getUnit();

    boolean isStable();

    boolean isZero();

    enum Unit {
        KG(1), LB(2);

        private final int value;

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

        Unit(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static Unit valueOf(int value) {
            switch(value) {
                case 1:
                    return KG;
                case 2:
                    return LB;
            }
            return null;
        }
    }
}
