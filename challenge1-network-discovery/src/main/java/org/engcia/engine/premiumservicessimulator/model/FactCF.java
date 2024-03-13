package org.engcia.engine.premiumservicessimulator.model;

import org.kie.api.runtime.rule.FactHandle;
import org.engcia.engine.premiumservicessimulator.Context;

public class FactCF implements Comparable<FactCF>, Uncertainty {
	private double cf;
	private String description;
	private String value;
	private Context context;

	public FactCF(double cf, String description, String value, Context context) {
		super();
		this.cf = cf;
		this.description = description;
		this.value = value;
		this.context = context;
	}

	public double getCf() {
		return cf;
	}

	public void setCf(double cf) {
		this.cf = cf;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int compareTo(FactCF f) {
		if(this.getCf() < f.getCf()) {
			return -1;
		}
		else if(this.getCf() > f.getCf()) {
			return 1;
		}
		return 0;
	}

	@Override
	public String toString() {
		return this.getClass().getName() +
				"[CF=" + cf + " ; " +
				"description=" + description + " ; " + "Value=" + value + "]";
	}

	@Override
	public void update() {

		// get conclusion fact handle
		FactHandle fHandle = context.getKS().getFactHandle(this);

		double lhsCF = context.getAgendaEventListener().getLHSminimumCF(this);

		// update this object (Rule conclusion)

		// calculate newCF (propagate certainty)
		double newCF = lhsCF * context.getAgendaEventListener().getRuleCF();

		// update conclusion object in working memory
		this.updateCF(newCF);
		context.getKS().update(fHandle, this);
	}

	private void updateCF(double newCF) {
		double combinedCF = combineCF(this.getCf(), newCF);
		this.setCf(combinedCF);
	}

	private double combineCF(double oldCF, double newCF) {
		double combinedCF;
		if(oldCF >= 0 && newCF >=0) {
			combinedCF = oldCF + newCF * (1 - oldCF);
		} else if(oldCF <= 0 && newCF <= 0) {
			combinedCF = oldCF + newCF * (1 + oldCF);
		} else {
			combinedCF = (oldCF + newCF) / (1 - Math.min(Math.abs(oldCF), Math.abs(newCF)));
		}
		return combinedCF;
	}

}