STDIN.each do |line|
  clean_line = line.strip
  next if clean_line.empty?
  result = line.chars.chunk{|char| char}.to_a.map do |char, group|
    group.size != 1 ? group.size != 2 ? "#{group.size}#{char}" : "#{char}#{char}" : char 
  end
  puts result.join("")
end