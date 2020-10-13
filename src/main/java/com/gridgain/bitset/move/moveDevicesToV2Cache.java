package com.gridgain.bitset.move;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.Ignition;
import org.apache.ignite.compute.ComputeTaskFuture;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.lang.IgniteRunnable;

import javax.cache.Cache;
import javax.cache.Cache.Entry;

import com.gridgain.bitset.model.DeviceKey;
import com.gridgain.bitset.model.Device;
// Cannot import another Device, must reference it directly by full packaged name
// import com.gridgain.bitset.model_v2.Device;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
// import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Duration;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class moveDevicesToV2Cache {

    public static void main(String[] args) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        System.out.println(LocalDateTime.now().format(formatter) + " >>> Bitset-moveDevicesToNewCache: Bitset Move Device Entries from original schema to new schema...");
        try (Ignite ignite = Ignition.start("Bitset-client-v2.xml")) {

            System.out.println(LocalDateTime.now().format(formatter) + " >>> Bitset-moveDevicesToNewCache: with started Ignite compute with broadcast >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ...");

            IgniteCache<DeviceKey, Device> srcCache = ignite.getOrCreateCache("DeviceCache");
            IgniteCache<DeviceKey, com.gridgain.bitset.model_v2.Device> trgCache = ignite.getOrCreateCache("DeviceV2Cache");

            List<IgniteFuture<String>> futs = new LinkedList<>();
            for (int i = 0; i < ignite.affinity("DeviceCache").partitions(); i++) {
                int finalI = i;
                final int j = i;
                futs.add( 
                    ignite.compute().<String>affinityCallAsync(Arrays.asList("DeviceCache"), j, () -> {
                        // For each partition (i) create a scan query specific to that partition
                        ScanQuery<DeviceKey, Device> query = new ScanQuery<DeviceKey, Device>().setPartition(j);

                        srcCache.query(query).getAll().stream()
                            .map(
                                (Function<Entry<DeviceKey, Device>, String>) ent -> {
                                    trgCache.put(
                                        ent.getKey(),
                                        new com.gridgain.bitset.model_v2.Device(ent.getValue())
                                    );
                                    return null;
                                }
                            )
                            .collect(Collectors.toList());

                            return ("Partition[" + j + "] - done");
                    })
                );
            }
            for (IgniteFuture<String> f : futs) {
                System.out.println(LocalDateTime.now().format(formatter) + " >>> Bitset-moveDevicesToNewCache[FUTURE RETURN]: " + f.get());
            }

            /*
             * ALTERNATIVE - Do via server broadcast
             * 

            // Get a compute for broadcast to all server nodes
            IgniteCompute compute = ignite.compute(ignite.cluster().forServers());

            // Broadcast this runnable on server nodes, get Async future to watch for return
            // IgniteFuture<?> fut = compute.broadcastAsync( () -> 
            //     {
            //         DateTimeFormatter fmtr = DateTimeFormatter.ISO_DATE_TIME;
            //         LocalDateTime sTime = LocalDateTime.now();
            //         System.out.println(LocalDateTime.now().format(fmtr) + " >>> Bitset-moveDevicesToNewCache[Node Compute] Starting on: " + Ignition.localIgnite().cluster().localNode().id());

            //         System.out.println(LocalDateTime.now().format(fmtr) + " >>> Bitset-moveDevicesToNewCache[Node Compute] get source and target caches..." );
            //         IgniteCache<DeviceKey, Device> srcCache = ignite.getOrCreateCache("DeviceCache");
            //         IgniteCache<DeviceKey, com.gridgain.bitset.model_v2.Device> trgCache = ignite.getOrCreateCache("DeviceV2Cache");

            //         System.out.println(LocalDateTime.now().format(fmtr) + " >>> Bitset-moveDevicesToNewCache[Node Compute] Create node-local query...");
            //         ScanQuery<DeviceKey, Device> query = new ScanQuery <DeviceKey, Device>();
            //         query.setLocal(true);
            //         srcCache.query(query).getAll().stream().map(
            //                 (x) -> ((TemporalAccessor) x)
            //                           .get(0)
            //             ).collect(
            //                 Collectors.toList()
            //             );

            //         System.out.println(LocalDateTime.now().format(fmtr) + " >>> Bitset-moveDevicesToNewCache[Node Compute] Try scan query with cursor...");
            //         try (QueryCursor<Cache.Entry<DeviceKey, Device>> cursor = srcCache.query(query)) {

            //             System.out.println(LocalDateTime.now().format(fmtr) + " >>> Bitset-moveDevicesToNewCache[Node Compute] scan query cursor... ");
            //             // Iteration over the local cluster node data using the scan query.
            //             int n = 0;
            //             for (Cache.Entry<DeviceKey, Device> entry : cursor) {
            //                 // System.out.println(LocalDateTime.now().format(fmtr) + " >>> Bitset-moveDevicesToNewCache[Node Compute] for loop n = " + n++);
            //                 DeviceKey deviceKey = entry.getKey();
            //                 Device device = entry.getValue();
            //                 // System.out.println(LocalDateTime.now().format(fmtr) + " >>> Bitset-moveDevicesToNewCache[Node Compute] for loop n; DeviceKey = " + deviceKey.toString() + "; device: " + device.toString());
            //                 com.gridgain.bitset.model_v2.Device newDevice = new com.gridgain.bitset.model_v2.Device(device);
            //                 trgCache.put(deviceKey, newDevice);
            //                 //System.out.println(LocalDateTime.now().format(fmtr) + " >>> Bitset-moveDevicesToNewCache[Node Compute]: entry: " + n++ + ": " + deviceKey.toString() + "; new device: " + newDevice.toString());
            //             }
            //             System.out.println(LocalDateTime.now().format(fmtr) + " >>> Bitset-moveDevicesToNewCache[Node Compute] for loop complete; Devices migrated: " + n);
            //         }
            //         LocalDateTime eTime = LocalDateTime.now();
            //         System.out.println(LocalDateTime.now().format(fmtr) + " >>> Bitset-moveDevicesToNewCache[Node Compute] Try scan query complete in: " + Duration.between(sTime,eTime).getSeconds() + " seconds.");

            //     }    
            // );

            System.out.println(LocalDateTime.now().format(formatter) + " >>> Bitset-moveDevicesToNewCache: listen on future...");
            // fut.listen(
            //     f -> System.out.println(LocalDateTime.now().format(formatter) + " >>> Bitset-moveDevicesToNewCache: Broadcast compute future finished!!!!!!!!!!!!!!!!!!!!")
            // );
            */

        } catch (Exception e) {
            // DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
			System.out.println(LocalDateTime.now().format(formatter) + " >>> Bitset-moveDevicesToNewCache: CAUGHT EXCEPTION!!!!!!! " + e );
        }
    }
}
