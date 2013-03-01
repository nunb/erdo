package com.geophile.erdo.map;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.DeadlockException;
import com.geophile.erdo.TransactionRolledBackException;

public abstract class MapBase implements Comparable<MapBase>, CommonMapOperations
{
    // Object interface

    @Override
    public int hashCode()
    {
        return ((int) (mapId >>> 32)) ^ (int) mapId;
    }

    @Override
    public boolean equals(Object o)
    {
        return mapId == ((MapBase) o).mapId;
    }

    @Override
    public String toString()
    {
        return String.format("%s#%s", getClass().getSimpleName(), mapId);
    }

    // Comparable interface

    public int compareTo(MapBase that)
    {
        long thisMapId = this.mapId;
        long thatMapId = that.mapId;
        return thisMapId == thatMapId ? 0 : thisMapId < thatMapId ? -1 : 1;
    }

    // CommonMapOperations interface

    public void lock(AbstractKey key) throws InterruptedException, DeadlockException, TransactionRolledBackException
    {
        throw new UnsupportedOperationException();
    }

    public final Factory factory()
    {
        return factory;
    }

    public final long mapId()
    {
        return mapId;
    }

    public boolean isWriteable()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isSealed()
    {
        throw new UnsupportedOperationException();
    }

    // MapBase interface

    protected MapBase(Factory factory)
    {
        this(factory, factory.newMapId());
    }

    protected MapBase(Factory factory, long mapId)
    {
        this.factory = factory;
        this.mapId = mapId;
    }

    // Object state

    protected final Factory factory;
    protected final long mapId;
}
