package com.homeapp.javatraining.dto;

public abstract class BaseStats {
    protected int total;
    protected int passed;

    public void incrementTotal() {
        total++;
    }

    public void incrementPassed() {
        passed++;
    }

    public int getTotal() {
        return total;
    }

    public int getPassed() {
        return passed;
    }

    public int getSuccessRate() {
        if (total == 0) {
            return 0;
        }
        return (passed * 100) / total;
    }
}
