#!/usr/bin/python3
import sys
import re
import csv

field_name = ['Mutation Score']
mut_value = []

lines = [line.strip('\n') for line in sys.stdin]

for line in lines:
    m = re.match('.*Mutation score.*?(?P<mutation_score>\S+)%.*', line)
    if m:
        mut_value.append(float(m.group('mutation_score')) / 100)

with open('mutation_score.csv', 'w', newline='') as mut_file:
    writer = csv.writer(mut_file)
    writer.writerow(field_name)
    writer.writerow(mut_value)

