#!/usr/bin/env python3

import sys

mean_prices_all = 0
count_prices_all = 0
var_prices_all = 0

for line in sys.stdin:
    line = line.strip()
    values = line.split(' ')
    count_prices = int(values[0])
    mean_prices = float(values[1])
    var_prices = float(values[2])
    
    var_prices_all = (count_prices_all * var_prices_all + count_prices * var_prices) / (count_prices_all + count_prices) + count_prices_all * count_prices * ((mean_prices_all - mean_prices) / (count_prices_all + count_prices))**2
    mean_prices_all = (count_prices_all * mean_prices_all + count_prices * mean_prices) / (count_prices_all + count_prices)
    count_prices_all += count_prices
    
print(var_prices_all)
