package io.github.gulliby.netspan;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Options2Test {
    static Stream<Arguments> rows() {
        return Stream.of(
                Arguments.of(List.of("sort", "--style", "hex"), "10.0.0.0/8\n", 0, "0A000000/8\n", ""),
                Arguments.of(List.of("split", "--style", "range", "--prefix", "25"), "10.0.0.0/24\n", 0, "10.0.0.0-10.0.0.127\n10.0.0.128-10.0.0.255\n", ""),
                Arguments.of(List.of("gaps", "--style", "cidr", "--within", "10.0.0.0/23"), "10.0.0.0/24\n", 0, "10.0.1.0/24\n", ""),
                Arguments.of(List.of("contains", "--ip", "9.9.9.9"), "9.0.0.0/8\n", 0, "9.9.9.9 in 9.0.0.0/8\n", ""));
    }

    @ParameterizedTest
    @MethodSource("rows")
    void rowsHold(List<String> args, String stdin, int status, String out, String err) {
        Rows.row(args, stdin, status, out, err);
    }
}
