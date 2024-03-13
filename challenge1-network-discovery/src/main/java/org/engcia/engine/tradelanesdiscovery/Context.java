package org.engcia.engine.tradelanesdiscovery;

import org.engcia.engine.tradelanesdiscovery.data.LogisticPointBlockageRepository;
import org.engcia.engine.tradelanesdiscovery.data.LogisticPointRepository;
import org.engcia.engine.tradelanesdiscovery.listeners.TrackingAgendaEventListener;
import org.engcia.engine.tradelanesdiscovery.model.Conclusion;
import org.engcia.engine.tradelanesdiscovery.model.TradeLane;
import org.engcia.engine.tradelanesdiscovery.model.Evidence;
import org.engcia.engine.tradelanesdiscovery.model.TransportationLane;
import org.engcia.engine.tradelanesdiscovery.model.Justification;
import org.engcia.engine.tradelanesdiscovery.model.LogisticPoint;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class Context {
    public KieSession getKS() {
        return KS;
    }

    public TrackingAgendaEventListener getAgendaEventListener() {
        return agendaEventListener;
    }

    public Map<Integer, Justification> getJustifications() {
        return justifications;
    }

    private final KieSession KS;
    private final TrackingAgendaEventListener agendaEventListener;
    private final Map<Integer, Justification> justifications;
    private final LogisticPointRepository logisticPointRepository = new LogisticPointRepository();

    private final LogisticPointBlockageRepository logisticPointBlockageRepository = new LogisticPointBlockageRepository();

    public Context(KieSession session, Map<Integer, Justification> justifications)
    {
        this.KS = session;
        this.agendaEventListener = new TrackingAgendaEventListener(this);
        this.justifications = justifications;
    }

    public boolean answerNot(String ev, String v) {
        return answer(ev, v, true);
    }

    public boolean answer(String ev, String v) {
        return answer(ev, v, false);
    }
    public boolean answer(String ev, String v, boolean not) {
        @SuppressWarnings("unchecked")
        Collection<Evidence> evidences = (Collection<Evidence>) this.KS.getObjects(new ClassObjectFilter(Evidence.class));
        boolean questionFound = false;
        Evidence evidence = null;
        for (Evidence e: evidences) {
            if (e.getEvidence().compareTo(ev) == 0) {
                questionFound = true;
                evidence = e;
                break;
            }
        }
        if (questionFound) {
            Boolean condition = Arrays.asList(v.toLowerCase().split(",")).contains(evidence.getValue().toLowerCase())
                                || this.logisticPointRepository.isCountryInRegion(evidence.getValue(), v);

            if (!not && condition) {
                this.agendaEventListener.addLhs(evidence);
                return true;
            } else if (not && !condition) {
                this.agendaEventListener.addLhs(evidence);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void conclude(Conclusion conclusion) {
        for (TradeLane tl: conclusion.getTradeLanes())
        {
            for (TransportationLane trl: tl.getLanes())
            {
                if (this.logisticPointBlockageRepository.isLogisticPointBlocked(trl.getDestination())) {
                    System.out.println(conclusion.toString() + " discarded due to " + trl.getDestination() + " blockage");
                    return;
                }
            }
        }

        this.agendaEventListener.addRhs(conclusion);
        this.KS.insert(conclusion);
    }

    public String getPreviousAnswer(String ev) {
        Collection<Evidence> evidences = (Collection<Evidence>) this.KS.getObjects(new ClassObjectFilter(Evidence.class));
        for (Evidence e: evidences) {
            if (e.getEvidence().compareTo(ev) == 0) {
                return e.getValue();
            }
        }

        return "";
    }

    // TODO: Check if null
    // create new logistic point
    // if this.logisticPointRepository.getPointsByName(getPreviousAnswer(Evidence.ORIGIN_COUNTRY)) is null
    //      LogisticPoint newLogisticPoint = new LogisticPoint(getPreviousAnswer(Evidence.ORIGIN_COUNTRY));
    //      return this.logisticPointRepository.getPointsByName(newLogisticPoint.getName());
    // else return this.logisticPointRepository.getPointsByName(getPreviousAnswer(Evidence.ORIGIN_COUNTRY));
    public LogisticPoint getOriginCountry() {
        if (this.logisticPointRepository.getPointsByName(getPreviousAnswer(Evidence.ORIGIN_COUNTRY)) == null){
            System.out.print(getPreviousAnswer(Evidence.ORIGIN_COUNTRY));
            return new LogisticPoint(getPreviousAnswer(Evidence.ORIGIN_COUNTRY));
        } else return this.logisticPointRepository.getPointsByName(getPreviousAnswer(Evidence.ORIGIN_COUNTRY));
    }

    public LogisticPoint getDestinationCountry() {
        if (this.logisticPointRepository.getPointsByName(getPreviousAnswer(Evidence.DESTINATION_COUNTRY)) == null){
            System.out.print(getPreviousAnswer(Evidence.DESTINATION_COUNTRY));
            return new LogisticPoint(getPreviousAnswer(Evidence.DESTINATION_COUNTRY));
        } else return this.logisticPointRepository.getPointsByName(getPreviousAnswer(Evidence.DESTINATION_COUNTRY));
    }

    public LogisticPoint getDestinationState() {
        if (this.logisticPointRepository.getPointsByName(getPreviousAnswer(Evidence.DESTINATION_STATE)) == null){
            System.out.print(getPreviousAnswer(Evidence.DESTINATION_STATE));
            return new LogisticPoint(getPreviousAnswer(Evidence.DESTINATION_STATE));
        } else return this.logisticPointRepository.getPointsByName(getPreviousAnswer(Evidence.DESTINATION_STATE));
    }

    public LogisticPoint getLogisticPointByName(String logisticPointName) {
        if (this.logisticPointRepository.getPointsByName(logisticPointName) == null){
            System.out.print(logisticPointName);
            return new LogisticPoint(logisticPointName);
        } else return this.logisticPointRepository.getPointsByName(logisticPointName);
    }

    public void destroy()
    {
        if (this.KS != null) this.KS.destroy();
    }
}
