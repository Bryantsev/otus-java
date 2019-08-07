package ru.otus.lesson8.agent;

import java.util.Arrays;

public class MethodDescription {

    private int access;
    private String name;
    private String descriptor;
    private String signature;
    private String[] exceptions;

    public MethodDescription() {
    }

    public MethodDescription(int access, String name, String descriptor, String signature, String[] exceptions) {
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.exceptions = exceptions;
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String[] getExceptions() {
        return exceptions;
    }

    public void setExceptions(String[] exceptions) {
        this.exceptions = exceptions;
    }

    @Override
    public String toString() {
        return "MethodForLogging{" +
            "access=" + access +
            ", name='" + name + '\'' +
            ", descriptor='" + descriptor + '\'' +
            ", signature='" + signature + '\'' +
            ", exceptions=" + Arrays.toString(exceptions) +
            '}';
    }

    public static class Param {

    }

}
