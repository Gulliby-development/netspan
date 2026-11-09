package io.github.gulliby.netspan;

import io.github.gulliby.netspan.Api.Command;
import java.util.List;
import java.util.Set;

/** The version, the limits, the usage text, the styles, the modes and the command table (in the order of the usage text). */
public final class Registry {
    private Registry() {
    }

    public static final String VERSION = "netspan 0.1.0";
    public static final int MAX_LINES = 40;
    public static final int MAX_SPLIT = 256;
    public static final String USAGE =
            "usage: netspan check\n" +
            "       netspan sort [--style STYLE]\n" +
            "       netspan merge [--style STYLE]\n" +
            "       netspan count [--mode MODE]\n" +
            "       netspan gaps --within NET [--style STYLE]\n" +
            "       netspan contains --ip IP\n" +
            "       netspan split --prefix N [--style STYLE]\n";
    public static final List<String> STYLES = List.of("cidr", "range", "mask", "hex");
    public static final List<String> MODES = List.of("addresses", "hosts");
    public static final List<String> OPTIONS = List.of("--style", "--mode", "--within", "--ip", "--prefix");

    public static final List<Command> COMMANDS = List.of(
            new Command("check", Set.of(), "", CheckCommand::run),
            new Command("sort", Set.of("--style"), "", SortCommand::run),
            new Command("merge", Set.of("--style"), "", MergeCommand::run),
            new Command("count", Set.of("--mode"), "", CountCommand::run),
            new Command("gaps", Set.of("--within", "--style"), "--within", GapsCommand::run),
            new Command("contains", Set.of("--ip"), "--ip", ContainsCommand::run),
            new Command("split", Set.of("--prefix", "--style"), "--prefix", SplitCommand::run));
}
