package site.sergeyfedorov.util.currency;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Currency;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CurrencyFinderTest {

    private static Stream<Arguments> allValidNumericalCodes () {
        return Currency.getAvailableCurrencies()
            .stream()
            // Issues of java.lang.Currency with specific codes
            // They're not used, so we can just ignore them
            .filter(currency -> currency.getNumericCode() != 0 && currency.getNumericCode() != 891)
            .map(currency -> Arguments.of(currency.getNumericCode(), currency.getCurrencyCode()));
    }

    @ParameterizedTest
    @MethodSource("allValidNumericalCodes")
    public void testFindValidCurrency(int numericalCode, String currencyCode) {
        assertEquals(Currency.getInstance(currencyCode), CurrencyFinder.getCurrencyInstance(numericalCode));
    }

    @Test
    public void testFindCurrencyInvalid() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyFinder.getCurrencyInstance(9999));
    }

    @Test
    public void testFindCurrencyInvalid2() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyFinder.getCurrencyInstance(-1));
    }
}
