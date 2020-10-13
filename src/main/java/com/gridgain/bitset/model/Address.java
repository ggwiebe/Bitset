package com.gridgain.bitset.model;

import java.util.Objects;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class Address {
    /** Indexed field. Will be visible for SQL engine. */
    @QuerySqlField
    private String street;

    @QuerySqlField (index = true)
    private String city;

    @QuerySqlField (index = true)
    private String country;

    @QuerySqlField(index = true)
    private String postalCode;

    /** Constructors */
    public Address() {
    }

    public Address(String street, String city, String country, String postalCode) {
        this.street = street;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
    }

    /** Getters and Setters */
    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    /** toString */
    @Override
    public String toString() {
        return "{" +
            " street='" + getStreet() + "'" +
            ", city='" + getCity() + "'" +
            ", country='" + getCountry() + "'" +
            ", postalCode='" + getPostalCode() + "'" +
        "}";
    }

    /** hash and equals methods */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Address)) {
            return false;
        }
        Address address = (Address) o;
        return Objects.equals(street, address.street) && Objects.equals(city, address.city) && Objects.equals(country, address.country) && Objects.equals(postalCode, address.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, country, postalCode);
    }

}
