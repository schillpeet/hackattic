IO.stream(:stdio, :line)
|> Enum.each(fn line ->
  line
  |> String.trim()
  |> String.graphemes()
  |> Enum.chunk_by(fn char -> char end)
  |> Enum.map(fn list ->
    if length(list) <= 2 do
      Enum.join(list)
    else
      "#{length(list)}#{List.first(list)}"
    end
  end)
  |> Enum.join()
  |> IO.puts()
end)
