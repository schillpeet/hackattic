import Data.Char (chr, intToDigit)
import Data.List (elemIndex)
import Data.Maybe (fromMaybe)
import Numeric (showIntAtBase)

toBinary :: Int -> String
toBinary n
  | n < 0 = "" -- padding sign (=)
  | otherwise =
      let s = showIntAtBase 2 intToDigit n ""
       in replicate (6 - length s) '0' ++ s

-- creates a long binary string (based on 6bit*x)
toBinary6Bit :: String -> String
toBinary6Bit [] = ""
toBinary6Bit (c : cs) =
  let abc64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
      idx = fromMaybe (-1) (elemIndex c abc64)
      binStr = toBinary idx
   in binStr ++ toBinary6Bit cs

-- Now I cut the long bin string into 8-bit parts and map the bin numbers to the ASCII numbers.
binStrToInt :: String -> Int
binStrToInt "" = 0
binStrToInt (b : bs) =
  let bit = if b == '1' then 1 else 0
   in bit * (2 ^ length bs) + binStrToInt bs

mapBinStrToInt :: String -> [Int]
mapBinStrToInt s
  | length s < 8 = []
  | otherwise =
      let (byte, rest) = splitAt 8 s
       in binStrToInt byte : mapBinStrToInt rest

decodeToString :: [Int] -> String
decodeToString = map chr

main = do
  input <- getContents
  let linesOfInput = lines input
  let results = map processLine linesOfInput
  mapM_ putStrLn results

processLine :: String -> String
processLine line =
  let binaryString = toBinary6Bit line
      intList = mapBinStrToInt binaryString
   in decodeToString intList
