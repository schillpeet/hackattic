import Data.Char (isUpper, toLower, toUpper)
import Data.List (intercalate)

countUpper :: String -> Int
countUpper word = length (filter isUpper word)

process :: String -> String
process raw =
  let output = concatMap (\c -> if isUpper c then ['_', c] else [c]) raw
      cu = countUpper output
      temp =
        if cu > 1 && (length (takeWhile (/= '_') output) < 4)
          then drop 1 (dropWhile (/= '_') output)
          else output
   in map toLower temp

main :: IO ()
main = interact (unlines . map processLine . lines)
  where
    processLine = process