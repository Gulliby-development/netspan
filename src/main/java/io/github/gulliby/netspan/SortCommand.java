package io.github.gulliby.netspan;

import io.github.gulliby.netspan.Api.Failure;
import io.github.gulliby.netspan.Api.Net;
import io.github.gulliby.netspan.Api.Options;
import io.github.gulliby.netspan.Api.Result;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Placeholder: the task of its part replaces this file with the command. */
final class SortCommand {
    private SortCommand() {
    }

    static Result run(Options options, List<Net> items) throws Failure {
        List<Net> sorted = new ArrayList<>(items);
        sorted.sort(Comparator.comparingLong(Net::first).thenComparingInt(Net::len));
        StringBuilder out = new StringBuilder();
        for (Net item : sorted) {
            out.append(Report.render(item.first(), item.len(), options.style())).append('\n');
        }
        return new Result(0, out.toString(), "");
    }
}
