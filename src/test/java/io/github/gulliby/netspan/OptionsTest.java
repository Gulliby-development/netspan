package io.github.gulliby.netspan;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class OptionsTest {
    static Stream<Arguments> rows() {
        return Stream.of(
                Arguments.of(List.of("sort", "extra"), "", 2, "", "netspan: unexpected argument extra\n"),
                Arguments.of(List.of("sort", "-"), "", 2, "", "netspan: unexpected argument -\n"),
                Arguments.of(List.of("sort", ""), "", 2, "", "netspan: unexpected argument \n"),
                Arguments.of(List.of("sort", "--bogus"), "", 2, "", "netspan: unknown option --bogus\n"),
                Arguments.of(List.of("check", "--style", "cidr"), "", 2, "", "netspan: option --style is not valid for check\n"),
                Arguments.of(List.of("sort", "--mode", "hosts"), "", 2, "", "netspan: option --mode is not valid for sort\n"),
                Arguments.of(List.of("sort", "--style", "cidr", "--style", "cidr"), "", 2, "", "netspan: given twice: --style\n"),
                Arguments.of(List.of("sort", "--style"), "", 2, "", "netspan: missing value for --style\n"),
                Arguments.of(List.of("sort", "-x", "--style"), "", 2, "", "netspan: unknown option -x\n"),
                Arguments.of(List.of("check", "--style"), "", 2, "", "netspan: option --style is not valid for check\n"),
                Arguments.of(List.of("sort", "--style", "x", "--bogus"), "", 2, "", "netspan: bad value for --style\n"),
                Arguments.of(List.of("count", "--style", "hex"), "", 2, "", "netspan: option --style is not valid for count\n"),
                Arguments.of(List.of("merge", "--prefix", "8"), "", 2, "", "netspan: option --prefix is not valid for merge\n"),
                Arguments.of(List.of("gaps"), "", 2, "", "netspan: missing --within\n"),
                Arguments.of(List.of("split"), "", 2, "", "netspan: missing --prefix\n"),
                Arguments.of(List.of("contains"), "", 2, "", "netspan: missing --ip\n"),
                Arguments.of(List.of("gaps", "--style", "mask"), "", 2, "", "netspan: missing --within\n"),
                Arguments.of(List.of("split", "--style", "hex", "--style", "mask"), "", 2, "", "netspan: given twice: --style\n"),
                Arguments.of(List.of("merge", "--style", "range"), "10.0.0.0/25\n10.0.0.128/25\n", 0, "10.0.0.0-10.0.0.255\n", ""),
                Arguments.of(List.of("count", "--mode", "hosts"), "10.0.0.0/24\n", 0, "254\n", ""));
    }

    @ParameterizedTest
    @MethodSource("rows")
    void rowsHold(List<String> args, String stdin, int status, String out, String err) {
        Rows.row(args, stdin, status, out, err);
    }
}
