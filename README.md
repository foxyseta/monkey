# 🅼🐵🅽🅺ey

Project for the University of Bologna Data Structure and Algorithms course
(a.y. 2020-21).

## Prerequisites

- any Java Development Kit
- `make`

## Commands

### Cleaning

`clean-docs` removes the documentation, while `clean-build` removes the results
of the previoud build process. `clean` does both.

### Docs

`make docs` generates the relevant documentation.

### Building

`make build` builds the whole project from zero. Use `make build-debug` instead
if you plan on using the debugger as well.

### Testing

`make test` runs some tests which are not used during the main execution due to
performance reasons. Use `make test-debug` instead if you plan on using the
debugger as well.

### Running

`make run MNK="5 4 3"` executes a _5-4-3-game_. `make run` (with no additional
parameters) executes a game of tic-tac-toe (_3-3-3-game_). Use `run-debug` if
you plan on using the debugger as well.
