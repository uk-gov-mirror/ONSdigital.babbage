package com.github.onsdigital.babbage.request.handler;

public class CookiesPolicy {
    private boolean essential;
    private boolean usage;

    public CookiesPolicy(boolean essential, boolean usage) {
        this.essential = essential;
        this.usage = usage;
    }

    public boolean isEssential() {
        return essential;
    }

    public void setEssential(boolean essential) {
        this.essential = essential;
    }

    public boolean isUsage() {
        return usage;
    }

    public void setUsage(boolean usage) {
        this.usage = usage;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("CookiesPolicy{").append("Essential: ")
                .append(essential).append(", Usage: ")
                .append(usage).append("}").toString();
    }
}
