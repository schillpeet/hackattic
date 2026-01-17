IO.stream(:stdio, :line)
|> Enum.each(fn line ->
  line
  |> String.to_charlist()
  |> Enum.reduce_while([], fn item, acc ->
    case item do
      ?( ->
        {:cont, [item | acc]}

      ?) ->
        case acc do
          [] -> {:halt, :error_stack_empty}
          [_head | tail] -> {:cont, tail}
        end

      _ ->
        {:cont, acc}
    end
  end)
  |> case do
    [] -> IO.puts("yes")
    _error -> IO.puts("no")
  end
end)
