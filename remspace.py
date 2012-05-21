#!/usr/local/bin/python

import fileinput, string

for line in fileinput.input():
  output = ' '.join(line.split())
  if output:
    print output.lower()
