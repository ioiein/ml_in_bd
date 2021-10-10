#!/usr/bin/env python3

import sys

prices_sum = 0
prices_count = 0
prices_squares_sum = 0

for line in sys.stdin:
    line = line.strip()
    values = line.split(',')
    
    if len(values) < 10 or not values[9].isdigit():
        continue
    prices_count += 1
    prices_sum += float(values[9])
    prices_squares_sum += float(values[9])**2
    
print(prices_count, prices_sum / prices_count, prices_squares_sum / prices_count - (prices_sum / prices_count)**2)