package com.geophile.erdo.transaction;

class WaitsFor
{
    public String toString()
    {
        return String.format("%s -> %s", waiter , owner);
    }
    
    public Transaction waiter()
    {
        return waiter;
    }
    
    public Transaction owner()
    {
        return owner;
    }
    
    public WaitsFor ownerWaitsFor()
    {
        return ownerWaitsFor;
    }
    
    public void ownerWaitsFor(WaitsFor ownerWaitsFor)
    {
        this.ownerWaitsFor = ownerWaitsFor;
    }
    
    public WaitsFor copy()
    {
        return new WaitsFor(waiter,  owner);
    }

    public WaitsFor(Transaction waiter, Transaction owner)
    {
        this.waiter = waiter;
        this.owner = owner;
    }
    
    private final Transaction waiter;
    private final Transaction owner;
    private WaitsFor ownerWaitsFor; 
}
