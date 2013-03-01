package com.geophile.erdo.consolidate;

import java.util.List;

import static com.geophile.erdo.consolidate.Consolidation.Element;

public class AllNonDurableConsolidationPlanner extends ConsolidationPlanner
{
    // ConsolidationPlanner interface

    @Override
    public boolean planConsolidation(Element newElement)
    {
        assert newElement == null : newElement;
        consolidationElements = consolidationSet.nonDurable().availableForConsolidation();
        long totalCount = 0;
        for (Element element : consolidationElements) {
            totalCount += element.count();
        }
        if (totalCount == 0) {
            consolidationElements = null;
        }
        return consolidationElements != null;
    }

    @Override
    public List<Element> elementsToConsolidate()
    {
        return consolidationElements;
    }

    @Override
    public String type()
    {
        return "allNonDurable";
    }

    // AllNonDurableConsolidationPlanner interface

    public static AllNonDurableConsolidationPlanner newPlanner(ConsolidationSet consolidationSet)
    {
        return new AllNonDurableConsolidationPlanner(consolidationSet);
    }

    // For use by this class

    private AllNonDurableConsolidationPlanner(ConsolidationSet consolidationSet)
    {
        super(consolidationSet, false, true);
    }

    // Object state

    private List<Element> consolidationElements;
}
