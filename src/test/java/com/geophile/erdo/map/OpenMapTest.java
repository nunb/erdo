package com.geophile.erdo.map;

import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.DeadlockException;
import com.geophile.erdo.TestRecord;
import com.geophile.erdo.TransactionRolledBackException;
import com.geophile.erdo.map.privatemap.PrivateMap;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;

public class OpenMapTest extends MapBehaviorTestBase
{
    @Test
    public void testPut()
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        testPut(false);
        testPut(true);
    }

    private void testPut(boolean returnReplaced)
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        for (int n = 0; n <= N_MAX; n++) {
            // Populate map with keys 0, ..., n - 1
            TestRecord replaced;
            OpenMap map = newMap();
            for (int key = 0; key < n; key++) {
                replaced = (TestRecord) map.put(newRecord(key, "initial"), returnReplaced);
                Assert.assertNull(replaced);
            }
            AbstractRecord record;
            LazyRecord lazyRecord;
            int expectedKey;
            String expectedValue;
            // Update every key and check records after each update
            for (int key = 0; key < n; key++) {
                replaced = (TestRecord) map.put(newRecord(key, "update"), returnReplaced);
                if (returnReplaced) {
                    Assert.assertEquals(key, key(replaced));
                    Assert.assertEquals("initial", replaced.stringValue());
                } else {
                    Assert.assertNull(replaced);
                }
                expectedKey = 0;
                expectedValue = "update";
                MapScan scan = map.scan(null);
                while ((lazyRecord = scan.next()) != null) {
                    record = lazyRecord.materializeRecord();
                    Assert.assertEquals(expectedKey, key(record));
                    Assert.assertEquals(expectedValue, ((TestRecord) record).stringValue());
                    if (expectedKey == key) {
                        expectedValue = "initial";
                    }
                    expectedKey++;
                }
                Assert.assertEquals(n, expectedKey);
            }
            // Delete every key and check records after each deletion
            expectedValue = "update";
            for (int key = 0; key < n; key++) {
                replaced = (TestRecord) map.put(newDeletedRecord(key), returnReplaced);
                if (returnReplaced) {
                    Assert.assertEquals(key, key(replaced));
                    Assert.assertEquals("update", replaced.stringValue());
                } else {
                    Assert.assertNull(replaced);
                }
                expectedKey = 0;
                MapScan scan = map.scan(null);
                while ((lazyRecord = scan.next()) != null) {
                    record = lazyRecord.materializeRecord();
                    Assert.assertEquals(expectedKey, key(record));
                    if (expectedKey <= key) {
                        Assert.assertTrue(record.deleted());
                    } else {
                        Assert.assertEquals(expectedValue, ((TestRecord) record).stringValue());
                    }
                    expectedKey++;
                }
                Assert.assertEquals(n, expectedKey);
            }
        }
    }

    private OpenMap newMap()
    {
        return new PrivateMap(FACTORY);
    }

    private static final int N_MAX = 100;
}
