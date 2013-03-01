package com.geophile.erdo.map.keyarray;

import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.TestFactory;
import com.geophile.erdo.TestKey;
import com.geophile.erdo.map.RecordFactory;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.Assert.*;

public class KeyArrayTest
{
    @BeforeClass
    public static void beforeClass()
    {
        FACTORY = new TestFactory();
        FACTORY.recordFactory(1, new RecordFactory(TestKey1.class, null));
        FACTORY.recordFactory(2, new RecordFactory(TestKey2.class, null));
    }

    @After
    public void after()
    {
        FACTORY.reset();
    }

    @Test
    public void testEmpty()
    {
        KeyArray a = new KeyArray(FACTORY, 1000);
        a.close();
        TestKey1 key = new TestKey1();
        // size
        assertEquals(0, a.size());
        // scan
        assertNull(a.scan(null).next());
        // binary search
        assertEquals(-1, a.binarySearch(key));
        // subscript
        try {
            a.key(0, key);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testNonEmpty()
    {
        final int MAX_N = 100;
        for (int n = 1; n <= MAX_N; n++) {
            KeyArray a = new KeyArray(FACTORY, 1000);
            TestKey1 key = null;
            for (int i = 1; i <= n; i++) {
                key = key1(i * 10);
                a.append(key);
            }
            a.close();
            // size
            assertEquals(n, a.size());
            // scan
            KeyArrayScan scan = a.scan(null);
            AbstractRecord record;
            int expected = 10;
            while ((record = scan.next()) != null) {
                assertEquals(expected, ((TestKey1)record.key()).key());
                expected += 10;
            }
            // binary search
            assertEquals(-1, a.binarySearch(key1(5)));
            expected = 0;
            for (int k = 10; k <= n; k += 10) {
                assertEquals(expected++, a.binarySearch(key1(k)));
            }
            expected = -2;
            for (int k = 15; k <= n + 5; k += 10) {
                assertEquals(expected--, a.binarySearch(key1(k)));
            }
            // subscript
            expected = 10;
            for (int i = 0; i < a.size(); i++) {
                a.key(i, key);
                assertEquals(expected, key.key());
                expected += 10;
            }
        }
    }
    
    @Test
    public void testMixed()
    {
        final int N = 100;
        for (int n1 = 1; n1 < N; n1++) {
            int n2 = N - n1;
            KeyArray a = new KeyArray(FACTORY, 1000);
            for (int i = 0; i < n1; i++) {
                a.append(key1(i));
            }
            for (int i = 0; i < n2; i++) {
                a.append(key2(i));
            }
            a.close();
            // size
            assertEquals(N, a.size());
            // scan
            KeyArrayScan scan = a.scan(null);
            AbstractRecord record;
            int expected = 0;
            while ((record = scan.next()) != null) {
                if (expected < n1) {
                    assertEquals(expected, ((TestKey)record.key()).key());
                } else {
                    assertEquals(expected - n1, ((TestKey)record.key()).key());
                }
                expected++;
            }
            // binary search
            for (int i = 0; i < n1; i++) {
                assertEquals(i, a.binarySearch(key1(i)));
            }
            for (int i = 0; i < n2; i++) {
                assertEquals(n1 + i, a.binarySearch(key2(i)));
            }
            // subscript
            int i = 0;
            TestKey1 key1 = key1(-1);
            while (i < n1) {
                a.key(i, key1);
                assertEquals(1, key1.erdoId());
                assertEquals(i, key1.key());
                i++;
            }
            TestKey2 key2 = key2(-1);
            while (i < N) {
                a.key(i, key2);
                assertEquals(2, key2.erdoId());
                assertEquals(i - n1, key2.key());
                i++;
            }
        }
    }

    private TestKey1 key1(int k)
    {
        TestKey1 key = new TestKey1();
        key.key(k);
        key.erdoId(1);
        key.transactionTimestamp(100);
        return key;
    }

    private TestKey2 key2(int k)
    {
        TestKey2 key = new TestKey2();
        key.key(k);
        key.erdoId(2);
        key.transactionTimestamp(200);
        return key;
    }

    private static TestFactory FACTORY;

    public static class TestKey1 extends TestKey
    {
        public TestKey1()
        {
            erdoId(1);
        }
    }

    public static class TestKey2 extends TestKey
    {
        public TestKey2()
        {
            erdoId(2);
        }
    }
}
