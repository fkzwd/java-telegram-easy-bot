package com.vk.dwzkf.tglib.commons.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Roman Shageev
 * @since 01.12.2023
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Slf4j
@Setter
public class Randomizer {
    private final Map<Object, AtomicLong> hitMap = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();
    private Double threshold = 0.1;
    private Double multiplierPerMiss = 0.02;

    public boolean random(Object key) {
        hitMap.computeIfAbsent(key,k -> new AtomicLong(0L));
        double rand = secureRandom.nextDouble();
        double currentThreshold = threshold + (hitMap.get(key).get() * multiplierPerMiss);
        log.info(String.format("Compute random for [%s]. Random: %s, Threshold: %s", key, rand, currentThreshold));
        if (rand <= currentThreshold) {
            hitMap.get(key).set(0L);
            return true;
        }
        hitMap.get(key).incrementAndGet();
        return false;
    }
}
