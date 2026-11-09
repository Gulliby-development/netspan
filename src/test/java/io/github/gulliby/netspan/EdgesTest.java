package io.github.gulliby.netspan;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EdgesTest {
    static Stream<Arguments> rows() {
        return Stream.of(
                Arguments.of(List.of("sort"), "0.0.0.0/0\n0.0.0.0/32\n", 0, "0.0.0.0/0\n0.0.0.0/32\n", ""),
                Arguments.of(List.of("check"), "0.0.0.0/0\n255.255.255.255/32\n", 0, "ok, 2 networks\n", ""),
                Arguments.of(List.of("sort", "--style", "range"), "255.255.255.254/31\n255.255.255.255\n", 0, "255.255.255.254-255.255.255.255\n255.255.255.255-255.255.255.255\n", ""),
                Arguments.of(List.of("sort", "--style", "range"), "0.0.0.0\n", 0, "0.0.0.0-0.0.0.0\n", ""),
                Arguments.of(List.of("merge"), "0.0.0.0/1\n128.0.0.0/1\n", 0, "0.0.0.0/0\n", ""),
                Arguments.of(List.of("count"), "0.0.0.0/0\n10.0.0.0/8\n", 0, "4294967296\n", ""),
                Arguments.of(List.of("sort"), "1.1.1.1\n1.1.1.1/32\n1.1.1.0/24\n", 0, "1.1.1.0/24\n1.1.1.1/32\n1.1.1.1/32\n", ""),
                Arguments.of(List.of("check"), "0.0.0.0\n# 0.0.0.0\n", 0, "ok, 1 network\n", ""),
                Arguments.of(List.of("gaps", "--within", "0.0.0.0/0"), "0.0.0.0/1\n", 0, "128.0.0.0/1\n", ""),
                Arguments.of(List.of("split", "--prefix", "2"), "0.0.0.0/0\n", 0, "0.0.0.0/2\n64.0.0.0/2\n128.0.0.0/2\n192.0.0.0/2\n", ""));
    }

    @ParameterizedTest
    @MethodSource("rows")
    void rowsHold(List<String> args, String stdin, int status, String out, String err) {
        Rows.row(args, stdin, status, out, err);
    }
}
