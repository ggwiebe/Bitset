package com.gridgain.bitset.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;
import com.gridgain.bitset.model.Customer;
import java.util.Objects;

public class CustomerExternal extends Customer {
    String repName;
    @QuerySqlField (index = true)
    String companyName;
    // Customer customer;

    /** Constructors */
    public CustomerExternal() {
    }

    public CustomerExternal(Customer customer, String repName, String companyName) {
        this.repName = repName;
        this.companyName = companyName;
        this.id = customer.id;
        this.name = customer.name;
        this.address = customer.address;
    }

    /** Getters and Setters */
    public String getRepName() {
        return this.repName;
    }

    public void setRepName(String repName) {
        this.repName = repName;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    // public Customer getCustomer() {
    //     return this.customer;
    // }

    // public void setCustomer(Customer customer) {
    //     this.customer = customer;
    // }

    /** hash and equals methods */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof CustomerExternal)) {
            return false;
        }
        CustomerExternal customerExternal = (CustomerExternal) o;
        return Objects.equals(repName, customerExternal.repName) 
            && Objects.equals(companyName, customerExternal.companyName)
            && Objects.equals(id, customerExternal.id)
            && Objects.equals(name, customerExternal.name)
            && Objects.equals(address, customerExternal.address);

    }

    @Override
    public int hashCode() {
        return Objects.hash(repName, companyName, id, name, address);
    }

    /** toString */
    @Override
    public String toString() {
        return "{" +
            " isrName='" + getRepName() + "'" +
            ", departmentName='" + getCompanyName() + "'" +
            ", customer={ id='" + getId() + "'" +
            ", name='" + getName() + "'}" +
            "}";
    }
}
