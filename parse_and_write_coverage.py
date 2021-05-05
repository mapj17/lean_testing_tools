#!/usr/bin/python3
import sys
import re
import csv

field_names = []
coverage_values = []
statements = []
misses = []

lines = [line.strip('\n') for line in sys.stdin]

for line in lines:
    print('Reading line: ' + line)
    m = re.match('(?P<file>\S+)\s*(?P<statements>\S+)\s*(?P<misses>\S+)\s+(?P<coverage>\d+)%',line)
    field_names.append(m.group('file'))
    statements.append(int(m.group('statements')))
    misses.append(int(m.group('misses')))
    coverage_values.append(int(m.group('coverage')) / 100)
print('Before aggregation we have: ')
print(field_names)
print(statements)
print(misses)
print(coverage_values)
field_names.append('TOTAL')
coverage_values.append(round((sum(statements) - sum(misses))/sum(statements), 2))

with open('coverage.csv', 'w', newline='') as coverage_file:
    writer = csv.writer(coverage_file)
    writer.writerow(field_names)
    writer.writerow(coverage_values)
