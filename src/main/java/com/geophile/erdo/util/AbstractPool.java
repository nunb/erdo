package com.geophile.erdo.util;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;

// A pool of interchangeable resources

public abstract class AbstractPool<RESOURCE>
{
    public final RESOURCE takeResource()
    {
        RESOURCE resource;
        if (resourceList.isEmpty()) {
            resource = newResource();
        } else {
            resource = resourceList.removeFirst();
            RESOURCE removed = resourceSet.remove(resource);
            assert removed == resource : resource;
        }
        activate(resource);
        return resource;
    }

    public final void returnResource(RESOURCE resource)
    {
        deactivate(resource);
        resourceList.addLast(resource);
        RESOURCE replaced = resourceSet.add(resource);
        assert replaced == null : resource;
    }

    public final Collection<RESOURCE> resources()
    {
        return exportableList;
    }

    public abstract RESOURCE newResource();

    public void activate(RESOURCE resource)
    {}

    public void deactivate(RESOURCE resource)
    {}

    private final Deque<RESOURCE> resourceList = new ArrayDeque<>();
    private final Collection<RESOURCE> exportableList = Collections.unmodifiableCollection(resourceList);
    // To check uniqueness
    private final IdentitySet<RESOURCE> resourceSet = new IdentitySet<>();
}
