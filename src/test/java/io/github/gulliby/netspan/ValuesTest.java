package io.github.gulliby.netspan;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ValuesTest {
    static Stream<Arguments> rows() {
        return Stream.of(
                Arguments.of(List.of("sort", "--style", "Cidr"), "", 2, "", "netspan: bad value for --style\n"),
                Arguments.of(List.of("sort", "--style", ""), "", 2, "", "netspan: bad value for --style\n"),
                Arguments.of(List.of("count", "--mode", "all"), "", 2, "", "netspan: bad value for --mode\n"),
                Arguments.of(List.of("split", "--prefix", "33"), "", 2, "", "netspan: bad value for --prefix\n"),
                Arguments.of(List.of("split", "--prefix", "05"), "", 2, "", "netspan: bad value for --prefix\n"),
                Arguments.of(List.of("split", "--prefix", "-1"), "", 2, "", "netspan: bad value for --prefix\n"),
                Arguments.of(List.of("split", "--prefix", "2.5"), "", 2, "", "netspan: bad value for --prefix\n"),
                Arguments.of(List.of("split", "--prefix", ""), "", 2, "", "netspan: bad value for --prefix\n"),
                Arguments.of(List.of("gaps", "--within", "10.0.0.1/24"), "", 2, "", "netspan: bad value for --within\n"),
                Arguments.of(List.of("gaps", "--within", "300.0.0.0/8"), "", 2, "", "netspan: bad value for --within\n"),
                Arguments.of(List.of("gaps", "--within", "10.0.0.0/"), "", 2, "", "netspan: bad value for --within\n"),
                Arguments.of(List.of("gaps", "--within", "10.0.0.0/33"), "", 2, "", "netspan: bad value for --within\n"),
                Arguments.of(List.of("contains", "--ip", "1.2.3"), "", 2, "", "netspan: bad value for --ip\n"),
                Arguments.of(List.of("contains", "--ip", "1.2.3.4/5"), "", 2, "", "netspan: bad value for --ip\n"),
                Arguments.of(List.of("contains", "--ip", "01.2.3.4"), "", 2, "", "netspan: bad value for --ip\n"),
                Arguments.of(List.of("count", "--mode", "addresses"), "10.0.0.0/30\n", 0, "4\n", ""),
                Arguments.of(List.of("count", "--mode", "hosts"), "10.0.0.0/31\n10.0.0.2/32\n", 0, "3\n", ""),
                Arguments.of(List.of("split", "--prefix", "31", "--style", "range"), "10.0.0.0/30\n", 0, "10.0.0.0-10.0.0.1\n10.0.0.2-10.0.0.3\n", ""));
    }

    @ParameterizedTest
    @MethodSource("rows")
    void rowsHold(List<String> args, String stdin, int status, String out, String err) {
        Rows.row(args, stdin, status, out, err);
    }
}
