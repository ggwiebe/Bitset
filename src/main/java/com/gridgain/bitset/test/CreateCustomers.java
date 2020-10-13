package com.gridgain.bitset.test;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.cache.Cache;

import com.gridgain.bitset.model.*;

/** This file was generated by Ignite Web Console (09/08/2020, 16:10) **/
public class CreateCustomers {

    /**
     * Start up client node with specified configuration.
     * 
     * @param args Command line arguments, none required.
     * @throws Exception If failed.
     **/
    public static void main(String[] args) throws Exception {
        System.out.println(">>> CreateCustomers: Client App starting...");
        try (Ignite ignite = Ignition.start("Bitset-client-v1.xml")) {

            try (IgniteCache<Long, Customer> custCache = ignite.getOrCreateCache("CustomerCache")) {
                System.out.println(">>> CreateCustomers: 1. Create Customer..............................");
                Customer cust1 = new Customer(
                    1L,
                    "First Customer",
                    new Address("132 Hampton Av", "Toronto", "Canada", "M4K 2Z1")
                );
                boolean wasThere = custCache.putIfAbsent( 1L, cust1 );
                System.out.println( wasThere ?  
                    ">>> CreateCustomers: 1. Created Customer: " + custCache.get(1L):
                    ">>> CreateCustomers: 1. Customer already there: "  + custCache.get(1L)
                );

                System.out.println(">>> CreateCustomers: 2. Create CustomerInternal......................");
                Customer cust2 = new CustomerInternal(
                    new Customer(
                        2L,
                        "Second Customer",
                        new Address("107 South St", "Boston", "USA", "02111")
                    ),
                    "Judy",
                    "HR"
                );
                wasThere = custCache.putIfAbsent(2L, (Customer)cust2);
                System.out.println( wasThere ?  
                    ">>> CreateCustomers: 2. Created CustomerInternal: " + custCache.get(2L) :
                    ">>> CreateCustomers: 2. CustomerInternal already there: "  + custCache.get(2L)
                );


                System.out.println(">>> CreateCustomers: 3. Create ExternalCustomer......................");
                Customer cust3 = new CustomerExternal(
                    new Customer(
                        3L,
                        "Third Customer",
                        new Address("26 Wellington Av", "Boston", "USA", "02106")
                    ),
                    "Paul",
                    "Amex"
                );
                wasThere = custCache.putIfAbsent(3L, (Customer)cust3);
                System.out.println( wasThere ?  
                    ">>> CreateCustomers: 3. Created CustomerInternal: " + custCache.get(3L):
                    ">>> CreateCustomers: 3. CustomerInternal already there: "  + custCache.get(3L)
                );
                // SqlFieldsQuery qry = new SqlFieldsQuery("SELECT * FROM CUSTOMERS;", true);
                // QueryCursor cur = custCache.query(qry);


            } catch (Exception e) {
                System.out.println(">>> CreateCustomers: Exception with cache(CustomerCache): " + e);
            }

            System.out.println(">>> CreateCustomers: Ignite Config complete!!!");

        } catch (Exception e) {
            System.out.println(">>> CreateCustomers: Unable to start ignite using Bitset-client.xml: " + e);
        }
        System.out.println(">>> CreateCustomers: Client App complete!!!");
    }

}