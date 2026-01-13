STDIN.each do |line|
  prefix = line.index(/[A-Z]/)
  puts prefix < 4 ? 
    line.gsub(/[A-Z]/) { |char| '_' + char.downcase }[prefix+1..] : 
    line.gsub(/[A-Z]/) { |char| '_' + char.downcase }
end