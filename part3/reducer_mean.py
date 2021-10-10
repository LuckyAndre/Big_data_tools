#!/usr/bin/env python

import sys

total_sum = 0
total_size = 0

# input comes from STDIN
for line in sys.stdin:

    # remove leading and trailing whitespace
    line = line.strip()

    # calculation
    try:
        chunk_sum, chunk_size = map(int, line.split('~'))
        total_sum += chunk_sum
        total_size += chunk_size
    except ValueError:
        continue

print(total_sum / total_size)