package com.gridgain.bitset.model;

import java.util.Objects;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import com.gridgain.bitset.model.Customer;

public class CustomerInternal extends Customer {
    String isrName;
    @QuerySqlField (index = true)
    String departmentName;
    // Customer customer;    

    /** Constructors */
    public CustomerInternal() {
    }

    public CustomerInternal(Customer customer, String isrName, String departmentName) {
        this.isrName = isrName;
        this.departmentName = departmentName;
        this.id = customer.id;
        this.name = customer.name;
        this.address = customer.address;
    }

    /** Getters and Setters */
    public String getIsrName() {
        return this.isrName;
    }

    public void setIsrName(String isrName) {
        this.isrName = isrName;
    }

    public String getDepartmentName() {
        return this.departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    /** hash and equals methods */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof CustomerInternal)) {
            return false;
        }
        CustomerInternal customerInternal = (CustomerInternal) o;
        return Objects.equals(isrName, customerInternal.isrName) 
            && Objects.equals(departmentName, customerInternal.departmentName)
            && Objects.equals(id, customerInternal.id)
            && Objects.equals(name, customerInternal.name)
            && Objects.equals(address, customerInternal.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isrName, departmentName, id, name, address);
    }

    /** toString */
    @Override
    public String toString() {
        return "{" +
            " isrName='" + getIsrName() + "'" +
            ", departmentName='" + getDepartmentName() + "'" +
            ", customer={ id='" + getId() + "'" +
            ", name='" + getName() + "'}" +
            "}";
    }
}
