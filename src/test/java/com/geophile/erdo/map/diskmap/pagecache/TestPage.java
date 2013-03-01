package com.geophile.erdo.map.diskmap.pagecache;

import com.geophile.erdo.immutableitemcache.CacheEntry;

import java.nio.ByteBuffer;

class TestPage extends CacheEntry<TestId, TestPage>
{
    // Object interface

    @Override
    public String toString()
    {
        return String.format("page(%s)%s", id.value(), okToEvict ? "*" : "");
    }

    // CacheEntry interface

    @Override
    public TestId id()
    {
        return id;
    }

    @Override
    public TestPage item()
    {
        return this;
    }

    @Override
    public boolean okToEvict()
    {
        return okToEvict;
    }

    @Override
    public int referenceCount()
    {
        return 1;
    }

    // TestPage interface

    public ByteBuffer buffer()
    {
        return buffer;
    }

    public void okToEvict(boolean okToEvict)
    {
        this.okToEvict = okToEvict;
    }

    public TestPage(TestId id, ByteBuffer buffer)
    {
        this.id = id;
        this.buffer = buffer;
    }

    // Object state

    private final TestId id;
    private ByteBuffer buffer;
    private boolean okToEvict = false;
}
