package app.cacheservice;

import java.math.BigDecimal;

public class DoubleVal {
    public static void main(String[] args) {
        BigDecimal val = new BigDecimal("0.81500");
        System.out.println(val.doubleValue());
    }
}
