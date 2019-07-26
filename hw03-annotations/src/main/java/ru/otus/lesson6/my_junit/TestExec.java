package ru.otus.lesson6.my_junit;

public class TestExec {
    private int numExec;
    private boolean isSuccess;
    private String error;

    public TestExec() {
    }

    TestExec(int numExec, boolean isSuccess, String error) {
        this.numExec = numExec;
        this.isSuccess = isSuccess;
        this.error = error;
    }

    int getNumExec() {
        return numExec;
    }

    void setNumExec(int numExec) {
        this.numExec = numExec;
    }

    boolean isSuccess() {
        return isSuccess;
    }

    void setSuccess(boolean success) {
        isSuccess = success;
    }

    String getError() {
        return error;
    }

    void setError(String error) {
        this.error = error;
    }
}
