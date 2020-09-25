package com.gridgain.bitset.move;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.Ignition;
import org.apache.ignite.compute.ComputeTaskFuture;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.lang.IgniteRunnable;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;

import com.gridgain.bitset.model.DeviceKey;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;

import javax.cache.Cache;

import com.gridgain.bitset.model.Device;
// Cannot import these, must reference them directly by full packaged name
// import com.gridgain.bitset.model_v2.Device;

public class testComputeBroadcast {

    public static void main(String[] args) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        System.out.println(LocalDateTime.now().format(formatter) + " >>> testComputeBroadcast: Bitset Move Device Entries from original schema to new schema...");
        try (Ignite ignite = Ignition.start("Bitset-client-v2.xml")) {

            System.out.println(LocalDateTime.now().format(formatter) + " >>> testComputeBroadcast: with started Ignite compute with broadcast...");
            // Broadcast to remote nodes only and enable asynchronous mode.
            IgniteCompute compute = ignite.compute(ignite.cluster().forServers()); // withAsync();

            // Prepare for using compute broadcast...
            System.out.println(LocalDateTime.now().format(formatter) + " >>> testComputeBroadcast:  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
 
            // System.out.println(">>> testComputeBroadcast: compute get future...");
            // ComputeTaskFuture<?> fut = compute.future();
 
            IgniteFuture<?> fut = compute.broadcastAsync( () -> 
                {
                    DateTimeFormatter fmtr = DateTimeFormatter.ISO_DATE_TIME;
                    System.out.println(LocalDateTime.now().format(fmtr) + " >>> testComputeBroadcast: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

                    try {
                        System.out.println(LocalDateTime.now().format(fmtr) + " >>> testComputeBroadcast: Node Compute: 10s DELAY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						Thread.sleep(10000);
                        System.out.println(LocalDateTime.now().format(fmtr) + " >>> testComputeBroadcast: Node Compute: Another 10s DELAY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						Thread.sleep(20000);
                        System.out.println(LocalDateTime.now().format(fmtr) + " >>> testComputeBroadcast: Node Compute: Another 10s DELAY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						Thread.sleep(20000);
					} catch (InterruptedException e) {
						// e.printStackTrace();
                        System.out.println(LocalDateTime.now().format(fmtr) + " >>> testComputeBroadcast: Sleep interupted " + e);
					}

                    System.out.println(LocalDateTime.now().format(fmtr) + " >>> testComputeBroadcast: Node Compute: Complete on: " + Ignition.localIgnite().cluster().localNode().id());
                    System.out.println(LocalDateTime.now().format(fmtr) + " >>> testComputeBroadcast: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                }    
            );

            // System.out.println(LocalDateTime.now().format(formatter) + " >>> testComputeBroadcast: compute get future...");
            // ComputeTaskFuture<?> fut = compute.future();

            System.out.println(LocalDateTime.now().format(formatter) + ">>> testComputeBroadcast: listen on future...");
            fut.listen(
                f -> System.out.println(LocalDateTime.now().format(formatter) + ">>> testComputeBroadcast: Finished sending broadcast job.")
            );

        } catch (Exception e) {
            System.out.println(LocalDateTime.now().format(formatter) + ">>> testComputeBroadcast: CAUGHT EXCEPTION!!!!!!! " + e );
        }
    }
}
