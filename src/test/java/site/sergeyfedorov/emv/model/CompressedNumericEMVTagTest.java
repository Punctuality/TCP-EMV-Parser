package site.sergeyfedorov.emv.model;

import org.junit.jupiter.api.Test;
import site.sergeyfedorov.emv.model.tag.AlphaEMVTag;
import site.sergeyfedorov.emv.model.tag.CompressedNumericEMVTag;
import site.sergeyfedorov.util.limits.Limited;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class CompressedNumericEMVTagTest {

    @Test
    public void testValidCNumeric() {
        assertTrue(
            new CompressedNumericEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue(new byte[]{
                    0x12, 0x12, 0x12, 0x13, 0x34, 0x5F
                }));
    }

    @Test
    public void testInvalidBigCNumeric() {
        assertFalse(
            new CompressedNumericEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue(new byte[]{
                    0x12, 0x12, 0x12, 0x13, 0x34, 0x56, 0x7F
                })
        );
    }

    @Test
    public void testInvalidSmallCNumeric() {
        assertFalse(
            new CompressedNumericEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue(new byte[]{
                    0x12, 0x12, 0x1F
                })
        );
    }

    @Test
    public void testInvalidCharCNumeric() {
        assertFalse(
            new CompressedNumericEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue(new byte[]{
                    0x12, 0x12, 0x12, (byte) 0xFF, 0x34, 0x55
                })
        );
    }

    @Test
    public void testConvertCNumeric() {
        byte[] data = new byte[]{
            0x12, 0x12, 0x12, 0x13, 0x34, 0x5F
        };
        assertEquals(new BigInteger("12121213345"),
            new CompressedNumericEMVTag(Limited.to(10), Limited.to(12)).parseTagValue(data)
        );
    }

}
