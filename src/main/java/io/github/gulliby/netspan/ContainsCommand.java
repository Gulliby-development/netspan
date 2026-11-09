package io.github.gulliby.netspan;

import io.github.gulliby.netspan.Api.Failure;
import io.github.gulliby.netspan.Api.Net;
import io.github.gulliby.netspan.Api.Options;
import io.github.gulliby.netspan.Api.Result;
import java.util.List;

/** Finds every input network that contains the requested IPv4 address. */
final class ContainsCommand {
    private ContainsCommand() {
    }

    static Result run(Options options, List<Net> items) throws Failure {
        long address = Nets.address(options.ip());
        StringBuilder out = new StringBuilder();
        for (Net item : items) {
            if (item.first() <= address && address <= item.last()) {
                out.append(options.ip()).append(" in ")
                        .append(Report.render(item.first(), item.len(), "cidr"))
                        .append('\n');
            }
        }
        if (out.length() == 0) {
            return new Result(1, options.ip() + " is not covered\n", "");
        }
        return new Result(0, out.toString(), "");
    }
}
