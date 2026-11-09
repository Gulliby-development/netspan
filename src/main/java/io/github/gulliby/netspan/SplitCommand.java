package io.github.gulliby.netspan;

import io.github.gulliby.netspan.Api.Failure;
import io.github.gulliby.netspan.Api.Net;
import io.github.gulliby.netspan.Api.Options;
import io.github.gulliby.netspan.Api.Result;
import java.util.List;

/** Expands networks into bounded, ordered subnets. */
final class SplitCommand {
    private SplitCommand() {
    }

    static Result run(Options options, List<Net> items) throws Failure {
        int prefix = options.prefix();
        for (Net item : items) {
            if (item.len() > prefix) {
                throw new Failure(1, "line " + item.line() + ": cannot split "
                        + item.word() + " into /" + prefix);
            }
        }

        long total = 0;
        for (Net item : items) {
            total += 1L << (prefix - item.len());
        }
        if (total > Registry.MAX_SPLIT) {
            throw new Failure(1, "too many subnets (max " + Registry.MAX_SPLIT + ")");
        }

        long stride = 1L << (32 - prefix);
        StringBuilder out = new StringBuilder();
        for (Net item : items) {
            long count = 1L << (prefix - item.len());
            for (long index = 0; index < count; index++) {
                long first = item.first() + index * stride;
                out.append(Report.render(first, prefix, options.style())).append('\n');
            }
        }
        return new Result(0, out.toString(), "");
    }
}
