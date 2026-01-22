import Data.List (isInfixOf, isPrefixOf, sortBy)
import Data.Ord (comparing)
import Text.Printf (printf)

data Account = Account {name :: String, balance :: Int} deriving (Show)

extractName :: String -> String
extractName s = case dropWhile (/= '"') s of
  ('"' : rest) -> takeWhile (/= '"') rest
  _ -> "Unbekannt"

findBalance :: String -> Int
findBalance [] = 0
findBalance s
  | "\"balance\":" `isPrefixOf` s =
      let afterKey = drop 10 s
          digits = takeWhile (`elem` "0123456789") (dropWhile (`notElem` "0123456789") afterKey)
       in if null digits then 0 else read digits
  | otherwise = findBalance (tail s)

parseLine :: String -> Account
parseLine line =
  let n = extractName line
      hasExtra = "\"extra\"" `isInfixOf` line
      (before, after) = splitAtExtra line
      bal = if hasExtra then findBalance after else findBalance before
   in Account n bal

splitAtExtra :: String -> (String, String)
splitAtExtra s = go s ""
  where
    go [] acc = (acc, "")
    go res@(x : xs) acc
      | "\"extra\"" `isPrefixOf` res = (acc, res)
      | otherwise = go xs (acc ++ [x])

formatNum :: Int -> String
formatNum n = reverse $ insertCommas $ reverse $ show n
  where
    insertCommas (a : b : c : rest@(_ : _)) = a : b : c : ',' : insertCommas rest
    insertCommas xs = xs

processAll :: String -> String
processAll input =
  let rows = lines input
      accounts = map parseLine rows
      sortedAccounts = sortBy (comparing balance) accounts
      resultLines = map (\a -> name a ++ ": " ++ formatNum (balance a)) sortedAccounts
   in unlines resultLines

main :: IO ()
main = do
  input <- getContents
  putStr (processAll input)

{-
{"Bentley.G":{"balance":2134,"account_no":233831255"}}
{"Barclay.E":{"balance":1123,"account_no":312333321}}
{"Alton.K":{"balance":9315,"account_no":203123613,"extra":{"balance":131}}}
{"Bancroft.M":{"balance": 233,"account_no":287655771101,"extra":{"balance":98}}
-}