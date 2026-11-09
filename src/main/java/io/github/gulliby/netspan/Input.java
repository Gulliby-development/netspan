package io.github.gulliby.netspan;

import java.util.ArrayList;
import java.util.List;

/** Reads and validates the physical lines of a network list. */
final class Input {
    private Input() {
    }

    /** Parses the input text into ordered networks, retaining each physical line and word. */
    static List<Api.Net> read(String stdin) throws Api.Failure {
        List<String> lines = lines(stdin);
        if (lines.size() > Registry.MAX_LINES) {
            throw new Api.Failure(1, "input is too long (max 40 lines)");
        }

        List<Api.Net> networks = new ArrayList<>();
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.endsWith("\r")) {
                line = line.substring(0, line.length() - 1);
            }

            List<String> words = words(line);
            if (words.isEmpty() || words.get(0).startsWith("#")) {
                continue;
            }
            int number = index + 1;
            if (words.size() != 1) {
                throw new Api.Failure(1, "line " + number + ": expected 1 network");
            }

            String word = words.get(0);
            Api.Net parsed;
            try {
                parsed = Nets.parse(word);
            } catch (Api.Failure failure) {
                throw new Api.Failure(1, "line " + number + ": bad network " + word);
            }
            if (!Nets.aligned(parsed.first(), parsed.len())) {
                throw new Api.Failure(1, "line " + number + ": host bits set " + word);
            }
            networks.add(new Api.Net(number, word, parsed.first(), parsed.len()));
        }

        if (networks.isEmpty()) {
            throw new Api.Failure(1, "no networks");
        }
        return networks;
    }

    private static List<String> lines(String text) {
        List<String> result = new ArrayList<>();
        if (text.isEmpty()) {
            return result;
        }
        int start = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                result.add(text.substring(start, i));
                start = i + 1;
            }
        }
        if (start < text.length()) {
            result.add(text.substring(start));
        }
        return result;
    }

    private static List<String> words(String line) {
        List<String> result = new ArrayList<>();
        int index = 0;
        while (index < line.length()) {
            while (index < line.length() && separator(line.charAt(index))) {
                index++;
            }
            int start = index;
            while (index < line.length() && !separator(line.charAt(index))) {
                index++;
            }
            if (start < index) {
                result.add(line.substring(start, index));
            }
        }
        return result;
    }

    private static boolean separator(char ch) {
        return ch == ' ' || ch == '\t';
    }
}
