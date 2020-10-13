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
import java.util.Arrays;
// import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class moveDevicesToV3Cache {

    public static void main(String[] args) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        System.out.println(LocalDateTime.now().format(formatter) + " >>> Bitset-moveDevicesToV3Cache: Bitset Move Device Entries from original schema to new schema...");
        try (Ignite ignite = Ignition.start("Bitset-client-v3.xml")) {

            System.out.println(LocalDateTime.now().format(formatter) + " >>> Bitset-moveDevicesToV3Cache: with started Ignite compute with broadcast >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ...");

            IgniteCache<DeviceKey, Device> srcCache = ignite.getOrCreateCache("DeviceCache");
            IgniteCache<com.gridgain.bitset.model_v3.DeviceKey, com.gridgain.bitset.model_v3.Device> trgCache = ignite.getOrCreateCache("DeviceV3Cache");

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
                                        new com.gridgain.bitset.model_v3.DeviceKey(ent.getKey()),
                                        new com.gridgain.bitset.model_v3.Device(ent.getValue())
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
                System.out.println(LocalDateTime.now().format(formatter) + " >>> Bitset-moveDevicesToV3Cache[FUTURE RETURN]: " + f.get());
            }

        } catch (Exception e) {
            // DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
			System.out.println(LocalDateTime.now().format(formatter) + " >>> Bitset-moveDevicesToV3Cache: CAUGHT EXCEPTION!!!!!!! " + e );
        }
    }
}
