package org.engcia.engine.tradelanesdiscovery.listeners;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.event.DefaultAgendaEventListener;
import org.engcia.engine.tradelanesdiscovery.Context;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.*;
import org.kie.api.runtime.rule.Match;

import org.engcia.engine.tradelanesdiscovery.model.Fact;
import org.engcia.engine.tradelanesdiscovery.model.Justification;

@SuppressWarnings("restriction")
public class TrackingAgendaEventListener extends DefaultAgendaEventListener{

    public TrackingAgendaEventListener(Context context){
        this.context = context;
    }

    private List<Match> matchList = new ArrayList<Match>();
    private List<Fact> lhs = new ArrayList<Fact>();
    private List<Fact> rhs = new ArrayList<Fact>();

    private final Context context;

    public void resetLhs() {
        lhs.clear();
    }

    public void addLhs(Fact f) {
        if (!lhs.contains(f))
            lhs.add(f);
    }

    public void resetRhs() {
        rhs.clear();
    }

    public void addRhs(Fact f) {
        rhs.add(f);
    }

    @Override
    public void matchCreated(MatchCreatedEvent event) {
    }
    @Override
    public void matchCancelled(MatchCancelledEvent event) {
    }
    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
    }
    @Override
    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
    }
    @Override
    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
    }
    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        Rule rule = event.getMatch().getRule();
        String ruleName = rule.getName();

        List <Object> list = event.getMatch().getObjects();
        for (Object e : list) {
            if (e instanceof Fact) {
                lhs.add((Fact)e);
            }
        }

        for (Fact f: rhs) {
            Justification j = new Justification(ruleName, lhs, f);
            this.context.getJustifications().put(f.getId(), j);
        }

        resetLhs();
        resetRhs();
    }
}
