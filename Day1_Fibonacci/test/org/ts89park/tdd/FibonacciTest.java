package org.ts89park.tdd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FibonacciTest {

    @Test
    public void testWithSmallBoundary() {
        long [] solutions = new long[] {2, 3, 5};
        Fibonacci fibonacci = new Fibonacci();

        assertEquals(solutions[0], fibonacci.getFibonacci(3));
        assertEquals(solutions[1], fibonacci.getFibonacci(4));
        assertEquals(solutions[2], fibonacci.getFibonacci(5));
    }

    @Test
    public void testWithLargeBoundary() {
        long [] solutions = new long[] {4660046610375530309L, 7540113804746346429L};
        Fibonacci fibonacci = new Fibonacci();

        assertEquals(solutions[0], fibonacci.getFibonacci(91));
        assertEquals(solutions[1], fibonacci.getFibonacci(92));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -2, Integer.MIN_VALUE})
    public void testWithInvalidNumber(int invalidNumber) {
        Fibonacci fibonacci = new Fibonacci();

        assertEquals(0, fibonacci.getFibonacci(invalidNumber));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    public void testWithSpecialNumber(int specialNumber) {
        Fibonacci fibonacci = new Fibonacci();

        assertEquals(1, fibonacci.getFibonacci(specialNumber));
    }

    @ParameterizedTest
    @ValueSource(ints = {93, 94, Integer.MAX_VALUE})
    public void testWithInvalidLargeNumber(int invalidLargeNumber) {
        Fibonacci fibonacci = new Fibonacci();

        assertThrows(IllegalArgumentException.class, () -> fibonacci.getFibonacci(invalidLargeNumber));
    }
}
