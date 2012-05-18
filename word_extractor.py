#!/usr/local/bin/python

import difflib
import fileinput
import pprint

VERBOSE = False

# Splits a string
def split(str):
    # TODO: Probably want to use something more clever than simply splitting on
    #       whitespace
    return str.split()

# Computes overlap of two sets
def overlap(set1, set2):
    # Computes proportion of set intersection to set union
    return float(len(set1 & set2)) / float(len(set1 | set2))

# Computes partial overlap of two sets
def partial_overlap(set1, set2):
    # Computes the disjoint sets
    disjoint_set1 = set1 - set2
    disjoint_set2 = set2 - set1
    
    partial_set1 = set()
    # Iterates through first disjoint set and finds all close matches to second
    for elem in disjoint_set1:
        matches = difflib.get_close_matches(elem, disjoint_set2)
        
        # Checks if close matches exist
        if len(matches):
            if VERBOSE:
                print 'found close matches for %s' % (elem)
            # Adds element to set of partial matches
            partial_set1.add(elem)

    partial_set2 = set()
    # Iterates through second disjoint set and finds all close matches to first
    for elem in disjoint_set2:
        matches = difflib.get_close_matches(elem, disjoint_set1)
        
        # Checks if close matches exist
        if len(matches):
            if VERBOSE:
                print 'found close matches for %s' % (elem)
            # Adds element to set of partial matches
            partial_set2.add(elem)
    
    # Computes maximum proportion of partial matches to set union
    return max(float(len(partial_set1)) / float(len(set1 | set2)),
               float(len(partial_set2)) / float(len(set1 | set2)))

# Displays the overlap and partial overlap of two files
if __name__ == '__main__':
    pp = pprint.PrettyPrinter(indent=4)
    
    sets = []
    s = set()
    
    for line in fileinput.input():
        if fileinput.isfirstline():
            # Checks for EOF
            if fileinput.lineno() != 1:
                if VERBOSE:
                    pp.pprint(s)
                sets.append(s)
                s = set()
        
        # Adds each word to set
        for word in split(line):
            s.add(intern(word))
    
    if VERBOSE:
        pp.pprint(s)
    sets.append(s)
    
    print 'overlap: %0.4f' % (overlap(sets[0], sets[1]))
    print 'partial overlap: %0.4f' % (partial_overlap(sets[0], sets[1]))