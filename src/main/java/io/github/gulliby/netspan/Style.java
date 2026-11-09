package io.github.gulliby.netspan;

import java.util.Locale;

/** Renders IPv4 networks in the mask and hexadecimal styles. */
final class Style {
    private Style() {
    }

    /** Renders a network in the mask or hexadecimal style without a trailing line feed. */
    static String render(long first, int len, String style) throws Api.Failure {
        if ("mask".equals(style)) {
            return Report.dotted(first) + " " + Masks.of(len);
        }
        if ("hex".equals(style)) {
            return String.format(Locale.ROOT, "%08X/%d", first, len);
        }
        throw new Api.Failure(1, "not implemented");
    }
}
