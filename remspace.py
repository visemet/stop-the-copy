#!/usr/local/bin/python

import fileinput

for line in fileinput.input():
  output = ' '.join(line.split())
  if output:
    print output
