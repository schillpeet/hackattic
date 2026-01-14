require 'json'

all_entries = []
STDIN.each do |obj|
  input = JSON.parse(obj, symbolize_names: true)
  name, details = input.first
  balance = input.dig(:extra, :balance) || details.dig(:extra, :balance) || details[:balance]

  all_entries << [name, balance]
end

all_entries.sort_by! { |name, balance| balance }

all_entries.each do |name, balance|
  formatted_balance = balance.to_s.gsub(/\d(?=(\d{3})+$)/, '\0,')
  puts "#{name}: #{formatted_balance}"
end