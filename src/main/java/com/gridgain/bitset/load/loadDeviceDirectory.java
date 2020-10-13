package com.gridgain.bitset.load;

import javax.cache.Cache;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.IgniteConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.roaringbitmap.RoaringBitmap;

import com.gridgain.bitset.model.*;

public class loadDeviceDirectory {

    private static String fileDirectory = "/data/devices/";

    public static void main(String[] args) throws IgniteException, IOException {

        System.out.println(">>> Bitset-loadDeviceDirectory: Bitset Load Devices from file directory app starting...");

        /*
         * -----------------------------------------------------------------------------------------------------------
         * FIRST: get value from props
         * -----------------------------------------------------------------------------------------------------------
         */
         try (InputStream in = IgniteConfiguration.class.getClassLoader().getResourceAsStream("bitset.properties")) {
            Properties props = new Properties();
            props.load(in);
            fileDirectory = props.getProperty("fileDirectory");
            System.out.println(">>>>>>>>>>>>>>>>> Bitset-loadDeviceDirectory: loaded properties bitset.properties; fileDirectory set to: " + fileDirectory);
        }
        catch (Exception ignored) {
            System.out.println(">>>>>>>>>>>>>>>>> Bitset-loadDeviceDirectory: Failed loading properties; using default fileDirectory: " + fileDirectory);
        }
        
        /*
         * -----------------------------------------------------------------------------------------------------------
         * SECOND: over-ride value with command line (if it works, otherwise take prior value from properties file)
         * -----------------------------------------------------------------------------------------------------------
         */
        if (args != null && args.length != 0) {
            // if a parameter is supplied on run
            try {
                fileDirectory = args[0].toString(); // the only supported arg is the file directory
                System.out.println(">>>>>>>>>>>>>>>>> Bitset-loadDeviceDirectory: setting fileDirectory from command line: " + fileDirectory);
            } catch (Exception e) {
                System.out.println(">>>>>>>>>>>>>>>>> Bitset-loadDeviceDirectory: setting fileDirectory from command line - FAILED!!! fileDirectory stays as: " + fileDirectory);
            }
        }

        File fileFolder = new File(fileDirectory);
        File[] listOfFiles = fileFolder.listFiles();
        System.out.println(">>>>>>>>>>>>>>>>> Bitset-loadDeviceDirectory: fileDirectory: " + fileDirectory + " holds " + listOfFiles.length + " files for processing.");

        int n = 0; // file/record counter

        /*
         * -----------------------------------------------------------------------------------------------------------
         * IGNITE SETUP (connection, cache, datastreamer)
         * -----------------------------------------------------------------------------------------------------------
         */
        System.out.println(">>> Bitset-loadDeviceDirectory: Bitset Client App starting...");
        try (Ignite ignite = Ignition.start("Bitset-client-v1.xml")) {

            try (IgniteCache cache = ignite.getOrCreateCache("DeviceCache")) {
                System.out.println(">>> Bitset-loadDeviceDirectory: Create Key & Value...");

                RoaringBitmap rb = new RoaringBitmap();
                try (IgniteDataStreamer<DeviceKey, Device> streamer = ignite.dataStreamer("DeviceCache")) {

                    /*
                    * -----------------------------------------------------------------------------------------------------------
                    * Go through each file...
                    * 
                    * Assume bitmap of:
                    * 0 - read
                    * 1 - write
                    * 2 - execute
                    * 3 - isAbsolute
                    * 4 - size 0 - 10k
                    * 5 - size 10k - 1m
                    * 6 - size 1m - 10m
                    * 7 - size 10m - 100m
                    * 8 - size 100m - 1GB
                    * 9 - size 1GB - 10GB
                    * 10- size 10GB+
                    * 
                    * -----------------------------------------------------------------------------------------------------------
                    */
                    for (int i = 0; i < listOfFiles.length; i++) {
                        if (listOfFiles[i].isFile()) {
                            System.out.println(">>> Bitset-loadDeviceDirectory: processing file " + listOfFiles[i].getName());

                            DeviceKey k = new DeviceKey(i);

                            // get info for Bitset and create a bitset...
                            if (listOfFiles[i].canRead()) rb.add(0);
                            if (listOfFiles[i].canWrite()) rb.add(1);
                            if (listOfFiles[i].canExecute()) rb.add(2);
                            if (listOfFiles[i].isAbsolute()) rb.add(3);
                            if (listOfFiles[i].length() < 10000 ) {rb.add(4);} 
                            else if (listOfFiles[i].length() < 1000000 ) {rb.add(5);}
                            else if (listOfFiles[i].length() < 10000000 ) {rb.add(6);}
                            else if (listOfFiles[i].length() < 100000000 ) {rb.add(7);}
                            else if (listOfFiles[i].length() < 1000000000 ) {rb.add(8);}
                            else if (listOfFiles[i].length() < 10000000000L ) {rb.add(9);}
                            else if (listOfFiles[i].length() >= 10000000000L ) {rb.add(10);}
                            rb.runOptimize(); // to improve compression

                            // create an object to hold/create byte array from device Demographics Bitset
                            ByteArrayOutputStream rbBos = new ByteArrayOutputStream();
                            DataOutputStream rbDos = new DataOutputStream(rbBos);
                            rb.serialize(rbDos);

                            // create an object to hold/create byte array from device file content
                            Device v = null; 
                            try (
                                InputStream inputStream = new FileInputStream(listOfFiles[i]);
                                ByteArrayOutputStream devBos = new ByteArrayOutputStream();
                                //DataOutputStream devDos = new DataOutputStream(devBos);
                                ) {
                     
                                int byteRead;
                                while ((byteRead = inputStream.read()) != -1) {
                                    devBos.write(byteRead);
                                }
                     
                                // create a device but without content bytes
                                v = new Device(new Timestamp(System.currentTimeMillis()), 
                                    rbBos.toByteArray(), 
                                    devBos.toByteArray()
                                );
                            } catch (IOException ex) {
                                System.out.println(">>> Bitset-loadDeviceDirectory: create device content from file Failed!!! Loading null content; Exception: " + ex);
                                // create a device but without content bytes
                                v = new Device(new Timestamp(System.currentTimeMillis()), 
                                    rbBos.toByteArray(), 
                                    null
                                );
                            }

                            streamer.addData(k, v);
                            rb.clear();

                        } else if (listOfFiles[i].isDirectory()) {
                            System.out.println(">>> Bitset-loadDeviceDirectory: directory " + listOfFiles[i].getName() + " ignored...");
                        }
                    }

                } catch (Exception e) {
                    System.out.println(">>> Bitset-loadDeviceDirectory: Unable to create datastreamer for cache(DeviceCache): " + e);
                }

                System.out.println(">>> Bitset-loadDeviceDirectory: Create Key & Value for directory: "+ fileDirectory +" COMPLETE!!!");

                System.out.println(">>> Bitset-loadDeviceDirectory: testing results...");
                testDemographicBitset(rb, cache);

            } catch (Exception e) {
                System.out.println(">>> Bitset-loadDeviceDirectory: Unable to get or create cache(DeviceCache): " + e);
            }

            System.out.println(">>> Bitset-loadDeviceDirectory: Ignite connection, cache & datastreamer complete!!!");

        } catch (Exception e) {
            System.out.println(">>> Bitset-loadDeviceDirectory: Unable to start ignite using Bitset-client.xml: " + e);
        }
        System.out.println(">>> Bitset-loadDeviceDirectory: Load Device Directory Client App complete!!!");
    }

    private static String getDemographicBitset(RoaringBitmap rb, Device dev) throws IOException {
        byte[] devDem = (byte[])dev.getDemographics();
        ByteArrayInputStream bais = new ByteArrayInputStream(devDem);
        rb.deserialize(new DataInputStream(bais));
        //System.out.println(">>> Bitset-loadDeviceDirectory: Show Demographic Bitset: " + rb.toString());
        return(rb.toString());
    }

    private static void testDemographicBitset(RoaringBitmap rb, IgniteCache cache) throws IOException {
        ScanQuery sq = new ScanQuery(); // get all entries
        QueryCursor<List<Cache.Entry<DeviceKey,Device>>> cur = cache.query(sq);
        //QueryCursor<List<?>> cur = cache.query(sq);
        Iterator iter = cur.iterator();
        while(iter.hasNext()){
            Cache.Entry<DeviceKey,Device> entry = (Cache.Entry<DeviceKey,Device>)iter.next();
            try {
                System.out.println(">>> Bitset-loadDeviceDirectory: testing result for Device key = "+entry.getKey().toString()+"; Device: " + entry.getValue().toString() + "; Device Demographics: " + getDemographicBitset(rb,(Device)entry.getValue()));
                //showDemographics(rb,entry.getValue());
            } catch (IOException e) {
                System.out.println(">>> Bitset-loadDeviceDirectory: Deserialize Roaring Bitset for Device key = "+entry.getKey().toString()+" - FAILED: " + e);
            }
        }
    }

}
