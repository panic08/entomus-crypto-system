package by.panic.entomus.util;

import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class RounderUtil {
    public double roundToNDecimalPlaces(double number, int n) {
        double factor = Math.pow(10, n);
        return Math.round(number * factor) / factor;
    }

    public BigInteger replaceDigitsWithZero(BigInteger number, int index) {
        if (index < 0 || index >= number.toString().length()) {
            throw new IllegalArgumentException("Index out of bounds");
        }

        String numberString = number.toString();
        StringBuilder modifiedNumber = new StringBuilder(numberString.substring(0, index));

        for (int i = index; i < numberString.length(); i++) {
            modifiedNumber.append("0");
        }

        return new BigInteger(modifiedNumber.toString());
    }
}
