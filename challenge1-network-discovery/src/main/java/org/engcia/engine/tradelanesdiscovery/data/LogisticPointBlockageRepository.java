package org.engcia.engine.tradelanesdiscovery.data;

import org.engcia.engine.tradelanesdiscovery.model.LogisticPoint;
import org.engcia.engine.tradelanesdiscovery.model.LogisticPointBlockage;

import java.util.*;


public class LogisticPointBlockageRepository {
    private static final List<LogisticPointBlockage> list = new ArrayList(); // ideally it should be replaced by some persistent mecanism

    public boolean isLogisticPointBlocked(LogisticPoint point) {
        for (LogisticPointBlockage blockage : list)
        {
            if (blockage.getLogisticPoint().equals(point))
                return true;
        }
        return false;
    }

    public void addBlockage(LogisticPointBlockage blockage) {
        list.add(blockage);
    }

    public void removeBlockage(LogisticPoint logisticPoint) {
        list.removeIf(b -> b.getLogisticPoint().equals(logisticPoint));
    }
}


