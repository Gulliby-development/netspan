<p align="center"><img src="docs/logo.svg" width="96" alt="" /></p>

# netspan

[![CI](https://github.com/Gulliby-development/netspan/actions/workflows/ci.yml/badge.svg)](https://github.com/Gulliby-development/netspan/actions/workflows/ci.yml) [![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

netspan is a Java command-line tool for checking and working with IPv4 network lists. It is intended for people maintaining firewall rules, routing tables, and allow lists, and for use in terminals, scripts, and CI. It uses only the Java standard library at runtime; it needs no network connection or clock.

Java 21 or newer and Maven are required to compile the project. The same Java-based invocation works on Windows, macOS, and Linux.

## Usage

Compile from the repository directory:

```text
mvn -q compile
```

Then invoke the entry point with the command and its arguments:

```text
java -cp target/classes io.github.gulliby.netspan.Main COMMAND
```

Each command reads its network list from standard input. The synopsis, in command order, is:

```text
usage: netspan check
       netspan sort [--style STYLE]
       netspan merge [--style STYLE]
       netspan count [--mode MODE]
       netspan gaps --within NET [--style STYLE]
       netspan contains --ip IP
       netspan split --prefix N [--style STYLE]
```

`check` accepts no options. `sort` and `merge` accept `--style`; `count` accepts `--mode`; `gaps` requires `--within` and optionally accepts `--style`; `contains` requires `--ip`; and `split` requires `--prefix` and optionally accepts `--style`. Style defaults to `cidr`, and count mode defaults to `addresses`. `--style` accepts `cidr`, `range`, `mask`, or `hex`; `--mode` accepts `addresses` or `hosts`. `--within` takes one aligned network in the input network format, while `--ip` takes one IPv4 address without a slash. `--prefix` takes a whole number from 0 through 32 written as one or two decimal digits with no leading zero (so `0` is valid and `05` is not). Each option consumes the next word as its value even when that word looks like an option. There is no `--option=value` syntax. Option names, values, and command names are case-sensitive.

The first argument `help`, `-h`, or `--help` prints the synopsis; `--version` prints exactly `netspan 0.1.0`. Any arguments after either are ignored. With no command, the synopsis is printed as an error.

The examples below use a POSIX shell function, defined from the repository directory after compiling:

```sh
netspan() { java -cp target/classes io.github.gulliby.netspan.Main "$@"; }
```

This function and the example shell syntax are POSIX-specific, not native Windows command syntax. On Windows, use the Java invocation above in a supported terminal or script; Java itself is available cross-platform.

## Networks

A network is a strict dotted-decimal IPv4 address, optionally followed by a slash and prefix length. An address has four decimal octets from 0 to 255, separated by dots. Octets have no sign or leading zero (so `01` and `256` are invalid). A prefix is from 0 to 32, with no leading zero; an address without a prefix means a single-address /32 network.

A network must be aligned to its prefix: its address is the first address in the block. For example, `10.0.0.0/24` is aligned, while `10.0.0.1/24` has host bits set. A /32 is always aligned. Each block includes every address from its first through its last address.

Input is processed in order, one network per physical line. Spaces and tabs at line ends are ignored, and spaces and tabs separate words; every non-skipped line must contain exactly one network. Blank lines and lines whose first word begins with `#` are skipped. One carriage return at the end of each line is removed. Lines are numbered from 1, including skipped lines; a final line without a line feed still counts. The maximum is 40 physical lines, including blank and comment lines.

## Styles

The `--style` option selects how a block is printed:

- `cidr`: first address and slash-prefix, such as `10.0.0.0/8`; single addresses print with /32.
- `range`: inclusive first and last addresses separated by a hyphen, such as `10.0.0.0-10.255.255.255`.
- `mask`: first address, a space, then the dotted netmask, such as `10.0.0.0 255.0.0.0`. Prefix 0 uses `0.0.0.0`; prefix 32 uses `255.255.255.255`.
- `hex`: first address as eight uppercase hexadecimal digits, then slash and decimal prefix, such as `0A000000/8`.

## Output

Successful commands write their result to standard output and nothing to standard error. Text output uses line feeds.

- `check` validates the list and prints `ok, N network(s)`, using the singular for one.
- `sort` prints all networks by first address, then by prefix length from shortest to longest. It preserves duplicates.
- `merge` prints the smallest ordered CIDR cover of the same addresses. Overlapping or touching coverage is joined before being decomposed into blocks; gaps remain gaps.
- `count` prints a decimal total of unique covered addresses using long-sized arithmetic. In `addresses` mode, every covered address counts once. In `hosts` mode, it counts addresses in the merged cover, subtracting two from each block of prefix 0 through 30 and subtracting nothing from /31 or /32 blocks.
- `gaps` prints the smallest ordered blocks inside the requested `--within` network that are not covered by the input. Coverage outside the boundary is ignored and crossing coverage is clipped to it. If fully covered, it prints `none`.
- `contains` prints one line for each input network that inclusively contains the requested address, exactly `IP in NET` followed by a line feed. It preserves input order and duplicates, uses the requested IP text for `IP`, and reports each matching network as canonical CIDR for `NET`.
- `split` prints each input network's subnets at the requested prefix, in input order and ascending order within each network. Networks already at that prefix remain unchanged. It first checks all networks for feasibility, then enforces an aggregate maximum of 256 output subnets.

## Examples

The `netspan` name below is the POSIX shell function shown under Usage. These examples are run from the repository directory after `mvn -q compile`; `2>&1` sends error output to the displayed stream.

```text
$ printf '10.0.0.0/25\n10.0.0.128/25\n10.0.1.0/24\n' | netspan merge
10.0.0.0/23
$ printf '10.0.0.0/24\n192.168.1.0/30\n' | netspan sort --style range; printf '172.16.0.0/12\n' | netspan sort --style mask
10.0.0.0-10.0.0.255
192.168.1.0-192.168.1.3
172.16.0.0 255.240.0.0
$ printf '10.0.0.0/24\n10.0.0.64/26\n' | netspan gaps --within 10.0.0.0/23; printf '10.0.0.0/30\n' | netspan split --prefix 31 --style hex
10.0.1.0/24
0A000000/31
0A000002/31
$ printf '10.0.0.0/8\n10.0.0.1/24\n' | netspan check 2>&1; printf '10.0.0.0/8\n' | netspan contains 2>&1
netspan: line 2: host bits set 10.0.0.1/24
netspan: missing --ip
```

## Errors

Exit status 0 means success. Status 1 means an invalid input list or a command-level problem; status 2 means an invalid command line. Failures normally leave standard output empty and write one line to standard error, prefixed with `netspan: `. The exception is a valid `contains` request with no match: it prints `IP is not covered` to standard output, exits 1, and leaves standard error empty. With no command, usage goes to standard error and status is 2. Successful output and errors end with a line feed.

In the templates below, `WORD` is the relevant argument or input word, `OPTION` is an option name, `COMMAND` is a command name, `N` is a physical input line number, `L` is a split input line number, and `NET`, `IP`, and `/N` are the supplied network, address, and prefix.

Command-line errors are checked before reading the list. An argument after the command that does not begin with `-`, or is empty or exactly `-`, is an unexpected argument; a word beginning with `-` that is not a recognized option is an unknown option. Option errors are considered left to right: an unrecognized non-option or option, an option invalid for the selected command, a duplicate option, a missing option value, and then a bad value are reported as encountered. Each option consumes the next word as its value regardless of its appearance; an option at the end has a missing value. Required-option checks happen after scanning the arguments. Invalid command lines, help, and version do not read standard input. If the command line is valid, list validation happens before command behavior. More than 40 physical lines takes precedence over line-specific errors; otherwise the first invalid line is reported.

The exact error messages are:

- `netspan: unknown command WORD`
- `netspan: unexpected argument WORD`
- `netspan: unknown option WORD`
- `netspan: option OPTION is not valid for COMMAND`
- `netspan: given twice: OPTION`
- `netspan: missing value for OPTION`
- `netspan: bad value for OPTION`
- `netspan: missing --within`, `netspan: missing --ip`, or `netspan: missing --prefix`
- `netspan: no networks`
- `netspan: input is too long (max 40 lines)`
- `netspan: line N: expected 1 network`
- `netspan: line N: bad network WORD`
- `netspan: line N: host bits set WORD`
- `netspan: line L: cannot split WORD into /N`
- `netspan: too many subnets (max 256)`

For `contains` without a match, the normal-output line is `IP is not covered`, not a prefixed error. The complete license terms are in [LICENSE](LICENSE).

## Development

Run the full Maven checks with:

```text
mvn -B -q verify
```

Maven uses Java 21 and the existing JUnit 5 test dependency; netspan has no third-party runtime dependencies. The command-line logic is exposed through the in-memory `Cli.run(List<String>, String)` interface, which returns an `Api.Result` containing status, output, and error text. `Cli.readsStdin(List<String>)` indicates whether a valid command invocation needs list input. `Api.Failure` carries a status and message for list or command failures. Stream access belongs to `Main`, which reads input only for valid command invocations and writes the result's two output texts.

---

Built with [BAKED](https://github.com/Gulliby-development/baked), an open-source coding agent (bench round R102).
