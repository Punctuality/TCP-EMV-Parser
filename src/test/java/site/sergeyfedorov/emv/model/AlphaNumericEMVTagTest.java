package site.sergeyfedorov.emv.model;

import org.junit.jupiter.api.Test;
import site.sergeyfedorov.emv.model.tag.AlphaNumericEMVTag;
import site.sergeyfedorov.util.limits.Limited;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class AlphaNumericEMVTagTest {

    @Test
    public void testValidAlphaNumeric() {
        assertTrue(
            new AlphaNumericEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue("number123456".getBytes(StandardCharsets.UTF_8))
        );
    }

    @Test
    public void testInvalidBigAlpha() {
        assertFalse(
            new AlphaNumericEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue("numbernumber123456".getBytes(StandardCharsets.UTF_8))
        );
    }

    @Test
    public void testInvalidSmallAlpha() {
        assertFalse(
            new AlphaNumericEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue("num123".getBytes(StandardCharsets.UTF_8))
        );
    }

    @Test
    public void testInvalidCharAlpha() {
        assertFalse(
            new AlphaNumericEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue("number 123".getBytes(StandardCharsets.UTF_8))
        );
    }

    @Test
    public void testConvertAlpha() {
        String data = "number12345";
        assertEquals(data,
            new AlphaNumericEMVTag(Limited.to(10), Limited.to(12))
                .parseTagValue(data.getBytes(StandardCharsets.UTF_8))
        );
    }

}
