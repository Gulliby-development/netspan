package io.github.gulliby.netspan;

import io.github.gulliby.netspan.Api.Failure;
import io.github.gulliby.netspan.Api.Net;
import io.github.gulliby.netspan.Api.Options;
import io.github.gulliby.netspan.Api.Result;
import java.util.List;

/** Runs the merge command and renders its minimal CIDR cover. */
final class MergeCommand {
    private MergeCommand() {
    }

    static Result run(Options options, List<Net> items) throws Failure {
        StringBuilder out = new StringBuilder();
        for (Merge.Block block : Merge.blocks(items)) {
            out.append(Report.render(block.first(), block.len(), options.style())).append('\n');
        }
        return new Result(0, out.toString(), "");
    }
}
