package org.ts89park.tdd;

public class Fibonacci {

    public long getFibonacci(int number) throws IllegalArgumentException {
        if (number <= 0) {
            return 0;
        } else if (number > 92) {
            throw new IllegalArgumentException("Number is too large.");
        }

        long olderFibonacci = 1;
        long oldFibonacci = 1;
        long currentFibonacci = 1;

        for (int i = 3; i <= number; i++) {
            olderFibonacci = oldFibonacci;
            oldFibonacci = currentFibonacci;

            currentFibonacci = olderFibonacci + oldFibonacci;
        }

        return currentFibonacci;
    }
}
