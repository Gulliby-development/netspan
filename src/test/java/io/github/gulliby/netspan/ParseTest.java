package io.github.gulliby.netspan;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ParseTest {
    static Stream<Arguments> rows() {
        return Stream.of(
                Arguments.of(List.of("check"), "0.0.0.0\n", 0, "ok, 1 network\n", ""),
                Arguments.of(List.of("check"), "255.255.255.255\n", 0, "ok, 1 network\n", ""),
                Arguments.of(List.of("check"), "0.0.0.0/0\n", 0, "ok, 1 network\n", ""),
                Arguments.of(List.of("check"), "1.2.3.4/32\n", 0, "ok, 1 network\n", ""),
                Arguments.of(List.of("check"), "10.0.0.0/8\n", 0, "ok, 1 network\n", ""),
                Arguments.of(List.of("check"), "192.168.100.0/24\n", 0, "ok, 1 network\n", ""),
                Arguments.of(List.of("check"), "10.0.0.1/24\n", 1, "", "netspan: line 1: host bits set 10.0.0.1/24\n"),
                Arguments.of(List.of("check"), "10.0.0.0/7\n", 0, "ok, 1 network\n", ""),
                Arguments.of(List.of("check"), "10.0.0.2/31\n", 0, "ok, 1 network\n", ""),
                Arguments.of(List.of("check"), "10.0.0.1/31\n", 1, "", "netspan: line 1: host bits set 10.0.0.1/31\n"),
                Arguments.of(List.of("check"), "128.0.0.0/1\n", 0, "ok, 1 network\n", ""),
                Arguments.of(List.of("check"), "128.0.0.1/1\n", 1, "", "netspan: line 1: host bits set 128.0.0.1/1\n"),
                Arguments.of(List.of("check"), "256.0.0.0\n", 1, "", "netspan: line 1: bad network 256.0.0.0\n"),
                Arguments.of(List.of("check"), "1.2.3\n", 1, "", "netspan: line 1: bad network 1.2.3\n"),
                Arguments.of(List.of("check"), "1.2.3.4.5\n", 1, "", "netspan: line 1: bad network 1.2.3.4.5\n"),
                Arguments.of(List.of("check"), "01.2.3.4\n", 1, "", "netspan: line 1: bad network 01.2.3.4\n"),
                Arguments.of(List.of("check"), "1.2.3.04\n", 1, "", "netspan: line 1: bad network 1.2.3.04\n"),
                Arguments.of(List.of("check"), "1..3.4\n", 1, "", "netspan: line 1: bad network 1..3.4\n"),
                Arguments.of(List.of("check"), "a.b.c.d\n", 1, "", "netspan: line 1: bad network a.b.c.d\n"),
                Arguments.of(List.of("check"), "+1.2.3.4\n", 1, "", "netspan: line 1: bad network +1.2.3.4\n"));
    }

    @ParameterizedTest
    @MethodSource("rows")
    void rowsHold(List<String> args, String stdin, int status, String out, String err) {
        Rows.row(args, stdin, status, out, err);
    }
}
