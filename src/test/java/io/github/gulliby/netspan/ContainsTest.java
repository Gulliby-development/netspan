package io.github.gulliby.netspan;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ContainsTest {
    static Stream<Arguments> rows() {
        return Stream.of(
                Arguments.of(List.of("contains", "--ip", "10.1.2.3"), "10.0.0.0/8\n", 0, "10.1.2.3 in 10.0.0.0/8\n", ""),
                Arguments.of(List.of("contains", "--ip", "11.1.2.3"), "10.0.0.0/8\n", 1, "11.1.2.3 is not covered\n", ""),
                Arguments.of(List.of("contains", "--ip", "10.1.2.3"), "10.0.0.0/8\n10.1.0.0/16\n10.1.2.0/24\n", 0, "10.1.2.3 in 10.0.0.0/8\n10.1.2.3 in 10.1.0.0/16\n10.1.2.3 in 10.1.2.0/24\n", ""),
                Arguments.of(List.of("contains", "--ip", "10.1.2.3"), "10.1.2.0/24\n10.0.0.0/8\n", 0, "10.1.2.3 in 10.1.2.0/24\n10.1.2.3 in 10.0.0.0/8\n", ""),
                Arguments.of(List.of("contains", "--ip", "10.1.2.3"), "10.1.2.3\n", 0, "10.1.2.3 in 10.1.2.3/32\n", ""),
                Arguments.of(List.of("contains", "--ip", "10.1.2.3"), "10.1.2.4\n", 1, "10.1.2.3 is not covered\n", ""),
                Arguments.of(List.of("contains", "--ip", "0.0.0.0"), "0.0.0.0/0\n", 0, "0.0.0.0 in 0.0.0.0/0\n", ""),
                Arguments.of(List.of("contains", "--ip", "255.255.255.255"), "0.0.0.0/0\n255.255.255.254/31\n", 0, "255.255.255.255 in 0.0.0.0/0\n255.255.255.255 in 255.255.255.254/31\n", ""),
                Arguments.of(List.of("contains", "--ip", "10.0.0.255"), "10.0.0.0/24\n10.0.1.0/24\n", 0, "10.0.0.255 in 10.0.0.0/24\n", ""));
    }

    @ParameterizedTest
    @MethodSource("rows")
    void rowsHold(List<String> args, String stdin, int status, String out, String err) {
        Rows.row(args, stdin, status, out, err);
    }
}
