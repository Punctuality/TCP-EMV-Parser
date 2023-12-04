package site.sergeyfedorov.emv.model;

import org.junit.jupiter.api.Test;
import site.sergeyfedorov.emv.model.tag.BinaryEMVTag;
import site.sergeyfedorov.util.limits.Limited;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class BinaryEMVTagTest {

    @Test
    public void testValidBinary() {
        assertTrue(
            new BinaryEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue(new byte[]{
                    0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xA
                })
        );
    }

    @Test
    public void testInvalidBigBinary() {
        assertFalse(
            new BinaryEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue(new byte[]{
                    0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xA, 0xB, 0xC, 0xD, 0xE
                })
        );
    }

    @Test
    public void testInvalidSmallBinary() {
        assertFalse(
            new BinaryEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue(new byte[]{
                    0x0, 0x1, 0x2
                })
        );
    }

    @Test
    public void testConvertBinary() {
        byte[] data = new byte[]{
            0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xA
        };
        assertEquals(data,
            new BinaryEMVTag(Limited.to(10), Limited.to(12))
                .parseTagValue(data)
        );
    }

}
