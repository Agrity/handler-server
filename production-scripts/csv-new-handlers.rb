#!/usr/bin/env ruby

require 'csv'

if !ARGV[0] then
  puts 'ERROR: Please give csv file to load.'
  exit
end

CSV.foreach(ARGV[0]) do |row|
  #puts "#{row[0]} #{row[1]} #{row[2]}"
  system("./new-handler.sh '#{row[0]}' '#{row[1]}' '#{row[2]}' >> result")
  system("echo >> result")
  system("echo >> result")
end
