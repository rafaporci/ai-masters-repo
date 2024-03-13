package org.engcia.engine.premiumservicessimulator.listeners;

import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;

public class FactListener implements RuleRuntimeEventListener{

	@Override
	public void objectDeleted(ObjectDeletedEvent event) {
		System.out.println(
						"Retracted: " +
						event.getOldObject().toString()
						);
	}

	@Override
	public void objectInserted(ObjectInsertedEvent event) {
		// FactHandle fact = event.getFactHandle();
		System.out.println(
						"Asserted: " +
						event.getObject().toString()
						);
	}

	@Override
	public void objectUpdated(ObjectUpdatedEvent event) {

		System.out.println(
				"Updated: " +
				event.getObject().toString()
				);

	}

}