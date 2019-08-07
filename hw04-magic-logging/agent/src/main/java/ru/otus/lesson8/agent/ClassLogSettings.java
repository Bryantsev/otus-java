package ru.otus.lesson8.agent;

class ClassLogSettings {

    private boolean doLogging;
    private boolean doLoggingAllMethods;

    boolean isDoLogging() {
        return doLogging;
    }

    void setDoLogging(boolean doLogging) {
        this.doLogging = doLogging;
    }

    boolean isDoLoggingAllMethods() {
        return doLoggingAllMethods;
    }

    void setDoLoggingAllMethods(boolean doLoggingAllMethods) {
        this.doLoggingAllMethods = doLoggingAllMethods;
    }

}
