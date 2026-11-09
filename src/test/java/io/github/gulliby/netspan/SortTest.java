package io.github.gulliby.netspan;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SortTest {
    static Stream<Arguments> rows() {
        return Stream.of(
                Arguments.of(List.of("sort"), "10.0.0.0/8\n", 0, "10.0.0.0/8\n", ""),
                Arguments.of(List.of("sort"), "192.168.0.0/16\n10.0.0.0/8\n172.16.0.0/12\n", 0, "10.0.0.0/8\n172.16.0.0/12\n192.168.0.0/16\n", ""),
                Arguments.of(List.of("sort"), "10.0.0.0/24\n10.0.0.0/8\n10.0.0.0/16\n", 0, "10.0.0.0/8\n10.0.0.0/16\n10.0.0.0/24\n", ""),
                Arguments.of(List.of("sort"), "10.0.0.0/8\n10.0.0.0/8\n", 0, "10.0.0.0/8\n10.0.0.0/8\n", ""),
                Arguments.of(List.of("sort"), "1.2.3.4\n1.2.3.3\n1.2.3.5\n", 0, "1.2.3.3/32\n1.2.3.4/32\n1.2.3.5/32\n", ""),
                Arguments.of(List.of("sort", "--style", "range"), "10.0.0.0/30\n10.0.0.8/29\n", 0, "10.0.0.0-10.0.0.3\n10.0.0.8-10.0.0.15\n", ""),
                Arguments.of(List.of("sort", "--style", "range"), "0.0.0.0/0\n", 0, "0.0.0.0-255.255.255.255\n", ""),
                Arguments.of(List.of("sort", "--style", "range"), "255.255.255.255\n", 0, "255.255.255.255-255.255.255.255\n", ""),
                Arguments.of(List.of("sort", "--style", "cidr"), "128.0.0.0/1\n0.0.0.0/1\n", 0, "0.0.0.0/1\n128.0.0.0/1\n", ""),
                Arguments.of(List.of("sort", "--style", "range"), "172.16.0.0/12\n172.16.0.0/16\n10.255.255.255\n", 0, "10.255.255.255-10.255.255.255\n172.16.0.0-172.31.255.255\n172.16.0.0-172.16.255.255\n", ""),
                Arguments.of(List.of("sort"), "# c\n9.9.9.9\n\n1.1.1.1\n", 0, "1.1.1.1/32\n9.9.9.9/32\n", ""),
                Arguments.of(List.of("sort", "--style", "range"), "203.0.113.0/24\n198.51.100.0/24\n192.0.2.0/24\n", 0, "192.0.2.0-192.0.2.255\n198.51.100.0-198.51.100.255\n203.0.113.0-203.0.113.255\n", ""));
    }

    @ParameterizedTest
    @MethodSource("rows")
    void rowsHold(List<String> args, String stdin, int status, String out, String err) {
        Rows.row(args, stdin, status, out, err);
    }
}
