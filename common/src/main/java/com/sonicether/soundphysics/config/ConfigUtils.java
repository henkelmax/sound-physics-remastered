package com.sonicether.soundphysics.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigUtils {

    public static <T extends Comparable<T>, U> Map<T, U> sortMap(Map<T, U> map) {
        List<Map.Entry<T, U>> entryList = new ArrayList<>(map.entrySet());
        entryList.sort(Map.Entry.comparingByKey());

        LinkedHashMap<T, U> sorted = new LinkedHashMap<>();
        for (Map.Entry<T, U> entry : entryList) {
            sorted.put(entry.getKey(), entry.getValue());
        }

        return sorted;
    }

}
