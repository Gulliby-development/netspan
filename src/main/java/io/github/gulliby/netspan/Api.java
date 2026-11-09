package io.github.gulliby.netspan;

import java.util.List;
import java.util.Set;

/** The shared types of netspan. */
public final class Api {
    private Api() {
    }

    /** What one command line produced: the exit status and the text for standard output and standard error. */
    public record Result(int status, String out, String err) {
    }

    /** Thrown by a command or the list reader: the exit status and the message without "netspan: " and the line feed. */
    public static final class Failure extends Exception {
        private static final long serialVersionUID = 1L;

        public final int status;

        public Failure(int status, String message) {
            super(message);
            this.status = status;
        }
    }

    /** One network of the list: its line number, the word as written, the first address and the prefix length. */
    public record Net(int line, String word, long first, int len) {
        public long size() {
            return 1L << (32 - len);
        }

        public long last() {
            return first + size() - 1;
        }
    }

    /** The options of a command line, with the defaults filled in: style cidr, mode addresses; within and ip are empty and prefix is -1 when absent. */
    public record Options(String style, String mode, String within, String ip, int prefix) {
    }

    /** A command returns its Result or throws Failure; items is the list that Input.read returns, in input order. */
    @FunctionalInterface
    public interface Handler {
        Result run(Options options, List<Net> items) throws Failure;
    }

    /** A command: its name, the options it accepts, the option that must be given (or "") and its handler. */
    public record Command(String name, Set<String> options, String required, Handler handler) {
    }
}
