parse_or_ascii = fn
  "0b" <> rest ->
    String.to_integer(rest, 2)

  "0o" <> rest ->
    String.to_integer(rest, 8)

  "0x" <> rest ->
    String.to_integer(rest, 16)

  part ->
    case Integer.parse(part) do
      {num, ""} -> num
      _ -> part |> String.to_charlist() |> List.first()
    end
end

IO.stream(:stdio, :line)
|> Enum.each(fn line ->
  line
  |> String.trim()
  |> String.split(" ", trim: true)
  |> Enum.map(parse_or_ascii)
  |> Enum.sum()
  |> IO.inspect(charlists: :as_lists)
end)
