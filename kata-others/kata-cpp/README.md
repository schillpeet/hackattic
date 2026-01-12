# Play with C++

For quick experiments and one-off runs, you can compile and execute C++ files without leaving binaries behind.

### One-liner approach

Compile and run directly to a temporary location:

```shell
$ g++ foobar.cpp -o /tmp/tmpprog && /tmp/tmpprog
```

## Make it convenient with direnv

To avoid typing the full command every time, this project uses [direnv](https://direnv.net/) to expose a small helper script.

### Setup

Install `direnv` via homebrew:

> brew install direnv

Enable `direnv` in your shell (e.g. add it to the plugin section of your `.zshrc`).

Make the helper script executable:

> chmod +x scripts/gpp-fast

Allow direnv to load the local environment:

> direnv allow .

### Usage

Once enabled, you can compile and run a C++ file with:

> gpp-fast <your_cpp_program.cpp>

The program is compiled into /tmp, executed immediately, and discarded afterwards.

No clutter. No cleanup. Just C++.
