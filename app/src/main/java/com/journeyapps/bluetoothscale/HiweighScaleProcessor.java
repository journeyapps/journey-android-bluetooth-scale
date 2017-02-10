package com.journeyapps.bluetoothscale;

import java.util.Arrays;

public class HiweighScaleProcessor {

    protected class HiweighScaleException extends Exception { }

    public class InvalidPayloadException extends HiweighScaleException { }

    public class InvalidChecksumException extends HiweighScaleException { }

    public class InvalidReadingException extends HiweighScaleException { }

    private static HiweighScaleProcessor instance = new HiweighScaleProcessor();

    private HiweighScaleProcessor() { }

    public static HiweighScaleProcessor getInstance() {
        return instance;
    }

    private static final int expectedPayloadSize = 11;

    public ScaleReading constructReading(byte[] payload) throws HiweighScaleException {

        if (payload == null || payload.length != expectedPayloadSize) {
            throw new InvalidPayloadException();
        }

        if (payload[0] != 2 || payload[expectedPayloadSize-1] != 13) {
            throw new InvalidPayloadException();
        }

        // validate payload checksum
        // bytes [1-8] are compared against byte 9 as a checksum
        final int expectedChecksum = payload[9] & 255;
        int checksumAccumulator = 0;
        for (int i = 1; i <= 8; i++) {
            checksumAccumulator += payload[i];
        }
        int checksum = (checksumAccumulator & 255) | 128;

        if (checksum != expectedChecksum) {
            throw new InvalidChecksumException();
        }

        /**
         * Bit mask for measurement information:
         *  7 N/A
         *  6 N/A
         *  5 N/A
         *  4 Fault
         *  3 Overweight
         *  2 pounds (not kilograms)
         *  1 stable
         *  0 zero
         */
        byte infoMask = payload[1];
        final boolean fault = isBitSet(infoMask, 4);
        final boolean overweight = isBitSet(infoMask, 3);
        final ScaleReading.Unit unit = isBitSet(infoMask, 2) ? ScaleReading.Unit.LB : ScaleReading.Unit.KG;
        final boolean stable = isBitSet(infoMask, 1);
        final boolean zero = isBitSet(infoMask, 0);

        // bytes [2,8] are the weight reading as a string
        final String weightString = new String(Arrays.copyOfRange(payload, 2, 8+1));
        double weight;
        try {
            weight = Double.parseDouble(weightString);
        } catch(NumberFormatException exception) {
            throw new InvalidReadingException();
        }

        return new HiweighScaleReading(weight, fault, overweight, unit, stable, zero);
    }

    private static boolean isBitSet(byte b, int bit)
    {
        // Reference: http://stackoverflow.com/a/1034501
        return (b & (1 << bit)) != 0;
    }

}
