package com.gridgain.bitset.test;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.cache.Cache;

import org.roaringbitmap.RoaringBitmap;

import com.gridgain.bitset.model.*;

/** This file was generated by Ignite Web Console (09/08/2020, 16:10) **/
public class CreateDevice {
    /**
     * Start up node with specified configuration.
     * 
     * @param args Command line arguments, none required.
     * @throws Exception If failed.
     **/
    public static void main(String[] args) throws Exception {
        System.out.println(">>> Bitset-CeateDevice: Bitset Client App starting...");
        try (Ignite ignite = Ignition.start("Bitset-client.xml")) {

            try (IgniteCache cache = ignite.getOrCreateCache("DeviceCache")) {
                System.out.println(">>> Bitset-CeateDevice: Create Key & Value...");

                RoaringBitmap rb = new RoaringBitmap();
                try (IgniteDataStreamer<DeviceKey, Device> streamer = ignite.dataStreamer("DeviceCache")) {
                    for (int i = 0; i < 10; i++) {
                        DeviceKey k = new DeviceKey(i);

                        // get info for Bitset and create a bitset...
                        rb.add(i);
                        rb.runOptimize(); // to improve compression

                        ByteArrayOutputStream rbBos = new ByteArrayOutputStream();
                        DataOutputStream rbDos = new DataOutputStream(rbBos);
                        rb.serialize(rbDos);

                        Device v = new Device(new Timestamp(System.currentTimeMillis()), 
                            rbBos.toByteArray(), 
                            //("demographics("+i+")").getBytes(),
                            ("Content(" + i + ") here...").getBytes()
                        );
                        streamer.addData(k, v);
                        rb.clear();
                    }
                } catch (Exception e) {
                    System.out.println(">>> Bitset-CeateDevice: Unable to create datastreamer for cache(DeviceCache): " + e);
                }

                System.out.println(">>> Bitset-CeateDevice: Create Key & Value (10 times): COMPLETE!!!");

                ScanQuery sq = new ScanQuery(); // get all entries
                QueryCursor<List<Cache.Entry<DeviceKey,Device>>> cur = cache.query(sq);
                //QueryCursor<List<?>> cur = cache.query(sq);
                Iterator iter = cur.iterator();
                while(iter.hasNext()){
                    Cache.Entry<DeviceKey,Device> entry = (Cache.Entry<DeviceKey,Device>)iter.next();
                    try {
                        System.out.println(">>> Bitset-CeateDevice: testing result for Device key = "+entry.getKey().toString()+"; Device: " + entry.getValue().toString() + "; Device Demographics: " + getDemographicBitset(rb,(Device)entry.getValue()));
                        //showDemographics(rb,entry.getValue());
                    } catch (IOException e) {
                        System.out.println(">>> Bitset-CeateDevice: Deserialize Roaring Bitset for Device key = "+entry.getKey().toString()+" - FAILED: " + e);
                    }
                }


            } catch (Exception e) {
                System.out.println(">>> Bitset-CeateDevice: Unable to get or create cache(DeviceCache): " + e);
            }

            System.out.println(">>> Bitset-CeateDevice: Ignite Config complete!!!");

        } catch (Exception e) {
            System.out.println(">>> Bitset-CeateDevice: Unable to start ignite using Bitset-client.xml: " + e);
        }
        System.out.println(">>> Bitset-CeateDevice: Bitset Client App complete!!!");
    }

    private static String getDemographicBitset(RoaringBitmap rb, Device dev) throws IOException {
        byte[] devDem = (byte[])dev.getDemographics();
        ByteArrayInputStream bais = new ByteArrayInputStream(devDem);
        rb.deserialize(new DataInputStream(bais));
        //System.out.println(">>> Bitset-CeateDevice: Show Demographic Bitset: " + rb.toString());
        return(rb.toString());
    }
}