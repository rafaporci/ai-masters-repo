package org.engcia.engine.tradelanesdiscovery;

import org.engcia.engine.tradelanesdiscovery.data.LogisticPointBlockageRepository;
import org.engcia.engine.tradelanesdiscovery.data.LogisticPointRepository;
import org.engcia.engine.tradelanesdiscovery.dto.logisticpointblockages.BlockLogisticPointCommand;
import org.engcia.engine.tradelanesdiscovery.dto.logisticpointblockages.UnblockLogisticPointCommand;
import org.engcia.engine.tradelanesdiscovery.model.*;
import org.engcia.engine.tradelanesdiscovery.dto.Result;
import org.engcia.engine.tradelanesdiscovery.dto.Query;
import org.kie.api.KieServices;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.ViewChangedEventListener;

import java.util.Collection;
import java.util.TreeMap;

public class Engine {
    private final LogisticPointBlockageRepository logisticPointBlockageRepository = new LogisticPointBlockageRepository();
    private final LogisticPointRepository logisticPointRepository = new LogisticPointRepository();

    public Result Run(Query query) {
        Context context = null;
        try {
            context = startContext();

            adaptQueryToFacts(query, context);
            context.getKS().fireAllRules();

            return getResult(context);
        } catch (Throwable t) {
            t.printStackTrace();
            return new Result();
        }
        finally {
            if (context != null) context.destroy();
        }
    }

    public boolean BlockLogisticPoint(BlockLogisticPointCommand command) {
        try {
            LogisticPoint logisticPoint = this.logisticPointRepository.getPointsByName(command.getLogisticPointName());
            if (logisticPoint == null)
                return false;
            this.logisticPointBlockageRepository.addBlockage(new LogisticPointBlockage(logisticPoint, command.getReason()));
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    public boolean UnBlockLogisticPoint(UnblockLogisticPointCommand command) {
        try {
            LogisticPoint logisticPoint = this.logisticPointRepository.getPointsByName(command.getLogisticPointName());
            if (logisticPoint == null)
                return false;
            this.logisticPointBlockageRepository.removeBlockage(logisticPoint);
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    private Context startContext()
    {
        // load up the knowledge base
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieSession kSession = kContainer.newKieSession("ksession-rules");
        Context context = new Context(kSession, new TreeMap<Integer, Justification>());
        kSession.addEventListener(context.getAgendaEventListener());

        // Query listener
        ViewChangedEventListener listener = new ViewChangedEventListener() {
            @Override
            public void rowDeleted(Row row) {
            }

            @Override
            public void rowInserted(Row row) {
                // stop inference engine after as soon as got a conclusion
                kSession.halt();
            }

            @Override
            public void rowUpdated(Row row) {
            }
        };

        context.getKS().openLiveQuery("Conclusions", null,  listener);
        context.getKS().setGlobal( "myContext", context);

        return context;
    }

    private void adaptQueryToFacts(Query query, Context context) {
        context.getKS().insert(new Evidence(Evidence.DELIVERY_TYPE, convertNullToEmpty(query.getDeliveryType())));
        context.getKS().insert(new Evidence(Evidence.ORIGIN_COUNTRY, convertNullToEmpty(query.getOrigin())));
        context.getKS().insert(new Evidence(Evidence.ORIGIN_STATE, convertNullToEmpty(query.getOriginState())));
        context.getKS().insert(new Evidence(Evidence.DESTINATION_COUNTRY, convertNullToEmpty(query.getDestinationCountry())));
        context.getKS().insert(new Evidence(Evidence.DESTINATION_STATE, convertNullToEmpty(query.getDestinationState())));
        context.getKS().insert(new Evidence(Evidence.COLLECTION_POINT, convertNullToEmpty(query.getColletionPoint())));
    }

    private String convertNullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private Result getResult(Context context) {
        Collection<Conclusion> conclusions = (Collection<Conclusion>) context.getKS().getObjects(new ClassObjectFilter(Conclusion.class));

        if (!conclusions.isEmpty())
        {
            Conclusion conclusion = conclusions.stream().findFirst().get();
            How how = new How(context.getJustifications());
            return new Result(conclusion, how.getHowExplanation(conclusion.getId()));
        }
        else
            return new Result();
    }
}

