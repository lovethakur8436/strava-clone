package com.fitness.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class DataSourceRouter extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        // If the developer used @Transactional(readOnly = true), route to REPLICA
        if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            return "REPLICA";
        }
        // Otherwise, route to PRIMARY for writes
        return "PRIMARY";
    }
}