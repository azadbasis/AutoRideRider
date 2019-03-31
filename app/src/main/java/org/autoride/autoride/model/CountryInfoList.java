package org.autoride.autoride.model;

import java.util.List;

public class CountryInfoList extends WebMessages {

    private List<CountryInfo> countries;

    public List<CountryInfo> getCountries() {
        return countries;
    }

    public void setCountries(List<CountryInfo> countries) {
        this.countries = countries;
    }
}