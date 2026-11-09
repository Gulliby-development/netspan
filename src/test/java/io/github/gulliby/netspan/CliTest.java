package io.github.gulliby.netspan;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CliTest {
    static Stream<Arguments> rows() {
        return Stream.of(
                Arguments.of(List.of("check"), "10.0.0.0/8\n192.168.1.0/24\n", 0, "ok, 2 networks\n", ""),
                Arguments.of(List.of("check"), "1.2.3.4\n", 0, "ok, 1 network\n", ""),
                Arguments.of(List.of("sort"), "192.168.0.0/16\n10.0.0.0/8\n", 0, "10.0.0.0/8\n192.168.0.0/16\n", ""),
                Arguments.of(List.of("sort", "--style", "range"), "10.0.0.0/30\n", 0, "10.0.0.0-10.0.0.3\n", ""),
                Arguments.of(List.of("merge"), "10.0.0.0/25\n10.0.0.128/25\n", 0, "10.0.0.0/24\n", ""),
                Arguments.of(List.of("count"), "10.0.0.0/24\n10.0.1.0/24\n", 0, "512\n", ""),
                Arguments.of(List.of("gaps", "--within", "10.0.0.0/24"), "10.0.0.0/25\n", 0, "10.0.0.128/25\n", ""),
                Arguments.of(List.of("contains", "--ip", "10.1.2.3"), "10.0.0.0/8\n", 0, "10.1.2.3 in 10.0.0.0/8\n", ""),
                Arguments.of(List.of("split", "--prefix", "26"), "10.0.0.0/24\n", 0, "10.0.0.0/26\n10.0.0.64/26\n10.0.0.128/26\n10.0.0.192/26\n", ""));
    }

    @ParameterizedTest
    @MethodSource("rows")
    void rowsHold(List<String> args, String stdin, int status, String out, String err) {
        Rows.row(args, stdin, status, out, err);
    }
}
