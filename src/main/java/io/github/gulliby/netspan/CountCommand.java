package io.github.gulliby.netspan;

import io.github.gulliby.netspan.Api.Failure;
import io.github.gulliby.netspan.Api.Net;
import io.github.gulliby.netspan.Api.Options;
import io.github.gulliby.netspan.Api.Result;
import java.util.List;

/** Runs the count command over the canonical union of the input networks. */
final class CountCommand {
    private CountCommand() {
    }

    static Result run(Options options, List<Net> items) throws Failure {
        long total = 0;
        for (Merge.Block block : Merge.blocks(items)) {
            long size = 1L << (32 - block.len());
            if ("hosts".equals(options.mode()) && block.len() <= 30) {
                size -= 2;
            }
            total += size;
        }
        return new Result(0, Long.toString(total) + "\n", "");
    }
}
