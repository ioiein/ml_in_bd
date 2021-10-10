#!/usr/bin/env python3

import sys

sum_prices_all = 0
count_prices_all = 0

for line in sys.stdin:
    line = line.strip()
    values = line.split(' ')
    prices_sum = float(values[0])
    prices_count = int(values[1])
    sum_prices_all += prices_sum
    count_prices_all += prices_count
    
print(sum_prices_all / count_prices_all)