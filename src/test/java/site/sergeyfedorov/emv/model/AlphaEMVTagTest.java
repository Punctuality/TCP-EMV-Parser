package site.sergeyfedorov.emv.model;

import org.junit.jupiter.api.Test;
import site.sergeyfedorov.emv.model.tag.AlphaEMVTag;
import site.sergeyfedorov.util.limits.Limited;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class AlphaEMVTagTest {

    @Test
    public void testValidAlpha() {
        assertTrue(
            new AlphaEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue("numbernumber".getBytes(StandardCharsets.UTF_8))
        );
    }

    @Test
    public void testInvalidBigAlpha() {
        assertFalse(
            new AlphaEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue("numbernumbernumber".getBytes(StandardCharsets.UTF_8))
        );
    }

    @Test
    public void testInvalidSmallAlpha() {
        assertFalse(
            new AlphaEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue("number".getBytes(StandardCharsets.UTF_8))
        );
    }

    @Test
    public void testInvalidCharAlpha() {
        assertFalse(
            new AlphaEMVTag(Limited.to(10), Limited.to(12))
                .validateTagValue("number12345".getBytes(StandardCharsets.UTF_8))
        );
    }

    @Test
    public void testConvertAlpha() {
        String data = "numbernumber";
        assertEquals(data,
            new AlphaEMVTag(Limited.to(10), Limited.to(12))
                .parseTagValue(data.getBytes(StandardCharsets.UTF_8))
        );
    }

}
