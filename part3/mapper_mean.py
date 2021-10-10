#!/usr/bin/env python

import sys

price = 0
chunk_sum = 0
chunk_size = 0

# input comes from STDIN (standard input)
for line in sys.stdin:

    # remove leading and trailing whitespace
    line = line.strip()

    # split the line into words
    words = line.split('~')

    # calculation
    try:
        price = int(words[10])
        chunk_sum += price
        chunk_size += 1
    except:
        pass

print(f'{chunk_sum}~{chunk_size}')