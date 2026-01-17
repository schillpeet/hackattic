format_num = fn num ->
  num
  |> String.reverse()
  |> String.replace(~r/(\d{3})(?=\d)/, "\\1,")
  |> String.reverse()
end

IO.stream(:stdio, :line)
|> Enum.map(fn line ->
  line = String.trim(line)

  [raw_name | _rest] = String.split(line, ":", parts: 2)
  name = String.replace(raw_name, ~r/[{}"]/, "")

  balance =
    case Regex.run(~r/"extra":\s*\{[^}]*"balance":\s*(-?\d+)/, line) do
      [_, val] ->
        val

      nil ->
        case Regex.run(~r/"balance":\s*(-?\d+)/, line) do
          [_, val] -> val
          _ -> "0"
        end
    end

  {String.to_integer(balance), name}
end)
|> Enum.sort()
|> Enum.each(fn {balance, name} ->
  IO.puts("#{name}: #{format_num.(Integer.to_string(balance))}")
end)

# {"Alton.K":{"balance":9315,"account_no":203123613,"extra":{"balance":131}}}
# {"Bancroft.M":{"balance": 233,"account_no":287655771101,"extra":{"balance":98}}}
# {"Barclay.E":{"balance":1123,"account_no":312333321}}

# {"Bentley.G":{"balance":2134,"account_no":233831255"}}
