package io.github.gulliby.netspan;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class UsageTest {
    static Stream<Arguments> rows() {
        return Stream.of(
                Arguments.of(List.of("help"), "", 0, "usage: netspan check\n       netspan sort [--style STYLE]\n       netspan merge [--style STYLE]\n       netspan count [--mode MODE]\n       netspan gaps --within NET [--style STYLE]\n       netspan contains --ip IP\n       netspan split --prefix N [--style STYLE]\n", ""),
                Arguments.of(List.of("-h"), "", 0, "usage: netspan check\n       netspan sort [--style STYLE]\n       netspan merge [--style STYLE]\n       netspan count [--mode MODE]\n       netspan gaps --within NET [--style STYLE]\n       netspan contains --ip IP\n       netspan split --prefix N [--style STYLE]\n", ""),
                Arguments.of(List.of("--help", "--bogus"), "", 0, "usage: netspan check\n       netspan sort [--style STYLE]\n       netspan merge [--style STYLE]\n       netspan count [--mode MODE]\n       netspan gaps --within NET [--style STYLE]\n       netspan contains --ip IP\n       netspan split --prefix N [--style STYLE]\n", ""),
                Arguments.of(List.of(), "", 2, "", "usage: netspan check\n       netspan sort [--style STYLE]\n       netspan merge [--style STYLE]\n       netspan count [--mode MODE]\n       netspan gaps --within NET [--style STYLE]\n       netspan contains --ip IP\n       netspan split --prefix N [--style STYLE]\n"),
                Arguments.of(List.of("--version"), "", 0, "netspan 0.1.0\n", ""),
                Arguments.of(List.of("--version", "x"), "", 0, "netspan 0.1.0\n", ""),
                Arguments.of(List.of("frob"), "", 2, "", "netspan: unknown command frob\n"),
                Arguments.of(List.of(""), "", 2, "", "netspan: unknown command \n"),
                Arguments.of(List.of("Check"), "", 2, "", "netspan: unknown command Check\n"),
                Arguments.of(List.of("-H"), "", 2, "", "netspan: unknown command -H\n"));
    }

    @ParameterizedTest
    @MethodSource("rows")
    void rowsHold(List<String> args, String stdin, int status, String out, String err) {
        Rows.row(args, stdin, status, out, err);
    }
}
