package com.geophile.erdo.apiimpl;

import com.geophile.erdo.Configuration;
import com.geophile.erdo.forest.ForestRecoveryOnDisk;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.SealedMap;
import com.geophile.erdo.map.arraymap.ArrayMap;
import com.geophile.erdo.map.diskmap.DiskMap;
import com.geophile.erdo.map.diskmap.tree.TreePositionPool;
import com.geophile.erdo.segmentfilemanager.AbstractSegmentFileManager;
import com.geophile.erdo.segmentfilemanager.MeteringSegmentFileManager;
import com.geophile.erdo.segmentfilemanager.ReferenceCountedSegmentFileManager;
import com.geophile.erdo.segmentfilemanager.SegmentFileManager;
import com.geophile.erdo.transaction.LockManager;
import com.geophile.erdo.transaction.TimestampSet;

import java.io.IOException;
import java.util.List;

public class DefaultFactory extends Factory
{
    // Factory interface

    @Override
    public SealedMap newPersistentMap(DatabaseImpl database,
                                      TimestampSet timestamps,
                                      List<SealedMap> obsoleteTrees)
        throws IOException
    {
        return DiskMap.create((DatabaseOnDisk) database, timestamps, obsoleteTrees);
    }

    @Override
    public SealedMap newTransientMap(DatabaseImpl database,
                                     TimestampSet timestamps,
                                     List<SealedMap> obsoleteTrees) throws IOException
    {
        return new ArrayMap(this, timestamps);
    }

    @Override
    public AbstractSegmentFileManager segmentFileManager()
    {
        if (segmentFileManager == null) {
            assert configuration != null;
            // Disk
            SegmentFileManager diskFileSystem = new SegmentFileManager(configuration);
            // Reference-counting for files used in multiple trees
            ReferenceCountedSegmentFileManager segmentFileManager =
                new ReferenceCountedSegmentFileManager(configuration, diskFileSystem);
            // Metering
            meterDiskFileSystem = new MeteringSegmentFileManager(configuration, segmentFileManager);
            this.segmentFileManager = meterDiskFileSystem;
        }
        return segmentFileManager;
    }

    @Override
    public LockManager lockManager()
    {
        return lockManager;
    }

    @Override
    public Class forestRecoveryClass()
    {
        return forestRecoveryClass;
    }

    @Override
    public TreePositionPool threadTreePositionPool()
    {
        return TREE_POSITION_POOL.get();
    }

    // DefaultFactory interface

    public AbstractSegmentFileManager.Stats diskFileSystemStats()
    {
        return meterDiskFileSystem;
    }

    public DefaultFactory(Configuration configuration)
    {
        super(configuration);
        lockManager = new LockManager();
    }

    // Class state

    private static ThreadLocal<TreePositionPool> TREE_POSITION_POOL =
        new ThreadLocal<TreePositionPool>()
        {
            @Override
            protected TreePositionPool initialValue()
            {
                return new TreePositionPool();
            }
        };

    // Object state

    private final LockManager lockManager;
    private Class forestRecoveryClass = ForestRecoveryOnDisk.class;
    private AbstractSegmentFileManager segmentFileManager;
    private MeteringSegmentFileManager meterDiskFileSystem;
}
