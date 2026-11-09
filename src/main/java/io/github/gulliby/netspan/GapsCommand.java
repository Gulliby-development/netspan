package io.github.gulliby.netspan;

import io.github.gulliby.netspan.Api.Failure;
import io.github.gulliby.netspan.Api.Net;
import io.github.gulliby.netspan.Api.Options;
import io.github.gulliby.netspan.Api.Result;
import java.util.List;

/** Runs the gaps command within the requested IPv4 boundary. */
final class GapsCommand {
    private GapsCommand() {
    }

    static Result run(Options options, List<Net> items) throws Failure {
        Net boundary = Nets.parse(options.within());
        long first = boundary.first();
        long last = boundary.last();
        long cursor = first;
        StringBuilder out = new StringBuilder();

        for (Merge.Interval interval : Merge.intervals(items)) {
            long clippedFirst = Math.max(first, interval.first());
            long clippedLast = Math.min(last, interval.last());
            if (clippedFirst > clippedLast) {
                continue;
            }
            if (cursor < clippedFirst) {
                append(out, cursor, clippedFirst - 1, options.style());
            }
            cursor = Math.max(cursor, clippedLast + 1);
        }
        if (cursor <= last) {
            append(out, cursor, last, options.style());
        }
        if (out.length() == 0) {
            return new Result(0, "none\n", "");
        }
        return new Result(0, out.toString(), "");
    }

    private static void append(StringBuilder out, long first, long last, String style) throws Failure {
        for (Merge.Block block : Merge.decompose(first, last)) {
            out.append(Report.render(block.first(), block.len(), style)).append('\n');
        }
    }
}
