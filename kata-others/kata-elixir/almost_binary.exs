IO.stream(:stdio, :line)
|> Enum.each(fn line ->
  line
  |> String.trim()
  |> String.replace("#", "1")
  |> String.replace(".", "0")
  |> String.to_integer(2)
  |> IO.puts()
end)
