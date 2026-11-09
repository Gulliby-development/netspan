package io.github.gulliby.netspan;

/** Deterministic text formatting for IPv4 addresses and networks. */
public final class Report {
    private Report() {
    }

    /** Returns the canonical dotted-decimal form of an IPv4 address. */
    static String dotted(long address) {
        return ((address >>> 24) & 255) + "."
                + ((address >>> 16) & 255) + "."
                + ((address >>> 8) & 255) + "."
                + (address & 255);
    }

    /** Renders a network without a trailing line feed. */
    static String render(long first, int len, String style) throws Api.Failure {
        if ("cidr".equals(style)) {
            return dotted(first) + "/" + len;
        }
        if ("range".equals(style)) {
            long last = first + (1L << (32 - len)) - 1;
            return dotted(first) + "-" + dotted(last);
        }
        if ("mask".equals(style) || "hex".equals(style)) {
            return Style.render(first, len, style);
        }
        throw new Api.Failure(1, "not implemented");
    }

    /** Returns a decimal count followed by a correctly pluralized noun. */
    static String plural(long count, String noun) {
        return count + " " + noun + (count == 1 ? "" : "s");
    }
}
