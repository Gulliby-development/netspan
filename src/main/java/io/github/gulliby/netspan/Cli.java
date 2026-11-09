package io.github.gulliby.netspan;

import io.github.gulliby.netspan.Api.Command;
import io.github.gulliby.netspan.Api.Failure;
import io.github.gulliby.netspan.Api.Net;
import io.github.gulliby.netspan.Api.Options;
import io.github.gulliby.netspan.Api.Result;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Command-line validation, list loading and command dispatch. */
public final class Cli {
    private Cli() {
    }

    public static boolean readsStdin(List<String> args) {
        if (args.isEmpty()) {
            return false;
        }
        String first = args.get(0);
        if (isHelp(first) || "--version".equals(first)) {
            return false;
        }
        try {
            validate(args);
            return true;
        } catch (Failure failure) {
            return false;
        }
    }

    public static Result run(List<String> args, String stdin) {
        if (args.isEmpty()) {
            return new Result(2, "", Registry.USAGE);
        }
        String first = args.get(0);
        if (isHelp(first)) {
            return new Result(0, Registry.USAGE, "");
        }
        if ("--version".equals(first)) {
            return new Result(0, Registry.VERSION + "\n", "");
        }
        try {
            Parsed parsed = validate(args);
            List<Net> items = Input.read(stdin);
            return parsed.command().handler().run(parsed.options(), items);
        } catch (Failure failure) {
            return new Result(failure.status, "", "netspan: " + failure.getMessage() + "\n");
        }
    }

    private static boolean isHelp(String word) {
        return "help".equals(word) || "-h".equals(word) || "--help".equals(word);
    }

    private static Parsed validate(List<String> args) throws Failure {
        Command command = null;
        for (Command candidate : Registry.COMMANDS) {
            if (candidate.name().equals(args.get(0))) {
                command = candidate;
                break;
            }
        }
        if (command == null) {
            throw new Failure(2, "unknown command " + args.get(0));
        }

        String style = "cidr";
        String mode = "addresses";
        String within = "";
        String ip = "";
        int prefix = -1;
        Set<String> seen = new HashSet<>();

        for (int i = 1; i < args.size(); i++) {
            String word = args.get(i);
            if (!Registry.OPTIONS.contains(word)) {
                if (word.startsWith("-") && !word.equals("-")) {
                    throw new Failure(2, "unknown option " + word);
                }
                throw new Failure(2, "unexpected argument " + word);
            }
            if (!command.options().contains(word)) {
                throw new Failure(2, "option " + word + " is not valid for " + command.name());
            }
            if (!seen.add(word)) {
                throw new Failure(2, "given twice: " + word);
            }
            if (i + 1 >= args.size()) {
                throw new Failure(2, "missing value for " + word);
            }
            String value = args.get(++i);
            switch (word) {
                case "--style" -> {
                    if (!Registry.STYLES.contains(value)) {
                        throw new Failure(2, "bad value for --style");
                    }
                    style = value;
                }
                case "--mode" -> {
                    if (!Registry.MODES.contains(value)) {
                        throw new Failure(2, "bad value for --mode");
                    }
                    mode = value;
                }
                case "--prefix" -> {
                    try {
                        prefix = Nets.length(value);
                    } catch (Failure failure) {
                        throw new Failure(2, "bad value for --prefix");
                    }
                }
                case "--within" -> {
                    Net parsed;
                    try {
                        parsed = Nets.parse(value);
                    } catch (Failure failure) {
                        throw new Failure(2, "bad value for --within");
                    }
                    if (!Nets.aligned(parsed.first(), parsed.len())) {
                        throw new Failure(2, "bad value for --within");
                    }
                    within = value;
                }
                case "--ip" -> {
                    try {
                        Nets.address(value);
                    } catch (Failure failure) {
                        throw new Failure(2, "bad value for --ip");
                    }
                    ip = value;
                }
                default -> throw new Failure(2, "unknown option " + word);
            }
        }

        if (!command.required().isEmpty() && !seen.contains(command.required())) {
            throw new Failure(2, "missing " + command.required());
        }
        return new Parsed(command, new Options(style, mode, within, ip, prefix));
    }

    private record Parsed(Command command, Options options) {
    }
}
