import Data.Char (digitToInt)

replace :: Char -> Char
replace '#' = '1'
replace '.' = '0'
replace '\n' = '\n'
replace c = c

binToInt :: String -> Int
binToInt = foldl (\acc c -> acc * 2 + digitToInt c) 0 . filter (`elem` "01")

main = do
  input <- getContents
  let allLines = lines input
  let processed = map (binToInt . map replace) allLines
  mapM_ print processed
