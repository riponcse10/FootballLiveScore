package com.sfuronlabs.livescore.football.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ripon
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryLeague implements Serializable {
    private String country;

    private List<League> leagues;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<League> getLeagues() {
        return leagues;
    }

    public void setLeagues(List<League> leagues) {
        this.leagues = leagues;
    }

    @Override
    public String toString() {
        StringBuilder allLeagues = new StringBuilder();
        for (int i = 0; i < leagues.size(); i++) {
            allLeagues.append(leagues.get(i).toString());
        }
        return "CountryLeague{" +
                "country='" + country + '\'' +
                ", leagues=" + allLeagues +
                '}';
    }
}
