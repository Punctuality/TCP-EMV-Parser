package site.sergeyfedorov.transaction.repr;

import org.junit.jupiter.api.Test;
import site.sergeyfedorov.transaction.model.Kernel;
import site.sergeyfedorov.transaction.model.Transaction;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionBeautifierTest {
    @Test
    public void testDiscreetBeautify() {
        Transaction transaction = new Transaction(
            Kernel.VISA,
            "1234567890123456",
            new BigDecimal("123.45"),
            Currency.getInstance("USD")
        );

        TransactionBeautifier beautifier = new TransactionBeautifier(true);
        String result = beautifier.apply(transaction);

        assertEquals("""
            Transaction {
                kernel: Visa
                cardNumber: 123456******3456 (16 digits)
                amount: 123.45
                currency: US Dollar (USD)
            }
            """, result);
    }

    @Test
    public void testSensitiveBeautify() {
        Transaction transaction = new Transaction(
            Kernel.VISA,
            "1234567890123456",
            new BigDecimal("123.45"),
            Currency.getInstance("USD")
        );

        TransactionBeautifier beautifier = new TransactionBeautifier(false);
        String result = beautifier.apply(transaction);

        assertEquals("""
            Transaction {
                kernel: Visa
                cardNumber: 1234567890123456 (16 digits)
                amount: 123.45
                currency: US Dollar (USD)
            }
            """, result);
    }
}
