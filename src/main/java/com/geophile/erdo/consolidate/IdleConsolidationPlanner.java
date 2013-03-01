package com.geophile.erdo.consolidate;

import java.util.List;

import static com.geophile.erdo.consolidate.Consolidation.Element;

class IdleConsolidationPlanner extends ConsolidationPlanner
{
    // ConsolidationPlanner interface

    @Override
    public boolean planConsolidation(Element newElement)
    {
        assert newElement == null;
        return consolidationSet.idle() && fractalConsolidationPlanner.planConsolidation(null);
    }

    @Override
    public List<Element> elementsToConsolidate()
    {
        return fractalConsolidationPlanner.elementsToConsolidate();
    }

    @Override
    public String type()
    {
        return "idle";
    }

    // IdleConsolidationPlanner interface

    public static IdleConsolidationPlanner newPlanner(ConsolidationSet consolidationSet,
                                                      int minConsolidationMaps,
                                                      int minConsolidationSize)
    {
        return new IdleConsolidationPlanner(consolidationSet, minConsolidationMaps, minConsolidationSize);
    }

    // For use by this class

    private IdleConsolidationPlanner(ConsolidationSet consolidationSet,
                                     int minConsolidationMaps,
                                     int minConsolidationSize)
    {
        super(consolidationSet, true, true);
        this.fractalConsolidationPlanner =
            FractalConsolidationPlanner.durable(consolidationSet, minConsolidationMaps, minConsolidationSize);
    }

    // Object state

    private final FractalConsolidationPlanner fractalConsolidationPlanner;
}
