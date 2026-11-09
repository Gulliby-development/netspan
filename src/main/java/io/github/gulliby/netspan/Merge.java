package io.github.gulliby.netspan;

import io.github.gulliby.netspan.Api.Net;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** IPv4 coverage union and its minimal CIDR decomposition. */
final class Merge {
    private Merge() {
    }

    /** An inclusive interval of IPv4 addresses. */
    record Interval(long first, long last) {
    }

    /** A CIDR block represented by its first address and prefix length. */
    record Block(long first, int len) {
    }

    /**
     * Returns the sorted maximal inclusive intervals covered by {@code items}.
     * Items must contain validated, aligned IPv4 networks. The supplied list is
     * not modified.
     */
    static List<Interval> intervals(List<Net> items) {
        List<Interval> sorted = new ArrayList<>(items.size());
        for (Net item : items) {
            sorted.add(new Interval(item.first(), item.last()));
        }
        sorted.sort(Comparator.comparingLong(Interval::first).thenComparingLong(Interval::last));

        List<Interval> result = new ArrayList<>();
        if (sorted.isEmpty()) {
            return result;
        }

        long first = sorted.get(0).first();
        long last = sorted.get(0).last();
        for (int index = 1; index < sorted.size(); index++) {
            Interval next = sorted.get(index);
            if (next.first() <= last + 1) {
                last = Math.max(last, next.last());
            } else {
                result.add(new Interval(first, last));
                first = next.first();
                last = next.last();
            }
        }
        result.add(new Interval(first, last));
        return result;
    }

    /**
     * Decomposes an inclusive IPv4 interval into the fewest ascending aligned
     * CIDR blocks. Endpoints must be in the IPv4 range; {@code first > last}
     * represents an empty interval and returns no blocks.
     */
    static List<Block> decompose(long first, long last) {
        List<Block> result = new ArrayList<>();
        if (first > last) {
            return result;
        }

        long cursor = first;
        while (cursor <= last) {
            long remaining = last - cursor + 1;
            long size = 1;
            while (size <= remaining / 2 && cursor % (size * 2) == 0) {
                size *= 2;
            }
            int len = 32 - Long.numberOfTrailingZeros(size);
            result.add(new Block(cursor, len));
            cursor += size;
        }
        return result;
    }

    /** Returns the minimal ordered CIDR cover of the union of {@code items}. */
    static List<Block> blocks(List<Net> items) {
        List<Block> result = new ArrayList<>();
        for (Interval interval : intervals(items)) {
            result.addAll(decompose(interval.first(), interval.last()));
        }
        return result;
    }
}
