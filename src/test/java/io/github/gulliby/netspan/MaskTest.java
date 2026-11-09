package io.github.gulliby.netspan;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MaskTest {
    static Stream<Arguments> rows() {
        return Stream.of(
                Arguments.of(List.of("sort", "--style", "mask"), "0.0.0.0/0\n", 0, "0.0.0.0 0.0.0.0\n", ""),
                Arguments.of(List.of("sort", "--style", "mask"), "128.0.0.0/1\n", 0, "128.0.0.0 128.0.0.0\n", ""),
                Arguments.of(List.of("sort", "--style", "mask"), "10.0.0.0/8\n", 0, "10.0.0.0 255.0.0.0\n", ""),
                Arguments.of(List.of("sort", "--style", "mask"), "172.16.0.0/12\n", 0, "172.16.0.0 255.240.0.0\n", ""),
                Arguments.of(List.of("sort", "--style", "mask"), "192.168.0.0/16\n", 0, "192.168.0.0 255.255.0.0\n", ""),
                Arguments.of(List.of("sort", "--style", "mask"), "10.1.0.0/17\n", 0, "10.1.0.0 255.255.128.0\n", ""),
                Arguments.of(List.of("sort", "--style", "mask"), "10.1.2.0/24\n", 0, "10.1.2.0 255.255.255.0\n", ""),
                Arguments.of(List.of("sort", "--style", "mask"), "10.1.2.128/25\n", 0, "10.1.2.128 255.255.255.128\n", ""),
                Arguments.of(List.of("sort", "--style", "mask"), "10.1.2.4/30\n", 0, "10.1.2.4 255.255.255.252\n", ""),
                Arguments.of(List.of("sort", "--style", "mask"), "10.1.2.6/31\n", 0, "10.1.2.6 255.255.255.254\n", ""),
                Arguments.of(List.of("sort", "--style", "mask"), "10.1.2.7\n", 0, "10.1.2.7 255.255.255.255\n", ""),
                Arguments.of(List.of("sort", "--style", "mask"), "255.255.255.254/31\n", 0, "255.255.255.254 255.255.255.254\n", ""));
    }

    @ParameterizedTest
    @MethodSource("rows")
    void rowsHold(List<String> args, String stdin, int status, String out, String err) {
        Rows.row(args, stdin, status, out, err);
    }

    static Stream<Arguments> totals() {
        return Stream.of(
                Arguments.of(List.of("sort", "--style", "mask"), "192.168.0.0/16\n10.0.0.0/8\n172.16.0.0/12\n10.0.0.0/24\n", 0, "10.0.0.0 255.0.0.0\n10.0.0.0 255.255.255.0\n172.16.0.0 255.240.0.0\n192.168.0.0 255.255.0.0\n", ""),
                Arguments.of(List.of("merge", "--style", "mask"), "10.0.0.0/25\n10.0.0.128/25\n", 0, "10.0.0.0 255.255.255.0\n", ""),
                Arguments.of(List.of("merge", "--style", "mask"), "10.0.0.0/24\n10.0.1.0/24\n10.0.2.0/23\n", 0, "10.0.0.0 255.255.252.0\n", ""),
                Arguments.of(List.of("merge", "--style", "mask"), "0.0.0.0/1\n128.0.0.0/2\n192.0.0.0/2\n", 0, "0.0.0.0 0.0.0.0\n", ""),
                Arguments.of(List.of("merge", "--style", "mask"), "10.0.0.0/8\n10.1.2.0/24\n", 0, "10.0.0.0 255.0.0.0\n", ""),
                Arguments.of(List.of("sort", "--style", "mask"), "1.1.1.1\n1.1.1.0\n1.1.0.0/16\n", 0, "1.1.0.0 255.255.0.0\n1.1.1.0 255.255.255.255\n1.1.1.1 255.255.255.255\n", ""),
                Arguments.of(List.of("merge", "--style", "mask"), "10.0.0.0/12\n192.3.104.0/22\n10.3.220.0/23\n192.2.117.52/30\n10.0.0.0/18\n11.3.0.0/20\n192.1.176.0/22\n11.3.148.8/29\n", 0, "10.0.0.0 255.240.0.0\n11.3.0.0 255.255.240.0\n11.3.148.8 255.255.255.248\n192.1.176.0 255.255.252.0\n192.2.117.52 255.255.255.252\n192.3.104.0 255.255.252.0\n", ""),
                Arguments.of(List.of("merge", "--style", "mask"), "10.0.0.0/13\n172.1.16.0/21\n192.2.227.128/25\n10.0.0.0/13\n192.1.90.0/24\n10.1.160.0/19\n172.1.0.0/16\n172.2.185.224/28\n192.3.0.0/17\n192.2.224.0/20\n192.3.112.0/23\n172.3.128.0/17\n192.2.106.240/28\n172.0.174.0/28\n", 0, "10.0.0.0 255.248.0.0\n172.0.174.0 255.255.255.240\n172.1.0.0 255.255.0.0\n172.2.185.224 255.255.255.240\n172.3.128.0 255.255.128.0\n192.1.90.0 255.255.255.0\n192.2.106.240 255.255.255.240\n192.2.224.0 255.255.240.0\n192.3.0.0 255.255.128.0\n", ""),
                Arguments.of(List.of("merge", "--style", "mask"), "192.0.2.0/25\n192.0.2.128/26\n192.0.2.192/27\n192.0.2.224/27\n", 0, "192.0.2.0 255.255.255.0\n", ""));
    }

    @ParameterizedTest
    @MethodSource("totals")
    void totalsHold(List<String> args, String stdin, int status, String out, String err) {
        Rows.row(args, stdin, status, out, err);
    }
}
