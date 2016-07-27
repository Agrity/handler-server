#!/usr/bin/env ruby

require 'csv'

if !ARGV[0] then
  puts 'ERROR: Please give csv file to load.'
  exit
end

skip = true

CSV.foreach(ARGV[0]) do |row|
  if skip then
    if row[0] == '~~~' then
      skip = false
    end
    next
  end

  system("./new-csv-trader.sh '#{row[0]}' '#{row[1]}' '#{row[2]}' '#{row[3]}' '#{row[4]}' '#{row[5]}'>> result")
  system("echo >> result")
  system("echo >> result")
end
