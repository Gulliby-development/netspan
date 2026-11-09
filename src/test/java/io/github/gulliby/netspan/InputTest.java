package io.github.gulliby.netspan;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class InputTest {
    static Stream<Arguments> rows() {
        return Stream.of(
                Arguments.of(List.of("check"), "", 1, "", "netspan: no networks\n"),
                Arguments.of(List.of("check"), "\n", 1, "", "netspan: no networks\n"),
                Arguments.of(List.of("check"), "# only\n\n   \n", 1, "", "netspan: no networks\n"),
                Arguments.of(List.of("check"), "10.0.0.0/8", 0, "ok, 1 network\n", ""),
                Arguments.of(List.of("check"), "10.0.0.0/8\n11.0.0.0/8", 0, "ok, 2 networks\n", ""),
                Arguments.of(List.of("check"), "10.0.0.0/8\r\n11.0.0.0/8\r\n", 0, "ok, 2 networks\n", ""),
                Arguments.of(List.of("check"), "10.0.0.0/8\n10.0.0.0/8 11.0.0.0/8\n", 1, "", "netspan: line 2: expected 1 network\n"),
                Arguments.of(List.of("check"), "\n# c\n10.0.0.0/8\n10.0.0.0 /8\n", 1, "", "netspan: line 4: expected 1 network\n"),
                Arguments.of(List.of("check"), "  10.0.0.0/8  \n\t11.0.0.0/8\t\n", 0, "ok, 2 networks\n", ""),
                Arguments.of(List.of("check"), "10.0.0.0/8\n   # note\n11.0.0.0/8\n#12.0.0.0/8\n", 0, "ok, 2 networks\n", ""),
                Arguments.of(List.of("check"), "10.0.0.0/8\nfrob\n11.0.0.0/8\nbad\n", 1, "", "netspan: line 2: bad network frob\n"),
                Arguments.of(List.of("check"), "10.0.0.0/8\nx\n1 2\n", 1, "", "netspan: line 2: bad network x\n"),
                Arguments.of(List.of("check"), "10.0.0.0/8\n10.0.0.0/8 #c\n", 1, "", "netspan: line 2: expected 1 network\n"),
                Arguments.of(List.of("check"), "10.0.0.0/8\n\n\n1.2.3\n", 1, "", "netspan: line 4: bad network 1.2.3\n"),
                Arguments.of(List.of("sort"), "10.0.0.0/8\n\t\n", 0, "10.0.0.0/8\n", ""));
    }

    @ParameterizedTest
    @MethodSource("rows")
    void rowsHold(List<String> args, String stdin, int status, String out, String err) {
        Rows.row(args, stdin, status, out, err);
    }
}
