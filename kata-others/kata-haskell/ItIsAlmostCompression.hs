import Data.List (group)

formatGroup :: String -> String
formatGroup g
  | len == 1 || len == 2 = g
  | otherwise = show len ++ [head g]
  where
    len = length g

main :: IO ()
main = do
  input <- getContents
  let processed = map (concatMap formatGroup . group) (lines input)
  mapM_ putStrLn processed