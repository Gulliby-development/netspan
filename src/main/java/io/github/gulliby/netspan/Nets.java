package io.github.gulliby.netspan;

/** Strict IPv4 address and network parsing helpers. */
final class Nets {
    private static final long IPV4_MAX = 0xffff_ffffL;

    private Nets() {
    }

    /** Parses a canonical dotted-decimal IPv4 address. */
    static long address(String word) throws Api.Failure {
        String[] octets = word.split("\\.", -1);
        if (octets.length != 4) {
            throw new Api.Failure(1, "bad address " + word);
        }

        long value = 0;
        for (String octet : octets) {
            if (octet.isEmpty() || octet.length() > 3
                    || (octet.length() > 1 && octet.charAt(0) == '0')) {
                throw new Api.Failure(1, "bad address " + word);
            }
            int part = 0;
            for (int i = 0; i < octet.length(); i++) {
                char ch = octet.charAt(i);
                if (ch < '0' || ch > '9') {
                    throw new Api.Failure(1, "bad address " + word);
                }
                part = part * 10 + ch - '0';
            }
            if (part > 255) {
                throw new Api.Failure(1, "bad address " + word);
            }
            value = (value << 8) | part;
        }
        return value;
    }

    /** Parses a canonical prefix length from 0 through 32. */
    static int length(String word) throws Api.Failure {
        if (word.isEmpty() || word.length() > 2
                || (word.length() > 1 && word.charAt(0) == '0')) {
            throw new Api.Failure(1, "bad prefix " + word);
        }
        int value = 0;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (ch < '0' || ch > '9') {
                throw new Api.Failure(1, "bad prefix " + word);
            }
            value = value * 10 + ch - '0';
        }
        if (value > 32) {
            throw new Api.Failure(1, "bad prefix " + word);
        }
        return value;
    }

    /** Parses a network while retaining its original address, including any host bits. */
    static Api.Net parse(String word) throws Api.Failure {
        int slash = word.indexOf('/');
        if (slash < 0) {
            try {
                return new Api.Net(0, word, address(word), 32);
            } catch (Api.Failure failure) {
                throw new Api.Failure(1, "bad network " + word);
            }
        }
        if (slash != word.lastIndexOf('/')) {
            throw new Api.Failure(1, "bad network " + word);
        }

        String addressWord = word.substring(0, slash);
        String prefixWord = word.substring(slash + 1);
        long first;
        int len;
        try {
            first = address(addressWord);
            len = length(prefixWord);
        } catch (Api.Failure failure) {
            throw new Api.Failure(1, "bad network " + word);
        }
        return new Api.Net(0, word, first, len);
    }

    /** Returns whether an address is the first address of a prefix-sized block. */
    static boolean aligned(long first, int len) {
        if (first < 0 || first > IPV4_MAX || len < 0 || len > 32) {
            return false;
        }
        long size = 1L << (32 - len);
        return first % size == 0;
    }
}
