require "base64"

STDIN.each do | line |
  puts Base64.decode64(line)
end