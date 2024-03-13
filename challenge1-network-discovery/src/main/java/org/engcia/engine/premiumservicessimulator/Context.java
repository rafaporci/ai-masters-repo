package org.engcia.engine.premiumservicessimulator;

import org.engcia.engine.premiumservicessimulator.listeners.TrackingAgendaListener;
import org.kie.api.runtime.KieSession;

public class Context {
    public KieSession getKS() {
        return KS;
    }

    public TrackingAgendaListener getAgendaEventListener() {
        return agendaEventListener;
    }

    private final KieSession KS;
    private final TrackingAgendaListener agendaEventListener;
    public Context(KieSession session)
    {
        this.KS = session;
        this.agendaEventListener = new TrackingAgendaListener();
    }

    public void destroy()
    {
        if (this.KS != null) this.KS.destroy();
    }
}