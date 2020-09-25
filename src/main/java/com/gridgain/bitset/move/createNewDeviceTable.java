package com.gridgain.bitset.move;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

public class createNewDeviceTable {
    public static void main(String[] args) {
        try (Ignite ignite = Ignition.start("Bitset-client-v2.xml")) {
            System.out.println("completed client v2... Check Table in new schema!");
        } catch (Exception e) {
            //TODO: handle exception
        };
    }
}
