package org.engcia.engine.tradelanesdiscovery.model;

public class LogisticPoint {

    private LogisticPoint parent;

    private final String name;

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

    public void setParent(LogisticPoint parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return name;
    }
    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof LogisticPoint)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        LogisticPoint c = (LogisticPoint) o;

        // Compare the data members and return accordingly
        return c.getName().equals(this.name);
    }
}
