IO.stream(:stdio, :line)
|> Enum.each(fn line ->
  snake = String.replace(line, ~r/[A-Z]/, &("_" <> String.downcase(&1))) |> String.trim()

  parts = String.split(snake, "_")
  [prefix | rest_list] = parts

  if(length(parts) > 2 && String.length(prefix) < 4, do: Enum.join(rest_list, "_"), else: snake)
  |> IO.puts()
end)
