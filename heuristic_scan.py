from difflib import *
from types import *
import sys, os, string, subprocess, math

latest_results = None

# Strips a file of its comments and whitespace, then returns the lines
def strip_file(filename):
  command="sed -nf remccoms3.sed " + filename + " | remspace.py"
  process = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)

  out, err = process.communicate()
  
  # OH SHIT SOMETHING BAD HAPPENED, probably there wasn't a file, so let's
  # just return none!
  if err:
    return None

  output=open(filename+".stripped", "w")
  output.write(out)
  output.close()
  
  lines = out.splitlines(1)
  
  return lines

# Computes a ratio for each file, then deletes the matching blocks, and
# recomputes the ratio with the truncated lines
def strict_ratio(_f1, _f2, num_itrs = 1):
  f1 = _f1[:]
  f2 = _f2[:]

  accum = 0
  tot_len = len(f1) + len(f2)

  for durrrhurrrr in range(num_itrs):
    seq = SequenceMatcher(None, f1, f2)
    ratio = seq.ratio()

    # The weight is given by the number of lines matching
    accum += ratio*(len(f1) + len(f2))

    # Splice out the matched blocks, and re-run
    blocks = seq.get_matching_blocks()
    if len(blocks) == 0:
      break

    for i in sorted(range(len(blocks)), reverse=True):
      f1_start = blocks[i][0]
      f2_start = blocks[i][1]
      run_len  = blocks[i][2]

      del f1[f1_start:f1_start + run_len]
      del f2[f2_start:f2_start + run_len]

  return accum/tot_len

# Returns the fuzzy ratio that does not count ? mark lines
def loose_ratio(diff, f1, f2):
  diff_count = 0.
  for i in range(len(diff)):
    line = diff[i]
    if line[0] == '+' or line[0] == '-':
      diff_count += 1.
    elif line[0] == '?':
      # Find the weight of the change based on the # of character changes
      # and the length of the original line
      change_count = line.count('+')    # Count the character changes
      change_count += line.count('-')
      change_count += line.count('^')
      line_size = float(len(diff[i-1])) # Previous line size
      ratio = change_count/line_size

      diff_count -= 1.*(1. - ratio)
      # TODO: sometimes a ? comes with both - and + lines, if there is only
      # one 'direction' of change i.e. a->b only appends or deletes chars
      # but not both operations, thus we might need to count more when
      # this happens
    

  tot_len = len(f1) + len(f2)
  return (tot_len - diff_count)/tot_len

# Grabs a diff between f1 & f2
def differ(f1, f2):
  d = Differ()
  diff = list(d.compare(f1, f2))
  return diff

# Does the magic
def go(folder, tag, files_to_scan, loose=False, itr=False):
  tag=tag+"-"
  
  # Get the current path, try to find the directory we want
  path = os.getcwd()
  filelist = os.listdir(path)

  # TODO: decide on a better structure for this stuff
  if folder not in filelist:
    print 'Could not find the folder:', folder
    exit()

  # Scan through the current set of files and see which ones do not
  # match the tag we want, grabbing the usernames from the file structure
  path = path+"/"+folder+"/"
  filelist = os.listdir(path)
  usernames = []

  # Look only for files that match the tag, and then grab their usernames
  for filename in filelist:
    if string.find(filename, tag) == 0:
      name_split = string.split(filename, '-')
      if len(name_split) != 2:
        print filename, "doesn't have exactly one dash!"
      else:
        usernames.append(name_split[1])

  # Now, take each username, and for every file that we want, strip the
  # contents of it and store it somewhere
  user_contents = {}
  for username in usernames:
    curdir = path+tag+username+"/"
    file_contents = {}

    for filename in files_to_scan:
      curpath = curdir+filename
      lines = strip_file(curpath)
      file_contents[filename] = lines

    user_contents[username] = file_contents

  # Compare all the files, printing out the diff results of each comparison
  diff_results = {}
  for filename in files_to_scan:
      print "Comparing", filename
      total_ratio = 0
      num_pairs = 0
      diff_results[filename] = {}
      sorted_results = []

      for i in range(len(usernames)):
        user1 = usernames[i]
        f1 = user_contents[user1][filename]
        if f1 == None:
          continue
          
        if user1 not in diff_results[filename].keys():
          diff_results[filename][user1] = {}

        for j in range(i+1, len(usernames)):
          user2 = usernames[j]
          f2 = user_contents[user2][filename]
          if f2 == None:
            continue
            
          if user2 not in diff_results[filename].keys():
            diff_results[filename][user2] = {}

          num_pairs += 1

          # Store the results in a global map and as a list that is will be
          # sorted and printed
          diff = differ(f1, f2)
          if loose:
            ratio = loose_ratio(diff, f1, f2)
          else:
            ratio = strict_ratio(f1, f2, 20 if itr else 1)
          diff_results[filename][user1][user2] = (ratio, diff)
          
          # Store the inverse as well, for bookkeeping purposes
          diff_inv = differ(f2, f1)
          if loose:
            ratio_inv = loose_ratio(diff_inv, f2, f1);
          else:
            ratio_inv = strict_ratio(f2, f1, 20 if itr else 1)
          diff_results[filename][user2][user1] = (ratio_inv, diff_inv)
          
          # Oddly, this isn't commutative?
          if (ratio > ratio_inv):
            sorted_results.append((ratio, user1, user2))
            total_ratio += ratio
          else:
            sorted_results.append((ratio_inv, user2, user1))
            total_ratio += ratio_inv
      
      diffsquared = 0.
      avg_ratio = total_ratio/num_pairs
      
      # Print the sorted results and find tally statistics
      sorted_results.sort(reverse = True)
      for (ratio, user1, user2) in sorted_results:
        print "\t[", user1, "]", '[', user2, "] :", ratio
        diffsquared += (ratio-avg_ratio)**2

      # Print some statistics
      print "\n\tAverage ratio: ", total_ratio/num_pairs
      print "\t Std dev:", math.sqrt(diffsquared/num_pairs)
      print "\n\n"

  # Set the global and return
  global latest_results
  latest_results = diff_results

  return diff_results
  
# Prints some stuff
def print_u2u(user1, user2, filename=None, results=None):
  global latest_results
  if results == None:
    results = latest_results

  if filename == None:
    files = results.keys()
  elif type(filename) is not ListType:
    files = [filename]
  else:
    files = filename
    
  for filename in files:
    ratio, diff = results[filename][user1][user2]
    diff = ''.join(diff)
    print "[", user1, "]", '[', user2, "] :", filename, ratio
    print diff
    print "\n\n"
    
# Outputs the filenames we scanned to CSV format... jankily
def output_to_csv(filename=None, results=None):
  global latest_results
  if results == None:
    results = latest_results

  if filename == None:
    files = results.keys()
  elif type(filename) is not ListType:
    files = [filename]
  else:
    files = filename
    
  for filename in files:
    values = results[filename]
    output = open(filename.split('/')[-1]+".csv", 'w')
    users = values.keys()
    
    # Print the top header of the csv
    output.write(filename+",")
    for user in users:
      output.write(user+",")
    output.write('\n')
    
    # Print the rest of the csv
    for user1 in users:
      output.write(user1+",")
      for user2 in users:
        if user1 == user2:
          output.write("1,")
        else:
          output.write(str(values[user1][user2][0])+",")
      output.write('\n')
    
    output.close()

##################################
######## MAIN
##################################

if (len(sys.argv) < 4):
  print "python heuristic_scan.py folder tag [file1, file2 ...]"
  print "i.e. python heuristic_scan.py files cs24mid qsort/ip_qsort.s"
else:
  # TODO: make this not as janky
  folder = sys.argv[1]
  tag = sys.argv[2]
  files = sys.argv[3:]

  # loose sets 'fuzzy' matching
  # itr   sets iterative matching TODO: loose iterative
  results = go(folder, tag, files, loose=False, itr=False)

