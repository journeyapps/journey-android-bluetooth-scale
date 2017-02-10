package com.journeyapps.bluetoothscale;

import org.junit.Test;

import static org.junit.Assert.*;

public class HiweighScaleProcessorUnitTest {

    @Test
    public void canGetInstance() throws Exception {
        assertNotNull(HiweighScaleProcessor.getInstance());
    }

    @Test
    public void constructReading_isCorrect() throws Exception {
        byte[] payload = new byte[] { 2, 66, 32, 32, 45, 48, 46, 49, 48, -18, 13 };

        ScaleReading reading = HiweighScaleProcessor.getInstance().constructReading(payload);

        assertNotNull(reading);
        assertEquals(-0.10f, reading.getWeight(), 0.01f);
        assertEquals(false, reading.isFault());
        assertEquals(false, reading.isOverweight());
        assertEquals(ScaleReading.Unit.KG, reading.getUnit());
        assertEquals(true, reading.isStable());
        assertEquals(false, reading.isZero());
    }

    @Test
    public void constructReading_isCorrectWhenNotStable() throws Exception {
        byte[] payload = new byte[] { 2, 64, 32, 32, 32, 56, 46, 48, 48, -26, 13 };

        ScaleReading reading = HiweighScaleProcessor.getInstance().constructReading(payload);

        assertNotNull(reading);
        assertEquals(8.00f, reading.getWeight(), 0.01f);
        assertEquals(false, reading.isFault());
        assertEquals(false, reading.isOverweight());
        assertEquals(ScaleReading.Unit.KG, reading.getUnit());
        assertEquals(false, reading.isStable());
        assertEquals(false, reading.isZero());
    }

    @Test
    public void constructReading_isCorrectWhenZero() throws Exception {
        byte[] payload = new byte[] { 2, 67, 32, 32, 32, 48, 46, 48, 48, -31, 13 };

        ScaleReading reading = HiweighScaleProcessor.getInstance().constructReading(payload);

        assertNotNull(reading);
        assertEquals(0.00f, reading.getWeight(), 0.01f);
        assertEquals(false, reading.isFault());
        assertEquals(false, reading.isOverweight());
        assertEquals(ScaleReading.Unit.KG, reading.getUnit());
        assertEquals(true, reading.isStable());
        assertEquals(true, reading.isZero());
    }

    @Test
    public void constructReading_isCorrectWhenInPounds() throws Exception {
        byte[] payload = new byte[] { 2, 68, 32, 32, 50, 57, 46, 57, 56, -114, 13 };

        ScaleReading reading = HiweighScaleProcessor.getInstance().constructReading(payload);

        assertNotNull(reading);
        assertEquals(29.98f, reading.getWeight(), 0.01f);
        assertEquals(false, reading.isFault());
        assertEquals(false, reading.isOverweight());
        assertEquals(ScaleReading.Unit.LB, reading.getUnit());
        assertEquals(false, reading.isStable());
        assertEquals(false, reading.isZero());
    }
}
