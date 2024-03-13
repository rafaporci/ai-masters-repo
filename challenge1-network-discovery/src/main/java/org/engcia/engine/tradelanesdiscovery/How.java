package org.engcia.engine.tradelanesdiscovery;

import java.util.Map;

import org.engcia.engine.tradelanesdiscovery.model.Fact;
import org.engcia.engine.tradelanesdiscovery.model.Justification;

public class How {
    private Map<Integer, Justification> justifications;

    public How(Map<Integer, Justification> justifications) {
        this.justifications = justifications;
    }
    public String getHowExplanation(Integer factNumber) {
        StringBuilder sb = new StringBuilder();
        Justification j = justifications.get(factNumber);
        if (j != null) { // justification for Fact factNumber was found
            sb.append("It was obtained by rule " + j.getRuleName() + " due to:\n");
            sb.append("\n");
            for (Fact f : j.getLhs()) {
                sb.append(f + "\n");
            }
        }

        return sb.toString();
    }   
}
