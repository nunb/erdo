package com.geophile.erdo.map.mergescan;

import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.map.MapScan;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TestMultiRecord extends AbstractMultiRecord
{
    // Transferrable interface

    @Override
    public void writeTo(ByteBuffer buffer)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readFrom(ByteBuffer buffer)
    {
        throw new UnsupportedOperationException();
    }

    // AbstractRecord interface

    @Override
    public long estimatedSizeBytes()
    {
        return 0;
    }

    // AbstractMultiRecord interface

    public void append(AbstractRecord record)
    {
        records.add(record);
    }

    public MapScan scan()
    {
        return new IteratorScan(records.iterator());
    }

    // TestMultiRecord interface

    List<AbstractRecord> records()
    {
        return records;
    }

    TestMultiRecord(MultiRecordKey multiRecordKey)
    {
        super(multiRecordKey);
    }

    // Object state

    private final List<AbstractRecord> records = new ArrayList<AbstractRecord>();
}
