STDIN.each_line do |line|
  puts line.split.sum { |thing|
    Integer(thing) rescue (thing.size == 1 ? thing.ord : 0)
  }
end

# EAFP: "Easier to Ask for Forgiveness than Permission"
# 0b100 q 0o313 0x132 0o272 f
# actual: 1986
# expected: 914