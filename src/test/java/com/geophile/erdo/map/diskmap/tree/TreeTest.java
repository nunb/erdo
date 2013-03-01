package com.geophile.erdo.map.diskmap.tree;

import com.geophile.erdo.TestFactory;
import com.geophile.erdo.TestKey;
import com.geophile.erdo.TestRecord;
import com.geophile.erdo.TransactionCallback;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapScan;
import com.geophile.erdo.map.RecordFactory;
import com.geophile.erdo.map.diskmap.DBStructure;
import com.geophile.erdo.transaction.Transaction;
import com.geophile.erdo.util.FileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class TreeTest
{
    @BeforeClass
    public static void beforeClass() throws IOException
    {
        TestKey.testErdoId(ERDO_ID);
        DB_STRUCTURE = new DBStructure(new File("/tmp/erdo"));
        Transaction.initialize(FACTORY);
    }

    @Before
    public void before() throws IOException
    {
        FACTORY.recordFactory(ERDO_ID, new RecordFactory(TestKey.class, TestRecord.class));
        FileUtil.deleteDirectory(DB_STRUCTURE.dbDirectory());
        FileUtil.ensureDirectoryExists(DB_STRUCTURE.dbDirectory());
        FileUtil.ensureDirectoryExists(DB_STRUCTURE.forestDirectory());
        FileUtil.ensureDirectoryExists(DB_STRUCTURE.segmentsDirectory());
        FileUtil.ensureDirectoryExists(DB_STRUCTURE.summariesDirectory());
    }
    
    @After
    public void after()
    {
        FACTORY.reset();
    }

    @Test
    public void testScanEmpty() throws Exception
    {
        WriteableTree writeableTree = Tree.create(FACTORY, DB_STRUCTURE, TREE_ID);
        Tree tree = writeableTree.close();
        startTransaction();
        MapScan scan = tree.scan(null);
        assertNull(scan.next());
    }

    // This tests tree scanning. Tree random access is tested indirectly, in SealedMapTest,
    // operating on a DiskMap.
    @Test
    public void testScanRecords() throws Exception
    {
        final int N = 1000;
        WriteableTree writeableTree = Tree.create(FACTORY, DB_STRUCTURE, TREE_ID);
        for (int i = 0; i < N; i++) {
            startTransaction();
            TestRecord record = TestRecord.createRecord(i, VALUES[i % 10]);
            record.key().transaction(FACTORY.transactionManager().currentTransaction());
            commitTransaction();
            writeableTree.append(record);
        }
        Tree tree = writeableTree.close();
        startTransaction();
        MapScan scan = tree.scan(null);
        int expected = 0;
        LazyRecord lazyRecord;
        while ((lazyRecord = scan.next()) != null) {
            TestRecord record = (TestRecord) lazyRecord.materializeRecord();
            int key = ((TestKey) record.key()).key();
            assertEquals(expected, key);
            assertEquals(VALUES[key % 10], record.stringValue());
            expected++;
        }
        commitTransaction();
        assertEquals(N, expected);
    }
    
    private void startTransaction()
    {
        FACTORY.transactionManager().currentTransaction();
    }
    
    private void commitTransaction() throws IOException, InterruptedException
    {
        FACTORY.transactionManager().commitTransaction(TransactionCallback.DO_NOTHING, null);
    }

    private static final int ERDO_ID = 1;
    private static DBStructure DB_STRUCTURE;
    private static final int TREE_ID = 0;
    private static final String[] VALUES = {
        "",
        "a",
        "ab",
        "abc",
        "abcd",
        "abcde",
        "abcdef",
        "abcdefg",
        "abcdefgh",
        "abcdefghi"
    };
    private static TestFactory FACTORY = new TestFactory();
}
