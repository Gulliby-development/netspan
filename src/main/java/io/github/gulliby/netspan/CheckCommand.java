package io.github.gulliby.netspan;

import io.github.gulliby.netspan.Api.Failure;
import io.github.gulliby.netspan.Api.Net;
import io.github.gulliby.netspan.Api.Options;
import io.github.gulliby.netspan.Api.Result;
import java.util.List;

/** Placeholder: the task of its part replaces this file with the command. */
final class CheckCommand {
    private CheckCommand() {
    }

    static Result run(Options options, List<Net> items) throws Failure {
        return new Result(0, "ok, " + Report.plural(items.size(), "network") + "\n", "");
    }
}
