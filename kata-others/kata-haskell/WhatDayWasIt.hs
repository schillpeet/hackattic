import Data.Time.Calendar
import Data.Time.Clock (addUTCTime)
import Data.Time.Clock.POSIX (posixSecondsToUTCTime)
import Data.Time.Format

getWeekday :: Int -> String
getWeekday days =
  let epoch = posixSecondsToUTCTime 0
      seconds = fromIntegral days * 86400
      targetDate = addUTCTime seconds epoch
   in formatTime defaultTimeLocale "%A" targetDate

main :: IO ()
main = do
  input <- getContents
  let results = map (getWeekday . read) (lines input)
  mapM_ putStrLn results
