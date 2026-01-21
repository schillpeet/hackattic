main = do
  input <- getContents
  let results = map (\l -> if analyze l [] then "yes" else "no") (lines input)
  mapM_ putStrLn results

analyze :: String -> [Char] -> Bool
analyze "" stack = null stack
analyze (x : xs) stack
  | x == '(' = analyze xs (x : stack)
  | otherwise = case stack of
      [] -> False
      (_ : ss) -> analyze xs ss
