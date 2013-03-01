package com.geophile.erdo.map;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.Configuration;
import com.geophile.erdo.apiimpl.DatabaseImpl;
import com.geophile.erdo.map.diskmap.DiskPageCache;
import com.geophile.erdo.map.diskmap.tree.TreePositionPool;
import com.geophile.erdo.memorymonitor.MemoryMonitor;
import com.geophile.erdo.segmentfilemanager.AbstractSegmentFileManager;
import com.geophile.erdo.segmentfilemanager.pagememorymanager.PageMemoryManager;
import com.geophile.erdo.segmentfilemanager.pagememorymanager.SubAllocatingPageMemoryManager;
import com.geophile.erdo.transaction.LockManager;
import com.geophile.erdo.transaction.TimestampSet;
import com.geophile.erdo.transaction.TransactionManager;
import com.geophile.erdo.util.IdGenerator;

import java.io.IOException;
import java.util.List;

public abstract class Factory
{
    public final void registerKeyAndValueClasses(int erdoId, String keyClassName, String recordClassName)
    {
        recordFactories.put(erdoId, new RecordFactory(keyClassName, recordClassName));
    }

    public final void registerKeyAndValueClasses(int erdoId,
                                                 Class<? extends AbstractKey> keyClass,
                                                 Class<? extends AbstractRecord<? extends AbstractKey>> recordClass)
    {
        recordFactories.put(erdoId, new RecordFactory(keyClass, recordClass));
    }

    public final RecordFactory recordFactory(int erdoId)
    {
        return recordFactories.get(erdoId);
    }

    public final long nextTransactionTime()
    {
        return transactionTimeGenerator.nextId();
    }

    public final long nextTransactionTimestamp()
    {
        return transactionTimestampGenerator.nextId();
    }

    public final long newMapId()
    {
        return mapIdGenerator.nextId();
    }

    public abstract SealedMap newPersistentMap(DatabaseImpl database,
                                               TimestampSet timestamps,
                                               List<SealedMap> obsoleteTrees) throws IOException;

    public abstract SealedMap newTransientMap(DatabaseImpl database,
                                              TimestampSet timestamps,
                                              List<SealedMap> obsoleteTrees) throws IOException;

    public abstract AbstractSegmentFileManager segmentFileManager();

    public final MemoryMonitor memoryMonitor()
    {
        return memoryMonitor;
    }

    public final DiskPageCache diskPageCache()
    {
        return diskPageCache;
    }

    public abstract Class forestRecoveryClass();

    public final Configuration configuration()
    {
        return configuration;
    }

    public abstract LockManager lockManager();

    public final void transactionManager(TransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }

    public final TransactionManager transactionManager()
    {
        return transactionManager;
    }

    public final PageMemoryManager pageMemoryManager()
    {
        return pageMemoryManager;
    }

    public final void restoreTransactionTimestampGenerator(long timestamp)
    {
        transactionTimestampGenerator.restore(timestamp);
    }

    public final void restoreMapIdGenerator(long mapId)
    {
        mapIdGenerator.restore(mapId);
    }

    public TreePositionPool threadTreePositionPool()
    {
        throw new UnsupportedOperationException();
    }

    protected Factory(Configuration configuration)
    {
        synchronized (getClass()) {
            assert !allocatedOnce;
            allocatedOnce = true;
        }
        this.memoryMonitor = new MemoryMonitor();
        this.configuration =  configuration;
        this.pageMemoryManager = new SubAllocatingPageMemoryManager(configuration);
        this.diskPageCache = new DiskPageCache(configuration);
    }

    // Class state

    protected static volatile boolean allocatedOnce = false;

    // Object state

    protected final Configuration configuration;
    protected final java.util.Map<Integer, RecordFactory> recordFactories = new java.util.HashMap<>();
    protected final MemoryMonitor memoryMonitor;
    protected TransactionManager transactionManager;
    private final IdGenerator mapIdGenerator = new IdGenerator();
    protected final IdGenerator transactionTimeGenerator = new IdGenerator();
    protected final IdGenerator transactionTimestampGenerator = new IdGenerator();
    protected final DiskPageCache diskPageCache;
    protected final PageMemoryManager pageMemoryManager;
}
