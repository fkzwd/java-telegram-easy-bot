package com.vk.dwzkf.tglib.commons.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Roman Shageev
 * @since 29.12.2024
 */
@RequiredArgsConstructor
@Slf4j
public class RateLimitCounter {
    private final int limit;
    private final TimeUnit unit;
    private final int window;
    private AtomicInteger current = new AtomicInteger(0);
    private volatile long windowStart = -1;


    public synchronized void execute(Runnable action) {
        if (wouldLimitExceed()) {
            await();
        }
        action.run();
        inc();
    }

    public void inc() {
        if (windowStart == -1) {
            log.info("Initialize window start.");
            windowStart = System.currentTimeMillis();
        }
        log.info("Current count: {}, limit: {}, window: {} ms, elapsed: {} ms",
                current.get(),
                limit,
                unit.toMillis(window),
                getTimeElapsed()
        );
        if (getTimeElapsed() > getWindowMillis()) {
            log.info("Reset window start. Elapsed time: {} ms", getTimeElapsed());
            windowStart = System.currentTimeMillis();
            current.set(1);
            return;
        }
        current.incrementAndGet();
    }

    public boolean wouldLimitExceed() {
        if (windowStart == -1) {
            return false;
        }
        long timeElapsed = getTimeElapsed();
        if (timeElapsed <= getWindowMillis() && current.get() + 1 > limit) {
            return true;
        }
        return false;
    }

    private long getWindowMillis() {
        return unit.toMillis(window);
    }

    private long getTimeElapsed() {
        return System.currentTimeMillis() - windowStart;
    }

    public void await() {
        try {
            if (windowStart == -1) {
                return;
            }
            long sleepTime = Math.max(getWindowMillis() - getTimeElapsed(), 0);
            log.info("Await for {} ms", sleepTime);
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
