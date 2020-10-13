package com.gridgain.bitset.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import com.gridgain.bitset.model.Customer;
import com.gridgain.bitset.model.CustomerExternal;
import com.gridgain.bitset.model.CustomerInternal;

import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.cache.QueryIndexType;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

/** This file was generated by Ignite Web Console (09/08/2020, 16:10) **/
public class ServerConfigurationFactory {
    /**
     * Configure grid.
     * 
     * @return Ignite configuration.
     * @throws Exception If failed to construct Ignite configuration instance.
     **/
    public static IgniteConfiguration createConfiguration() throws Exception {
        IgniteConfiguration cfg = new IgniteConfiguration();

        cfg.setIgniteInstanceName("Bitset");

        TcpDiscoverySpi discovery = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Arrays.asList("127.0.0.1:47500..47510"));
        discovery.setIpFinder(ipFinder);
        cfg.setDiscoverySpi(discovery);

        cfg.setClientConnectorConfiguration(new ClientConnectorConfiguration());

        DataStorageConfiguration dataStorageCfg = new DataStorageConfiguration();
        DataRegionConfiguration dataRegionCfg = new DataRegionConfiguration();
        dataRegionCfg.setMetricsEnabled(true);
        dataRegionCfg.setPersistenceEnabled(true);
        dataStorageCfg.setDefaultDataRegionConfiguration(dataRegionCfg);
        dataStorageCfg.setStoragePath("db_bitset");
        cfg.setDataStorageConfiguration(dataStorageCfg);

        cfg.setCacheConfiguration(cacheDeviceCache());
        cfg.setCacheConfiguration(cacheCustomerCache());

        return cfg;
    }

    /**
     * Create configuration for cache "DeviceCache".
     * 
     * @return Configured cache.
     **/
    public static CacheConfiguration cacheDeviceCache() {
        CacheConfiguration ccfg = new CacheConfiguration();

        ccfg.setName("DeviceCache");
        ccfg.setCacheMode(CacheMode.PARTITIONED);
        ccfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        ccfg.setBackups(1);
        ccfg.setReadFromBackup(true);
        ccfg.setCopyOnRead(true);
        ccfg.setSqlSchema("DEVICE");
        ccfg.setEagerTtl(true);
        ccfg.setStatisticsEnabled(true);
        ccfg.setManagementEnabled(true);

        ArrayList<QueryEntity> qryEntities = new ArrayList<>();

        QueryEntity qryEntity = new QueryEntity();
        qryEntity.setKeyType("com.gridgain.bitset.model.DeviceKey");
        qryEntity.setValueType("com.gridgain.bitset.model.Device");

        HashSet<String> keyFields = new HashSet<>();
        keyFields.add("id");
        qryEntity.setKeyFields(keyFields);

        LinkedHashMap<String, String> fields = new LinkedHashMap<>();
        fields.put("id", "java.lang.Integer");
        fields.put("updatets", "java.sql.Timestamp");
        fields.put("demographics", "[B");
        fields.put("content", "[B");
        qryEntity.setFields(fields);

        HashSet<String> notNullFields = new HashSet<>();
        notNullFields.add("id");
        notNullFields.add("updatets");
        qryEntity.setNotNullFields(notNullFields);

        HashMap<String, Object> defaultFieldValues = new HashMap<>();
        defaultFieldValues.put("id", 0);
        defaultFieldValues.put("updatets", System.currentTimeMillis());
        qryEntity.setDefaultFieldValues(defaultFieldValues);

        ArrayList<QueryIndex> indexes = new ArrayList<>();
        QueryIndex index = new QueryIndex();
        index.setName("device_Demographics_IDX");
        index.setIndexType(QueryIndexType.SORTED);
        LinkedHashMap<String, Boolean> indFlds = new LinkedHashMap<>();
        indFlds.put("demographics", false);
        index.setFields(indFlds);
        indexes.add(index);

        index = new QueryIndex();
        index.setName("device_UpdateTS_IDX");
        index.setIndexType(QueryIndexType.SORTED);
        indFlds = new LinkedHashMap<>();
        indFlds.put("updatets", false);
        index.setFields(indFlds);
        indexes.add(index);

        qryEntity.setIndexes(indexes);
        qryEntities.add(qryEntity);

        ccfg.setQueryEntities(qryEntities);
        ccfg.setCopyOnRead(false); // Performance recommendation is to NOT return a copy of the value, but actually return the value object

        return ccfg;
    }

    /**
     * Create configuration for cache "CustomerCache".
     * 
     * @return Configured cache.
     **/
    public static CacheConfiguration cacheCustomerCache() {
        // Preparing configuration.
        CacheConfiguration<Long, Customer> ccfg = new CacheConfiguration<>();

        ccfg.setName("CustomerCache");
        ccfg.setCacheMode(CacheMode.PARTITIONED);
        ccfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        ccfg.setBackups(1);
        ccfg.setReadFromBackup(true);
        ccfg.setCopyOnRead(true);
        ccfg.setSqlSchema("DEVICE");
        ccfg.setEagerTtl(true);
        ccfg.setStatisticsEnabled(true);
        ccfg.setManagementEnabled(true);

        // Registering indexed type.
        ccfg.setIndexedTypes(
            Long.class, Customer.class,
            Long.class, CustomerInternal.class,
            Long.class, CustomerExternal.class 
        );

        return ccfg;
    }
}