package com.gridgain.bitset.move;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.Ignition;
import org.apache.ignite.compute.ComputeTaskFuture;
import org.apache.ignite.lang.IgniteRunnable;
import com.gridgain.bitset.model.DeviceKey;

import javax.cache.Cache;

import com.gridgain.bitset.model.Device;
// Cannot import these, must reference them directly by full packaged name
// import com.gridgain.bitset.model_v2.Device;

public class moveDevicesToV2Schema {

    public static void main(String[] args) {

        System.out.println(">>> Bitset-moveDevicesToNewSchema: Bitset Move Device Entries from original schema to new schema...");
        try (Ignite ignite = Ignition.start("Bitset-client.xml")) {

            System.out.println(">>> Bitset-moveDevicesToNewSchema: with started Ignite compute with broadcast...");
            // Broadcast to remote nodes only and enable asynchronous mode.
            IgniteCompute compute = ignite.compute(ignite.cluster().forRemotes()).withAsync();

            // Print out hello message on remote nodes in the cluster group.
            compute.broadcast( () -> 
                {
                    System.out.println(">>> Bitset-moveDevicesToNewSchema: Node Compute: Starting on : " + Ignition.localIgnite().cluster().localNode().id());
                    System.out.println(">>> Bitset-moveDevicesToNewSchema: Node Compute: get DeviceCache..." );
                    IgniteCache<DeviceKey, Device> cache = ignite.getOrCreateCache("DeviceCache");
                    IgniteCache<DeviceKey, com.gridgain.bitset.model_v2.Device> newCache = ignite.getOrCreateCache("DeviceCache");

                    System.out.println(">>> Bitset-moveDevicesToNewSchema: Node Compute: Starting scan query with cursor...");

                    // QueryCursor<IgniteCache.Entry<DeviceKey, Device>> cursor = cache.query(new ScanQuery<>());
                    // System.out.println(">>> Bitset-moveDevicesToNewSchema: Node Compute: cursor: " + cursor.toString() + "\n");
                    //
                    // while (cursor.iterator().hasNext()) {
                    //     System.out.println(">>> Bitset-moveDevicesToNewSchema: Node Compute: cursor.next(): " + cursor.iterator().next());
                    // }

                    System.out.println(">>> Bitset-moveDevicesToNewSchema: Node Compute: Create query...");
                    ScanQuery<DeviceKey, Device> query = new ScanQuery <DeviceKey, Device>();

                    System.out.println(">>> Bitset-moveDevicesToNewSchema: Node Compute: Try scan query with cursor...");
                    try (QueryCursor<Cache.Entry<DeviceKey, Device>> cursor = cache.query(query)) {

                        System.out.println(">>> Bitset-moveDevicesToNewSchema: Node Compute: scan query cursor... ");
                        // Iteration over the local cluster node data using the scan query.
                        int n = 0;
                        for (Cache.Entry<DeviceKey, Device> entry : cursor) {
                            System.out.println(">>> Bitset-moveDevicesToNewSchema: Node Compute: for loop n = " + n++);
                            DeviceKey deviceKey = entry.getKey();
                            Device device = entry.getValue();
                            System.out.println(">>> Bitset-moveDevicesToNewSchema: Node Compute: for loop n; DeviceKey = " + deviceKey.toString() + "; device: " + device.toString());
                            com.gridgain.bitset.model_v2.Device newDevice = new com.gridgain.bitset.model_v2.Device(device);
                            newCache.put(deviceKey, newDevice);
                            System.out.println(">>> Bitset-moveDevicesToNewSchema: Node Compute:                         " + deviceKey.toString() + "; new device: " + newDevice.toString());
                        }
                        System.out.println(">>> Bitset-moveDevicesToNewSchema: Node Compute: for loop complete.");
                    }
                    System.out.println(">>> Bitset-moveDevicesToNewSchema: Node Compute: scan query complete.");

                    System.out.println(">>> Bitset-moveDevicesToNewSchema: Node Compute: Complete on: " + Ignition.localIgnite().cluster().localNode().id());
                }    
            );

            System.out.println(">>> Bitset-moveDevicesToNewSchema: compute get future...");
            ComputeTaskFuture<?> fut = compute.future();

            System.out.println(">>> Bitset-moveDevicesToNewSchema: listen on future...");
            fut.listen(
                f -> System.out.println(">>> Bitset-moveDevicesToNewSchema: Finished sending broadcast job.")
            );

        } catch (Exception e) {
            System.out.println(">>> Bitset-moveDevicesToNewSchema: CAUGHT EXCEPTION!!!!!!! " + e );
        }
    }
}
