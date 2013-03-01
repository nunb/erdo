package com.geophile.erdo.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class LongArray implements Iterable<Long>
{
    // Iterable interface

    public Iterator<Long> iterator()
    {
        return new LongArrayIterator();
    }

    // CompressibleLongArray interface

    public long at(int position)
    {
        try {
            return arrays.get(position / ARRAY_CAPACITY)[position % ARRAY_CAPACITY];
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(String.format("arrays.size: %s, position: %s",
                                                              arrays == null ? null : arrays.size(),
                                                              position));
        }
    }

    public void at(int position, long value)
    {
        try {
            arrays.get(position / ARRAY_CAPACITY)[position % ARRAY_CAPACITY] = value;
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(String.format("arrays.size: %s, position: %s",
                                                              arrays == null ? null : arrays.size(),
                                                              position));
        }
    }

    public void append(long x)
    {
        ensureArray();
        currentArray[currentArrayPosition++] = x;
        count++;
    }

    public int size()
    {
        return count;
    }

    // For use by this class

    private void ensureArray()
    {
        if (currentArrayPosition == ARRAY_CAPACITY) {
            currentArray = null;
        }
        if (currentArray == null) {
            currentArray = new long[ARRAY_CAPACITY];
            arrays.add(currentArray);
            currentArrayPosition = 0;
        }
    }

    // Class state

    private final static int ARRAY_CAPACITY = 1000;

    // Object state

    private final List<long[]> arrays = new ArrayList<long[]>();
    private long[] currentArray; // Last element of arrays, and the arrays array currently being loaded.
    private int currentArrayPosition;
    private int count = 0;

    // Inner classes

    private class LongArrayIterator implements Iterator<Long>
    {
        public boolean hasNext()
        {
            return position < count;
        }

        public Long next()
        {
            Long next;
            if (position >= count) {
                throw new NoSuchElementException();
            }
            next = at(position++);
            return next;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        private int position = 0;
    }
}
