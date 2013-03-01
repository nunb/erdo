package com.geophile.erdo.examples.helloworld;

import com.geophile.erdo.AbstractKey;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class Name extends AbstractKey
{
    // Object interface

    @Override
    public String toString()
    {
        return name;
    }

    // AbstractKey interface

    @Override
    public int compareTo(AbstractKey that)
    {
        int c = super.compareTo(that);
        if (c == 0) {
            c = name.compareTo(((Name)that).name);
        }
        return c;
    }

    @Override
    public void readFrom(ByteBuffer buffer) throws BufferUnderflowException
    {
        int size = buffer.getInt();
        byte[] bytes = new byte[size];
        buffer.get(bytes);
        name = new String(bytes);
    }

    @Override
    public void writeTo(ByteBuffer buffer) throws BufferOverflowException
    {
        byte[] bytes = name.getBytes();
        buffer.putInt(bytes.length);
        buffer.put(bytes);
    }

    @Override
    public int estimatedSizeBytes()
    {
        return name.length() + 4; // name length
    }

    @Override
    public AbstractKey copy()
    {
        return new Name(name);
    }

    // Name interface

    public Name(String name)
    {
        this.name = name;
    }

    public Name()
    {}

    // Object state

    String name;
}
