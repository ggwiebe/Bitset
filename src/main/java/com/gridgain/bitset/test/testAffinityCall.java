package com.gridgain.bitset.test;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
// import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
// import org.apache.ignite.cache.query.SqlFieldsQuery;
// import org.apache.ignite.events.JobEvent;
// import org.apache.ignite.internal.processors.cache.persistence.tree.BPlusTree.Get;
import org.apache.ignite.lang.IgniteFuture;
import javax.cache.Cache.Entry;

import com.gridgain.bitset.model.Device;
import com.gridgain.bitset.model.DeviceKey;

// import java.util.Collection;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class testAffinityCall {
    public static void main(String[] args) {

        try (Ignite ignite = Ignition.start("Bitset-client-v2.xml")) {

            IgniteCache<DeviceKey, Device> srcCache = ignite.getOrCreateCache("DeviceCache");
            IgniteCache<DeviceKey, com.gridgain.bitset.model_v2.Device> trgCache = ignite.getOrCreateCache("DeviceV2Cache");

            List<IgniteFuture<String>> futs = new LinkedList<>();
            for (int i = 0; i < ignite.affinity("DeviceCache").partitions(); i++) {
                int finalI = i;
                final int j = i;
                futs.add( // The method add(IgniteFuture<List<?>>) in the type List<IgniteFuture<List<?>>>
                          // is not applicable for the arguments (IgniteFuture<String>)
                    ignite.compute().<String>affinityCallAsync(Arrays.asList("DeviceCache"), j, () -> {
                        // For each partition (i) create a scan query specific to that partition
                        ScanQuery<DeviceKey, Device> query = new ScanQuery<DeviceKey, Device>().setPartition(j);

                        srcCache.query(query)
                            .getAll()
                            .stream()
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

                        /*
                        QueryCursor<Entry<DeviceKey, Device>> iqCursor = srcCache.query(query);

                        // srcCache // Cannot infer type argument(s) for <R> map(Function<? super T,?
                        // extends R>)
                        // .query(query) // QueryCursor<Entry<DeviceKey,Device>>
                        // .getAll() // <List<Entry<DeviceKey,Device>>>
                        List<Entry<DeviceKey, Device>> eList = iqCursor.getAll();

                        // .stream() // Stream<Entry<DeviceKey,Device>>
                        Stream<Entry<DeviceKey, Device>> eStream = eList.stream();

                        // .map(
                        List oList = eStream.map(  // The method map(Function<? super Cache.Entry<DeviceKey,Device>,? extends R>) in the type Stream<Cache.Entry<DeviceKey,Device>> is not applicable for the arguments ((<no type> ent) -> {})
                            // (Function<? super Entry<DeviceKey, Device>, String>) ent -> {
                            (Function<Entry<DeviceKey, Device>, String>) ent -> {
                                trgCache.put(
                                    ent.getKey(),
                                    new com.gridgain.bitset.model_v2.Device(ent.getValue())
                                );
                                return null;
                            }
                        ).collect(Collectors.toList());
                        //         x -> {
                        //             trgCache.put(x.getKey(), new com.gridgain.bitset.model_v2.Device(x.getValue()));
                        //         }
                        //     )               // Stream<Object>
                        //     .collect(Collectors.toList());
                        */
                        
                        return ("Partition[" + j + "] - done");
                    })
                );
            }
            for (IgniteFuture<String> f : futs) {  // Type mismatch: cannot convert from element type IgniteFuture<String> to IgniteFuture<List<?>>
                System.out.println(f.get());
            }
        }
    }
}
