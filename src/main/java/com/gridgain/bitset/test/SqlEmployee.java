package com.gridgain.bitset.test;

import org.apache.ignite.*;
import org.apache.ignite.cache.query.*;
import org.apache.ignite.cache.query.annotations.*;
import org.apache.ignite.configuration.*;

import java.util.*;


public class SqlEmployee {
    

    private static final String CACHE_NAME = "EmployeeCache";
    private static int seqLast = 0;

    /**
     * @param args Args.
     */
    public static void main(String[] args) {
        try (Ignite ignite = Ignition.start("Bitset-client-v3.xml")) {
            CacheConfiguration<Integer, Integer> cc = new CacheConfiguration<>(CACHE_NAME);

            cc.setIndexedTypes(Integer.class, Integer.class);
            cc.setSqlFunctionClasses(Functions.class);

            try (IgniteCache<Integer, Integer> c = ignite.getOrCreateCache(cc)) {
                Random r = new Random();

                for (int i = 0; i < 10; i++)
                    c.put(r.nextInt(1000), r.nextInt(1000));

                SqlFieldsQuery q = new SqlFieldsQuery("select _val, nextnumb() from Integer");

                for (List<?> row : c.query(q))
                    System.out.println(row.get(0) + " -> " + row.get(1));
            }
        }
    }

    /**
     * Function definitions.
     */
    public static class Functions {
        /**
         * Function must be a static method.
         *
         * @param x Argument.
         * @return Square of given value.
         */
        @QuerySqlFunction
        public static int nextnumb() {
            System.out.println("Custom function has been called with no argument!");
            return seqLast++;
        }
    }

}