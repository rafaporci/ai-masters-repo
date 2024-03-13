package org.engcia.engine.premiumservicessimulator;

import org.engcia.engine.premiumservicessimulator.dto.Query;
import org.engcia.engine.premiumservicessimulator.dto.Result;
import org.engcia.engine.premiumservicessimulator.listeners.FactListener;
import org.engcia.engine.premiumservicessimulator.model.Evidence;
import org.engcia.engine.premiumservicessimulator.model.Hypothesis;
import org.kie.api.KieServices;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;


public class Engine {

    //richCountries content is based on https://en.wikipedia.org/wiki/List_of_countries_by_GDP_(nominal)_per_capita (IMF estimate for 2023)
    static final Set<String> richCountries = new HashSet<>(
            Arrays.asList("Suica",
                    "Noruega",
                    "Estados Unidos",
                    "Dinamarca",
                    "Australia",
                    "Holanda",
                    "Austria",
                    "Suecia",
                    "Finlandia",
                    "Belgica",
                    "Canada",
                    "Alemanha",
                    "Hong Kong",
                    "Inglaterra",
                    "Nova Zelandia",
                    "Franca",
                    "Italia",
                    "Japao",
                    "Espanha"));
    public Result Run(Query query) {
        Context context = null;

        try {
            context = startContext();

            context.getKS().insert(new Hypothesis(0.0, "selectPremium", "true", context)); //baseline (CF 0 = unknown)

            context.getKS().insert(new Evidence(1, "RichDestinationCountry", this.isRichCountry(query.getDestinationCountry()) ? "true" : "false", context));
            context.getKS().insert(new Evidence(1, "StandardShippingInDays", String.valueOf(query.getStandardShippingInDays()), context));
            context.getKS().insert(new Evidence(1, "VipCustomer", String.valueOf(query.isVipCustomer()), context));
            context.getKS().insert(new Evidence(1, "OrderAbove5000USD", query.getOrderValue() > 5000 ? "true" : "false", context));

            context.getKS().fireAllRules();

            return new Result ((Collection<Hypothesis>)context.getKS().getObjects(new ClassObjectFilter(Hypothesis.class)));
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (context != null) context.destroy();
        }

        return new Result(new ArrayList<>());
    }

    private org.engcia.engine.premiumservicessimulator.Context startContext() {
        // load up the knowledge base
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieSession kSession = kContainer.newKieSession("ksession-rules-fc");

        Context context = new Context(kSession);
        kSession.setGlobal("myContext", context);

        // Agenda listener
        kSession.addEventListener(context.getAgendaEventListener());

        // Facts listener
        kSession.addEventListener(new FactListener());

        return context;
    }

    private boolean isRichCountry(String country) {
        return richCountries.contains(country);
    }
}