package com.geophile.erdo.map.privatemap;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.apiimpl.KeyRange;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapScan;
import com.geophile.erdo.map.OpenOrSealedMapBase;
import com.geophile.erdo.transaction.TimestampSet;
import com.geophile.erdo.transaction.Transaction;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

// PrivateMap is an updatable map that stores updates for a single transaction.

public class PrivateMap extends OpenOrSealedMapBase
{
    // Consolidation.Element interface

    @Override
    public boolean durable()
    {
        return false;
    }

    // TransactionUpdates interface

    public void transactionTimestamp(long timestamp)
    {
        timestamps = new TimestampSet(timestamp);
    }

    // OpenOrSealedMapBase interface

    @Override
    public LazyRecord put(AbstractRecord record, boolean returnReplaced)
    {
        assert record != null;
        AbstractKey key = record.key();
        AbstractRecord replaced = contents.put(key, record);
        estimatedSizeBytes += record.estimatedSizeBytes();
        if (replaced != null) {
            estimatedSizeBytes -= replaced.estimatedSizeBytes();
        }
        return returnReplaced ? replaced : null;
    }

    @Override
    public MapScan scan(KeyRange keyRange)
    {
        return new PrivateMapScan(this, keyRange);
    }

    // SealedMap interface

    @Override
    public long recordCount()
    {
        return contents.size();
    }

    @Override
    public long estimatedSizeBytes()
    {
        return estimatedSizeBytes;
    }

    @Override
    public void loadForConsolidation(MapScan recordScan, MapScan keyScan)
        throws UnsupportedOperationException, IOException, InterruptedException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean keysInMemory()
    {
        return true;
    }

    @Override
    public MapScan keyScan(KeyRange keyRange)
    {
        return new PrivateMapKeyScan(contents, keyRange);
    }

    // PrivateMap interface

    public PrivateMap(Factory factory)
    {
        super(factory);
        Transaction transaction = factory.transactionManager().currentTransaction();
        assert transaction != null;
        transactions.add(transaction);
    }

    // Object state

    SortedMap<AbstractKey, AbstractRecord> contents = new TreeMap<>();
    private long estimatedSizeBytes = 0;
}
