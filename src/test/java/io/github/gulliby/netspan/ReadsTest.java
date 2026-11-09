package io.github.gulliby.netspan;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ReadsTest {
    static Stream<Arguments> rows() {
        return Stream.of(
                Arguments.of(List.of("check"), true),
                Arguments.of(List.of("sort"), true),
                Arguments.of(List.of("sort", "--style", "range"), true),
                Arguments.of(List.of("merge"), true),
                Arguments.of(List.of("count"), true),
                Arguments.of(List.of("count", "--mode", "hosts"), true),
                Arguments.of(List.of("gaps", "--within", "10.0.0.0/8"), true),
                Arguments.of(List.of("gaps", "--within", "10.0.0.0/8", "--style", "mask"), true),
                Arguments.of(List.of("contains", "--ip", "1.2.3.4"), true),
                Arguments.of(List.of("split", "--prefix", "24"), true),
                Arguments.of(List.of("split", "--style", "hex", "--prefix", "8"), true),
                Arguments.of(List.of(), false),
                Arguments.of(List.of("help"), false),
                Arguments.of(List.of("-h"), false),
                Arguments.of(List.of("--help"), false),
                Arguments.of(List.of("--version"), false),
                Arguments.of(List.of("frob"), false),
                Arguments.of(List.of("check", "--style", "cidr"), false),
                Arguments.of(List.of("sort", "--style", "x"), false),
                Arguments.of(List.of("gaps"), false),
                Arguments.of(List.of("gaps", "--within", "10.0.0.1/8"), false),
                Arguments.of(List.of("split", "--prefix", "33"), false),
                Arguments.of(List.of("contains", "--ip", "1.2.3"), false),
                Arguments.of(List.of("Check"), false));
    }

    @ParameterizedTest
    @MethodSource("rows")
    void rowsHold(List<String> args, boolean expected) {
        Rows.reads(args, expected);
    }
}
