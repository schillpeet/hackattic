defmodule Checker do
  def check(n) do
    case {rem(n, 3), rem(n, 5)} do
      {0, 0} -> IO.puts("FizzBuzz")
      {0, _} -> IO.puts("Fizz")
      {_, 0} -> IO.puts("Buzz")
      _ -> IO.puts(n)
    end
  end
end

IO.stream(:stdio, :line)
|> Enum.each(fn line ->
  [first, last] =
    line
    |> String.split()
    |> Enum.map(&String.to_integer/1)

  Enum.each(first..last, &Checker.check/1)
end)
