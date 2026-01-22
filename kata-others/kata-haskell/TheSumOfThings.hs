import Data.Char (digitToInt, isDigit, ord)
import Numeric (readHex, readOct)

binStrToInt :: String -> Int
binStrToInt = foldl (\acc b -> acc * 2 + if b == '1' then 1 else 0) 0

octStrToInt :: String -> Int
octStrToInt s = fst (head (readOct s))

hexStrToInt :: String -> Int
hexStrToInt s = fst (head (readHex s))

map0box :: String -> Int
map0box ('0' : t : ts)
  | t == 'b' = binStrToInt ts
  | t == 'o' = octStrToInt ts
  | t == 'x' = hexStrToInt ts
map0box [s]
  | isDigit s = digitToInt s
  | otherwise = ord s
map0box s
  | all isDigit s = read s
  | otherwise = error "invalid input"

processLine :: String -> String
processLine line =
  let parts = words line
      numbers = map map0box parts
      total = sum numbers
   in show total

main :: IO ()
main = do
  interact (unlines . map processLine . lines)
