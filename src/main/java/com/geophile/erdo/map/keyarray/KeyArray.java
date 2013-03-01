package com.geophile.erdo.map.keyarray;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.apiimpl.KeyRange;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.diskmap.CompressibleLongArray;
import com.geophile.erdo.memorymonitor.MemoryMonitor;
import com.geophile.erdo.memorymonitor.MemoryTracker;
import com.geophile.erdo.util.ErdoIdArray;
import com.geophile.erdo.util.PackedArray;

public class KeyArray
{
    public void append(AbstractKey key)
    {
        assert !closed;
        erdoIds.append(key.erdoId());
        timestamps.append(key.transactionTimestamp());
        array.object().append(key);
    }

    public int size()
    {
        return array.object().size();
    }

    public KeyArrayScan scan(KeyRange keyRange)
    {
        assert closed;
        return new KeyArrayScan(this, keyRange);
    }

    public void close()
    {
        timestamps.close();
        closed = true;
    }

    public void destroy()
    {
        array.object(null);
    }

    // Adapted from java.util.Arrays.binarySearch0
    public int binarySearch(AbstractKey key)
    {
        int low = 0;
        int high = array.object().size() - 1;
        AbstractKey midKey = null;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            midKey = key(mid, midKey);
            int c = midKey.compareTo(key);
            if (c < 0) {
                low = mid + 1;
            } else if (c > 0) {
                high = mid - 1;
            } else {
                return mid; // key found
            }
        }
        return -(low + 1);  // key not found.
    }

    public KeyArray(Factory factory, int initialCapacity)
    {
        this.factory = factory;
        this.erdoIds = new ErdoIdArray();
        this.timestamps = new CompressibleLongArray(initialCapacity);
        MemoryTracker<PackedArray> arrayMemoryTracker =
            new MemoryTracker<>(factory.memoryMonitor(), MemoryMonitor.KEY_ARRAY_KEYS);
        arrayMemoryTracker.object(new PackedArray(arrayMemoryTracker));
        this.array = arrayMemoryTracker;
    }

    // For use by this package

    AbstractKey key(int position, AbstractKey key)
    {
        assert closed;
        int erdoId = erdoIds.at(position);
        if (key == null || key.erdoId() != erdoId) {
            key = factory.recordFactory(erdoId).newKey();
        }
        key.clearTransactionState();
        key.erdoId(erdoId);
        key.transactionTimestamp(timestamps.at(position));
        array.object().at(position, key);
        return key;
    }

    // Object state

    private final Factory factory;
    private final ErdoIdArray erdoIds;
    private final CompressibleLongArray timestamps;
    private final MemoryTracker<PackedArray> array;
    private boolean closed = false;
}
