package io.github.gulliby.netspan;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SplitTest {
    static Stream<Arguments> rows() {
        return Stream.of(
                Arguments.of(List.of("split", "--prefix", "26"), "10.0.0.0/24\n", 0, "10.0.0.0/26\n10.0.0.64/26\n10.0.0.128/26\n10.0.0.192/26\n", ""),
                Arguments.of(List.of("split", "--prefix", "24"), "10.0.0.0/24\n", 0, "10.0.0.0/24\n", ""),
                Arguments.of(List.of("split", "--prefix", "25"), "10.0.0.0/24\n10.0.1.0/25\n", 0, "10.0.0.0/25\n10.0.0.128/25\n10.0.1.0/25\n", ""),
                Arguments.of(List.of("split", "--prefix", "23"), "10.0.0.0/24\n", 1, "", "netspan: line 1: cannot split 10.0.0.0/24 into /23\n"),
                Arguments.of(List.of("split", "--prefix", "32"), "10.0.0.0/30\n", 0, "10.0.0.0/32\n10.0.0.1/32\n10.0.0.2/32\n10.0.0.3/32\n", ""),
                Arguments.of(List.of("split", "--prefix", "31", "--style", "range"), "10.0.0.0/29\n", 0, "10.0.0.0-10.0.0.1\n10.0.0.2-10.0.0.3\n10.0.0.4-10.0.0.5\n10.0.0.6-10.0.0.7\n", ""),
                Arguments.of(List.of("split", "--prefix", "26", "--style", "mask"), "10.0.0.0/24\n", 0, "10.0.0.0 255.255.255.192\n10.0.0.64 255.255.255.192\n10.0.0.128 255.255.255.192\n10.0.0.192 255.255.255.192\n", ""),
                Arguments.of(List.of("split", "--prefix", "26", "--style", "hex"), "10.0.0.0/25\n10.0.1.0/26\n", 0, "0A000000/26\n0A000040/26\n0A000100/26\n", ""),
                Arguments.of(List.of("split", "--prefix", "0"), "0.0.0.0/0\n", 0, "0.0.0.0/0\n", ""),
                Arguments.of(List.of("split", "--prefix", "1"), "0.0.0.0/0\n", 0, "0.0.0.0/1\n128.0.0.0/1\n", ""),
                Arguments.of(List.of("split", "--prefix", "2", "--style", "range"), "128.0.0.0/1\n", 0, "128.0.0.0-191.255.255.255\n192.0.0.0-255.255.255.255\n", ""),
                Arguments.of(List.of("split", "--prefix", "24"), "10.0.0.0/16\n10.1.0.0/24\n", 1, "", "netspan: too many subnets (max 256)\n"));
    }

    @ParameterizedTest
    @MethodSource("rows")
    void rowsHold(List<String> args, String stdin, int status, String out, String err) {
        Rows.row(args, stdin, status, out, err);
    }
}
