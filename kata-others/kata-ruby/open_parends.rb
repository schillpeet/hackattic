STDIN.each do | line |
  stack = []
  line.chomp.chars.each do | el |
    case
    when stack.last == "(" && el == ")"
      stack.pop()
    else
      stack.push(el)
    end
  end
  puts stack.empty? ? "yes" : "no"
end
