package site.sergeyfedorov.emv.model;

import org.junit.jupiter.api.Test;
import site.sergeyfedorov.emv.model.tag.NumericEMVTag;
import site.sergeyfedorov.util.limits.Limited;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class NumericEMVTagTest {

    @Test
    public void testValidNumeric() {
        assertTrue(
            new NumericEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue(new byte[]{
                    0x02, 0x12, 0x12, 0x13, 0x34, 0x56
                }));
    }

    @Test
    public void testInvalidBigNumeric() {
        assertFalse(
            new NumericEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue(new byte[]{
                    0x02, 0x12, 0x12, 0x13, 0x34, 0x56, 0x78
                })
        );
    }

    @Test
    public void testInvalidSmallNumeric() {
        assertFalse(
            new NumericEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue(new byte[]{
                    0x02, 0x12, 0x13
                })
        );
    }

    @Test
    public void testInvalidCharNumeric() {
        assertFalse(
            new NumericEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue(new byte[]{
                    0x02, 0x12, 0x12, (byte) 0x9F, 0x34, 0x55
                })
        );
    }

    @Test
    public void testConvertNumeric() {
        byte[] data = new byte[]{
            0x02, 0x12, 0x12, 0x13, 0x34, 0x56
        };
        assertEquals(new BigInteger("21212133456"),
            new NumericEMVTag(Limited.to(10), Limited.to(12)).parseTagValue(data)
        );
    }

}
