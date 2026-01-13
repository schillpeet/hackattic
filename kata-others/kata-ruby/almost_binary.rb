STDIN.each do | line |
  bin = line.gsub("#", "1").gsub(".", "0").to_i(2)
  puts bin
end