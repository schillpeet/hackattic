STDIN.each do | line |
  first, last = line.split.map(&:to_i)
  (first..last).each do | el |
    res = "#{"Fizz" if el % 3 == 0}#{"Buzz" if el % 5 == 0}"
    puts res.empty? ? el : res
  end
end