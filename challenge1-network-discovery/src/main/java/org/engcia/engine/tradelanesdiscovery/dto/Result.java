package org.engcia.engine.tradelanesdiscovery.dto;

import org.engcia.engine.tradelanesdiscovery.model.Conclusion;
import org.engcia.engine.tradelanesdiscovery.model.Justification;

public class Result {
    private final Conclusion conclusion;
    private final String justification;

    public Result(Conclusion conclusion, String justification) {
        this.conclusion = conclusion;
        this.justification = justification;
    }

    public Result() {
        this.conclusion = null;
        this.justification = null;
    }

    public Conclusion getConclusion() {
        return conclusion;
    }

    public String getJustification() {
        return justification;
    }

    public String toString() {
        StringBuilder stb = new StringBuilder();
        if (conclusion != null)
            stb.append("Conclusion: " + conclusion.toString());
        if (justification != null)
            stb.append("Justification: " + justification);
        return stb.toString();
    }
}
