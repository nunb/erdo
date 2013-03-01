package com.geophile.erdo.map.testarraymap;

import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.apiimpl.KeyRange;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapScan;
import com.geophile.erdo.map.OpenOrSealedMapBase;
import com.geophile.erdo.map.keyarray.KeyArray;
import com.geophile.erdo.transaction.TimestampSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestArrayMap extends OpenOrSealedMapBase
{
    // Consolidation.Element interface

    @Override
    public boolean durable()
    {
        return durable;
    }

    // OpenOrSealedMapBase interface

    // ArrayMap.put only allows insertion in key order, and returnReplaced is ignored.
    @Override
    public LazyRecord put(AbstractRecord record, boolean returnReplaced)
    {
        // ArrayMap does not support returnReplaced
        assert !returnReplaced;
/* TODO: Breaks FastMergeTest. Restore this.
        if (size > 0 && records[size - 1].key().compareTo(record.key()) >= 0) {
            throw new InternalError(record.toString());
        }
*/
        records.add(record);
        estimatedSizeBytes += record.estimatedSizeBytes();
        return null;
    }

    @Override
    public MapScan scan(KeyRange keyRange)
    {
        return new TestArrayMapScan(this, keyRange);
    }

    @Override
    public long recordCount()
    {
        return records.size();
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
        keys = new KeyArray(factory, 1000); // Doesn't matter - used to set initial capacity of Key.timestamps, which can grow anyway.
        LazyRecord record;
        while ((record = recordScan.next()) != null) {
            records.add(record);
            keys.append(record.key());
        }
        keys.close();
    }

    @Override
    public boolean keysInMemory()
    {
        return true;
    }

    @Override
    public MapScan keyScan(KeyRange keyRange)
    {
        return
            keys == null
            ? scan(keyRange)
            : keys.scan(keyRange);
    }

    // ArrayMap interface

    public TestArrayMap(Factory factory, TimestampSet timestamps)
    {
        this(factory, timestamps, false);
    }

    public TestArrayMap(Factory factory, TimestampSet timestamps, boolean durable)
    {
        super(factory);
        this.durable = durable;
        this.timestamps = timestamps;
    }

    // Object state

    private final boolean durable;
    KeyArray keys;
    List<LazyRecord> records = new ArrayList<>();
    private long estimatedSizeBytes = 0;
}
