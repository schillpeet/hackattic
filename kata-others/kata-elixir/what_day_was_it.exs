IO.stream(:stdio, :line)
|> Enum.each(fn line ->
  line
  |> String.trim()
  |> String.to_integer()
  |> then(&(&1 * 86_400))
  |> DateTime.from_unix!()
  |> DateTime.to_date()
  |> Calendar.strftime("%A")
  |> IO.puts()
end)
