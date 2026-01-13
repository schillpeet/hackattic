STDIN.each do | line |
  the_start, the_end = line.split.map(&:to_i)
  (the_start..the_end).each do | el |
    res = "#{"Fizz" if el % 3 == 0}#{"Buzz" if el % 5 == 0}"
    puts res.empty? ? el : res
  end
end