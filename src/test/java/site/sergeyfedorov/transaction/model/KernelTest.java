package site.sergeyfedorov.transaction.model;

import org.junit.jupiter.api.Test;
import site.sergeyfedorov.emv.model.tag.EMVTag;
import site.sergeyfedorov.emv.model.tag.EMVTagId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KernelTest {

    @Test
    public void testParseTag() {
        EMVTag data = new EMVTag(
          0x9f2a,
          EMVTagId.KERNEL_IDENTIFIER,
            (short) 0x02,
            new byte[] {0x00, 0x02}
        );

        Kernel result = Kernel.parseTag(data);

        assertEquals(Kernel.MASTERCARD, result);
    }

    @Test
    public void testParseMalformedAmexTag() {
        EMVTag data = new EMVTag(
            0x9f2a,
            EMVTagId.KERNEL_IDENTIFIER,
            (short) 0x03,
            new byte[] {0x04, 0x00, 0x00}
        );

        Kernel result = Kernel.parseTag(data);

        assertEquals(Kernel.AMEX, result);
    }

    @Test
    public void testUnknownKernel() {
        EMVTag data = new EMVTag(
            0x9f2a,
            EMVTagId.KERNEL_IDENTIFIER,
            (short) 0x01,
            new byte[] {0x00}
        );

        Kernel result = Kernel.parseTag(data);

        assertEquals(Kernel.UNKNOWN, result);
    }
}
