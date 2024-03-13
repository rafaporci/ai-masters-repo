package org.engcia.engine.tradelanesdiscovery.data;

import org.engcia.engine.tradelanesdiscovery.model.LogisticPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class LogisticPointRepository {
    private final Map<String, List<LogisticPoint>> points;

    public LogisticPointRepository() {
        this.points = new HashMap<>();
        this.initializeRegions();
    }

    //public Map<String, List<LogisticPoint>> getPoints() {
    //    return points;
    //}

    public LogisticPoint getPointsByName(String name) {
        for (List<LogisticPoint> countries : this.points.values())
        {
            for (LogisticPoint point : countries) {
                if (point.getName().equalsIgnoreCase(name)) {
                    return point;
                }
            }
        }
        return null;
    }

    //public Boolean containsKey(String key) {
    //    return this.points.containsKey(key);
    //}


    public boolean isCountryInRegion(String country, String region) {

        // If "Mundo" is the region specified in the rule
        // Check if continent or contry is a child of Mundo in our Repository
        if (region.equalsIgnoreCase("Mundo")) {
            return true;
        // If we answer with a continent and we have a region in the rule
        } else if (this.points.containsKey(country.toLowerCase())) {
            return country.equalsIgnoreCase(region);
        // If we answer with Country and a Continent is in the rule
        } else if (this.points.containsKey(region.toLowerCase())){
            List<LogisticPoint> countries = points.get(region.toLowerCase());
            for (LogisticPoint location : countries) {
                if (location.getName().equalsIgnoreCase(country)) {
                    return true;
                }
            }
        // If we answer with a country and the country is in the rule
        } else return country.equalsIgnoreCase(region);
        return false;
    }

    private void initializeRegions() {
        LogisticPoint mundo = new LogisticPoint("Mundo");
        List<LogisticPoint> continentes = new ArrayList<>();

        continentes.add(new LogisticPoint(mundo, "Europa"));
        LogisticPoint europa = new LogisticPoint("Europa");
        europa.setParent(mundo);
        List<LogisticPoint> europaCountries = new ArrayList<>();
        europaCountries.add(new LogisticPoint(europa,"Portugal"));
        europaCountries.add(new LogisticPoint(europa,"Espanha"));
        europaCountries.add(new LogisticPoint(europa,"Alemanha"));
        europaCountries.add(new LogisticPoint(europa,"Franca"));
        europaCountries.add(new LogisticPoint(europa,"Italia"));
        europaCountries.add(new LogisticPoint(europa,"Holanda"));
        europaCountries.add(new LogisticPoint(europa,"Belgica"));
        europaCountries.add(new LogisticPoint(europa,"Suecia"));
        europaCountries.add(new LogisticPoint(europa,"Noruega"));
        europaCountries.add(new LogisticPoint(europa,"Dinamarca"));
        europaCountries.add(new LogisticPoint(europa,"Finlandia"));
        europaCountries.add(new LogisticPoint(europa,"Suica"));
        europaCountries.add(new LogisticPoint(europa,"Austria"));
        europaCountries.add(new LogisticPoint(europa,"Polonia"));
        europaCountries.add(new LogisticPoint(europa,"Republica Checa"));
        europaCountries.add(new LogisticPoint(europa,"Hungria"));
        europaCountries.add(new LogisticPoint(europa,"Croacia"));
        europaCountries.add(new LogisticPoint(europa,"Eslovaquia"));
        europaCountries.add(new LogisticPoint(europa,"Grecia"));
        europaCountries.add(new LogisticPoint(europa,"Bulgaria"));
        europaCountries.add(new LogisticPoint(europa,"Romenia"));
        europaCountries.add(new LogisticPoint(europa,"Suecia"));
        europaCountries.add(new LogisticPoint(europa,"Ucrania"));
        europaCountries.add(new LogisticPoint(europa,"Russia"));
        europaCountries.add(new LogisticPoint(europa,"Inglaterra"));

        continentes.add(new LogisticPoint(mundo, "Asia"));
        LogisticPoint asia = new LogisticPoint("Asia");
        asia.setParent(mundo);
        LogisticPoint china = new LogisticPoint(asia,"China");
        List<LogisticPoint> asiaCountries = new ArrayList<>();
        asiaCountries.add(china);
        asiaCountries.add(new LogisticPoint(china,"Beijing"));
        asiaCountries.add(new LogisticPoint(china,"Hebei"));
        asiaCountries.add(new LogisticPoint(china,"Shandong"));
        asiaCountries.add(new LogisticPoint(asia,"Hong Kong"));
        asiaCountries.add(new LogisticPoint(asia,"Beijing"));
        asiaCountries.add(new LogisticPoint(asia,"India"));
        asiaCountries.add(new LogisticPoint(asia,"Japao"));


        continentes.add(new LogisticPoint(mundo, "Africa"));
        LogisticPoint africa = new LogisticPoint("Africa");
        africa.setParent(mundo);
        List<LogisticPoint> africaCountries = new ArrayList<>();
        africaCountries.add(new LogisticPoint(africa,"Nigeria"));
        africaCountries.add(new LogisticPoint(africa,"Egito"));
        africaCountries.add(new LogisticPoint(africa,"RD Congo"));
        africaCountries.add(new LogisticPoint(africa,"Africa do Sul"));
        africaCountries.add(new LogisticPoint(africa,"Tanzania"));
        africaCountries.add(new LogisticPoint(africa,"Quenia"));
        africaCountries.add(new LogisticPoint(africa,"Uganda"));
        africaCountries.add(new LogisticPoint(africa,"Argelia"));
        africaCountries.add(new LogisticPoint(africa,"Sudao"));
        africaCountries.add(new LogisticPoint(africa,"Marrocos"));
        africaCountries.add(new LogisticPoint(africa,"Angola"));
        africaCountries.add(new LogisticPoint(africa,"Mocambique"));
        africaCountries.add(new LogisticPoint(africa,"Gana"));
        africaCountries.add(new LogisticPoint(africa,"Camaroes"));
        africaCountries.add(new LogisticPoint(africa,"Costa do Marfim"));
        africaCountries.add(new LogisticPoint(africa,"Niger"));
        africaCountries.add(new LogisticPoint(africa,"Burkina Faso"));
        africaCountries.add(new LogisticPoint(africa,"Mali"));
        africaCountries.add(new LogisticPoint(africa,"Senegal"));
        africaCountries.add(new LogisticPoint(africa,"Chade"));
        africaCountries.add(new LogisticPoint(africa,"Somalia"));
        africaCountries.add(new LogisticPoint(africa,"Etiopia"));
        africaCountries.add(new LogisticPoint(africa,"Ruanda"));
        africaCountries.add(new LogisticPoint(africa,"Burundi"));
        africaCountries.add(new LogisticPoint(africa,"Uganda"));
        africaCountries.add(new LogisticPoint(africa,"Quenia"));
        africaCountries.add(new LogisticPoint(africa,"Tanzania"));
        africaCountries.add(new LogisticPoint(africa,"Seychelles"));

        continentes.add(new LogisticPoint(mundo, "America do Norte"));
        LogisticPoint americaDoNorte = new LogisticPoint("America do Norte");
        americaDoNorte.setParent(mundo);
        List<LogisticPoint> americaDoNorteCountries = new ArrayList<>();
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Estados Unidos"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Canada"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Mexico"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Cuba"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Haiti"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Republica Dominicana"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Guatemala"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Honduras"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Nicaragua"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Costa Rica"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Panama"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"El Salvador"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Belize"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Jamaica"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Trinidad e Tobago"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Bahamas"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Barbados"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Santa Lucia"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Dominica"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Sao Cristovao e Nevis"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Antigua e Barbuda"));
        americaDoNorteCountries.add(new LogisticPoint(americaDoNorte,"Granada"));

        continentes.add(new LogisticPoint(mundo, "America do Sul"));
        LogisticPoint americaDoSul = new LogisticPoint("America do Sul");
        americaDoSul.setParent(mundo);
        List<LogisticPoint> americaDoSulCountries = new ArrayList<>();
        americaDoSulCountries.add(new LogisticPoint(americaDoSul,"Brasil"));
        americaDoSulCountries.add(new LogisticPoint(americaDoSul,"Argentina"));
        americaDoSulCountries.add(new LogisticPoint(americaDoSul,"Peru"));
        americaDoSulCountries.add(new LogisticPoint(americaDoSul,"Venezuela"));
        americaDoSulCountries.add(new LogisticPoint(americaDoSul,"Colombia"));
        americaDoSulCountries.add(new LogisticPoint(americaDoSul,"Chile"));
        americaDoSulCountries.add(new LogisticPoint(americaDoSul,"Equador"));
        americaDoSulCountries.add(new LogisticPoint(americaDoSul,"Bolivia"));
        americaDoSulCountries.add(new LogisticPoint(americaDoSul,"Paraguai"));
        americaDoSulCountries.add(new LogisticPoint(americaDoSul,"Uruguai"));
        americaDoSulCountries.add(new LogisticPoint(americaDoSul,"Guiana"));
        americaDoSulCountries.add(new LogisticPoint(americaDoSul,"Suriname"));
        americaDoSulCountries.add(new LogisticPoint(americaDoSul,"Guiana Francesa"));
        americaDoSulCountries.add(new LogisticPoint(americaDoSul,"Falkland Islands"));

        continentes.add(new LogisticPoint(mundo, "Australia"));
        LogisticPoint australia = new LogisticPoint("Australia");
        australia.setParent(mundo);
        List<LogisticPoint> australiaCountries = new ArrayList<>();
        australiaCountries.add(new LogisticPoint(australia,"Australia"));
        australiaCountries.add(new LogisticPoint(australia,"Nova Zelandia"));
        australiaCountries.add(new LogisticPoint(australia,"Papua Nova Guine"));
        australiaCountries.add(new LogisticPoint(australia,"Ilhas Salomao"));
        australiaCountries.add(new LogisticPoint(australia,"Vanuatu"));
        australiaCountries.add(new LogisticPoint(australia,"Samoa"));
        australiaCountries.add(new LogisticPoint(australia,"Kiribati"));
        australiaCountries.add(new LogisticPoint(australia,"Tonga"));
        australiaCountries.add(new LogisticPoint(australia,"Tuvalu"));
        australiaCountries.add(new LogisticPoint(australia,"Nauru"));
        australiaCountries.add(new LogisticPoint(australia,"Ilhas Marshall"));
        australiaCountries.add(new LogisticPoint(australia,"Palau"));
        australiaCountries.add(new LogisticPoint(australia,"Micronesia"));
        australiaCountries.add(new LogisticPoint(australia,"Ilhas Cook"));
        australiaCountries.add(new LogisticPoint(australia,"Niue"));
        australiaCountries.add(new LogisticPoint(australia,"Tokelau"));
        australiaCountries.add(new LogisticPoint(australia,"Wallis e Futuna"));
        australiaCountries.add(new LogisticPoint(australia,"Ilhas Pitcairn"));
        australiaCountries.add(new LogisticPoint(australia,"Ilha Norfolk"));

        this.points.put("mundo",continentes);
        this.points.put("europa", europaCountries);
        this.points.put("asia", asiaCountries);
        this.points.put("africa", africaCountries);
        this.points.put("america do norte", americaDoNorteCountries);
        this.points.put("america do sul", americaDoSulCountries);
        this.points.put("australia", australiaCountries);
    }
}


