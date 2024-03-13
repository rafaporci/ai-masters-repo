package org.engcia.engine.premiumservicessimulator.dto;

import org.engcia.engine.premiumservicessimulator.model.Hypothesis;

import java.util.Collection;

public class Result {
    private final Collection<Hypothesis> hypotheses;

    public Result(Collection<Hypothesis> hypotheses) {
        this.hypotheses = hypotheses;
    }

    public Collection<Hypothesis> getHypothesis() {
        return hypotheses;
    }
}