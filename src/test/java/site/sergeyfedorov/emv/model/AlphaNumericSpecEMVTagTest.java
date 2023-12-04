package site.sergeyfedorov.emv.model;

import org.junit.jupiter.api.Test;
import site.sergeyfedorov.emv.model.tag.AlphaNumericSpecialEMVTag;
import site.sergeyfedorov.util.limits.Limited;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class AlphaNumericSpecEMVTagTest {

    @Test
    public void testValidAlphaNumericSpec() {
        assertTrue(
            new AlphaNumericSpecialEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue("number#/123".getBytes(StandardCharsets.UTF_8))
        );
    }

    @Test
    public void testInvalidBigAlphaNumericSpec() {
        assertFalse(
            new AlphaNumericSpecialEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue("numbernumber#/123".getBytes(StandardCharsets.UTF_8))
        );
    }

    @Test
    public void testInvalidSmallAlphaNumericSpec() {
        assertFalse(
            new AlphaNumericSpecialEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue("#/123".getBytes(StandardCharsets.UTF_8))
        );
    }

    @Test
    public void testInvalidCharAlphaNumericSpec() {
        assertFalse(
            new AlphaNumericSpecialEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue("number#/123±".getBytes(StandardCharsets.UTF_8))
        );
    }

    @Test
    public void testConvertAlphaNumericSpec() {
        String data = "number#/123";
        assertEquals(data,
            new AlphaNumericSpecialEMVTag(Limited.to(10), Limited.to(12))
                .parseTagValue(data.getBytes(StandardCharsets.UTF_8))
        );
    }

}
