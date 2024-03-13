package org.engcia.engine.premiumservicessimulator.model;

public class LogisticPoint {

    private LogisticPoint parent;

    private String name;

    public LogisticPoint(LogisticPoint parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public LogisticPoint(String name) {
        this.name = name;
    }

    public LogisticPoint getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        if (parent == null)
            return name;
        else
            return (name + " belongs to " + parent.toString());
    }
}
