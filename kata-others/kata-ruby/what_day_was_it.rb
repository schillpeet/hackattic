STDIN.each do |line|
  puts Time.at(line.to_i * 86_400).strftime("%A")
end