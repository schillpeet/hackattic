IO.stream(:stdio, :line)
|> Enum.each(fn line ->
  line |> String.trim() |> Base.decode64!() |> IO.puts()
end)
