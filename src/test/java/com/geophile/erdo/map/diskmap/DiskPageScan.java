package com.geophile.erdo.map.diskmap;

import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.map.MapScan;

import java.io.IOException;

class DiskPageScan extends MapScan
{
    @Override
    public AbstractRecord next() throws IOException, InterruptedException
    {
        AbstractRecord next = null;
        if (page != null && recordNumber < page.nRecords()) {
            next = page.readRecord(recordNumber, pageAccessBuffers);
            recordNumber++;
        }
        return next;
    }

    @Override
    public void close()
    {
        page = null;
        pageAccessBuffers = null;
    }

    DiskPageScan(DiskPage page)
    {
        this.page = page;
        this.pageAccessBuffers = page.accessBuffers();
        this.recordNumber = 0;
    }

    private DiskPage page;
    private DiskPage.AccessBuffers pageAccessBuffers;
    private int recordNumber;
}
