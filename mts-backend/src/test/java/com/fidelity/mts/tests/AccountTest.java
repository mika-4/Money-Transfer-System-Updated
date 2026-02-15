package com.fidelity.mts.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fidelity.mts.entity.Account;
import com.fidelity.mts.exceptions.InsufficientBalanceException;

public class AccountTest {

    private Account acc;

    @BeforeEach
    void setUp() {
        acc = new Account();
        acc.setBalance(BigDecimal.valueOf(1000.0));
    }

    @ParameterizedTest(name = "debit {0} should reduce balance accordingly")
    @MethodSource("values")
    @DisplayName("Debit success")
    void testDebit_Success(BigDecimal amt) {
        BigDecimal before = acc.getBalance();
        acc.debit(amt);
        BigDecimal after = acc.getBalance();
        assertEquals(0, after.compareTo(before.subtract(amt)));
    }

    @ParameterizedTest(name = "credit {0} should increase balance accordingly")
    @MethodSource("values")
    @DisplayName("Credit success")
    void testCredit_Success(BigDecimal amt) {
        BigDecimal before = acc.getBalance();
        acc.credit(amt);
        BigDecimal after = acc.getBalance();
        assertEquals(0, after.compareTo(before.add(amt)));
    }

    @ParameterizedTest(name = "debit {0} should throw InsufficientBalanceException")
    @MethodSource("insuffBal")
    @DisplayName("Debit insufficient balance")
    void testDebit_InsufficientBalance(BigDecimal amt) {
        InsufficientBalanceException ex =
            assertThrows(InsufficientBalanceException.class, () -> acc.debit(amt));
        
       
        String expectedMessage = "insufficient balance. Balance " + acc.getBalance() + " Amount " + amt;
        assertEquals(expectedMessage, ex.getMessage());
    }
    
    static Stream<BigDecimal> values() {
        return Stream.of(
            BigDecimal.valueOf(100.0),
            BigDecimal.valueOf(200.0),
            BigDecimal.valueOf(500.0)
        );
    }

    static Stream<BigDecimal> insuffBal() {
        return Stream.of(
            BigDecimal.valueOf(1005.0),
            BigDecimal.valueOf(1200.0),
            BigDecimal.valueOf(1500.0)
        );
    }
}