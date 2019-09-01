package ru.otus.lesson9;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander Bryantsev on 08.08.2019.
 */
public class GcStat {

    private Map<String, List<Long>> actionData = new HashMap<>();

    GcStat() {
    }

    Map<String, List<Long>> getActionData() {
        return actionData;
    }


    synchronized void addAction(String actionName, Long duration) {
        if (!actionData.containsKey(actionName)) {
            actionData.put(actionName, new ArrayList<>());
        }
        actionData.get(actionName).add(duration);
    }

}


