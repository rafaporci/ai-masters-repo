package org.engcia.engine.premiumservicessimulator.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.engcia.engine.premiumservicessimulator.model.FactCF;

public class TrackingAgendaListener implements AgendaEventListener {
	private KieSession kieSession;
	private List<Object> activations;
	private String ruleName;
	private double ruleCF;

	public KieSession getKieSession() {
		return kieSession;
	}

	public List<Object> getActivations() {
		return activations;
	}

	public double getRuleCF() {
		return ruleCF;
	}

	public String getRuleName() {
		return ruleName;
	}

	public FactCF getFactRef(Class<?> c, String description, String value) {
		Collection<FactCF> myfacts = (Collection<FactCF>) this.kieSession.getObjects( new ClassObjectFilter(c) );
		Iterator<FactCF> iterator = myfacts.iterator();
		while (iterator.hasNext()) {
			FactCF fact = iterator.next();
			String factDesc = fact.getDescription();
			String factVal = fact.getValue();
			if (factDesc.compareTo(description) == 0 && factVal.compareTo(value) == 0) {
				return fact;
			}
		}
		return null;
	}

//	getLHSminimumCF - uses only conjunctive rule
	public double getLHSminimumCF(Object obj) {

		// remove conclusion object from activations list
		this.getActivations().remove(obj);

		// get the minimum CF value from LHS (considering only the "and" operator)
		this.getActivations().sort(null);
		FactCF f = (FactCF)this.getActivations().get(0);
		return f.getCf();
	}

	public TrackingAgendaListener() {
		super();
		this.kieSession = null;
		this.activations = new ArrayList<>();
		this.ruleName = null;
		this.ruleCF = 0;
	}

	@Override
	public void afterMatchFired(AfterMatchFiredEvent event) {
		// Clear activation object list to the next activation
		this.activations.clear();
	}

	@Override
	public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void agendaGroupPushed(AgendaGroupPushedEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeMatchFired(BeforeMatchFiredEvent event) {
		this.kieSession = (KieSession) event.getKieRuntime().getKieBase().getKieSessions()
				.toArray()[0];
		this.activations.addAll(event.getMatch().getObjects());
		Map<String, Object> metaData = event.getMatch().getRule().getMetaData();
		if (metaData.containsKey("CF")) {
			if (metaData.get("CF") instanceof Double) {
				this.ruleCF = ((Double) metaData.get("CF"));
			}
		}
		else {
			this.ruleCF = 1; // deterministic rule. Never happens in our case.
		}
		this.ruleName = event.getMatch().getRule().getName();
	}

	@Override
	public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void matchCancelled(MatchCancelledEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void matchCreated(MatchCreatedEvent event) {
		// TODO Auto-generated method stub

	}

}