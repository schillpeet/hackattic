printTop :: Int -> IO ()
printTop x = putStrLn $ case (x `mod` 3 == 0, x `mod` 5 == 0) of
    (True, True)    -> "FizzBuzz"
    (True, False)   -> "Fizz"
    (False, True)   -> "Buzz"
    (False, False)  -> show x

main :: IO()
main = getLine >>= \line ->
    let ints = map read (words line) in
    if null ints
        then putStr "Empty Input"
        else mapM_ printTop [minimum ints .. maximum ints]
