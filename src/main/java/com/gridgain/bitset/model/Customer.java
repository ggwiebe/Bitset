package com.gridgain.bitset.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;
import java.util.Objects;
// import com.gridgain.bitset.model.Address;

public class  Customer {
    @QuerySqlField (index = true)
    Long id;

    @QuerySqlField (index = true)
    String name;

    com.gridgain.bitset.model.Address address;

    /** Constructors */
    public Customer() {
    }

    public Customer(Long id, String name, com.gridgain.bitset.model.Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    /** Getters and Setters */
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public com.gridgain.bitset.model.Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    /** hash and equals methods */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Customer)) {
            return false;
        }
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) && Objects.equals(name, customer.name) && Objects.equals(address, customer.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address);
    }

    /** toString */
    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            " name='" + getName() + "'" +
            ", address='" + address.toString() + "'" +
        "}";
    }

}