stop-the-copy
=============

-------------------------------------------------------------------------------
Heuristic Scanner AKA Ye's Little Tool

Test it out with this command:
  python heuristic_scan.py --folder examples --tag cs24hw4 exceptions/my_setjmp.s

Which outputs:
  -----------------------------------------------------
  | To jump between files, search for the marker: +++ |
  | To jump to statistics, search for the marker: === |
  | Alternately, you can search for the filename      |
  -----------------------------------------------------

  Checking files:
  	1) exceptions/my_setjmp.s  


  strict results
  +++ exceptions/my_setjmp.s:
  	[User1    - User2   ]  [Norm    |  Actual]
  	[1        - 0       ]  [1.0000  |  0.9118]
  	[SOLN     - 1       ]  [0.0803  |  0.2571]
  	[SOLN     - 0       ]  [0.0000  |  0.2000]

  strict stats
  === exceptions/my_setjmp.s:
  	     [Norm   | Actual]
  	Avg: [0.3601 | 0.4563]	Max: 0.9118
  	Dev: [0.4537 | 0.3229]	Min: 0.2000

---
File organization:
  The '--folder' and '--tag' options specify the organization that the scanner
  expects the data to be in. Namely, the options '--folder foo --tag bar' tells
  the scanner to look in the directory 'foo' for all subdirectories that begin
  with 'bar-'. Note that the '-' is expected by the scanner as part of the tag
  but is not specified in the command line option. Generally, after the tag, some
  unique identifier (generally a username) should follow. This will be how the
  scanner keeps track of users.
  
  If a tag is not given, we assume the tag is the same as the folder.
  
  For example, the samples given here are as follows:
    examples/cs24hw4-0
    examples/cs24hw4-1
    examples/cs24hw4-SOLN
    
  Thus the folder will be 'examples' and the tag will be 'cs24hw4'. If there is
  a solution set, put the files in a folder and set the ID to SOLN. If there is
  a template/reference set of code that is first given to the user (more on this
  later), set the ID to REFERENCE. 
  
  To tell the scanner what files to look for, simply type in the filenames after
  specifying all the command line arguments. Looking at our example query, we 
  see that we are telling the scanner to look at the file 'exceptions/my_setjmp.s'
  under each of the individual subdirectories. If a particular file does not
  exist for a user, the scanner will not display a result for that user.
  
---
Options:

1) Regular (strict) matching:
  This is a simple calculation that finds the ratio of EXACT matches between
  two sets of code. The calculation done is:
  
        2*(num_common_lines)/(file1_size + file2_size)
        
  This calculation is done by the SequenceMatcher.ratio function in difflib. No
  other arguments are necessary to specify this.
  
2) fuzzy/loose matching: specify with '--fuzzy' argument
  Fuzzy/loose matching takes into account the lines that are close matches.
  Namely, for a line that is a close match, we add into the total num matches:
  
      (line_length - num_diff_chars)/line_length
      
  This calculation is done by manually scanning the diff produced by difflib. As
  such, it is slower than the regular matching, but is generally much better at
  finding matches for C code. Due to the conciseness of ASM code, fuzzy matching
  tends to overestimate the closeness since the op codes and register names are
  very very close. Regardless, fuzzy matching is a very good tool.
  
3) Iterative matching: specify with '--itr' argument
  Iterative matching does many levels of diffs. In particular, after matching two
  files together, iterative matching excises the common/matched blocks from the
  two files and then diffs the new files together. This way, if a user simply
  cut and pasted code, the matching will be much higher. The overall ratio is
  calculated as the sum of:
  
      (sum_i: ratio_i*numlines_i)/total_numlines
  
  We iterate for a maximum of 20 iterations, or when there are no more matches.    
  Iterative matching is compatible with both regular and fuzzy matching. However,
  it is much faster with regular matching than with fuzzy due to the manual
  edits needed for the latter. In general, iterative matching produces higher
  overall similarity ratios, but not by much since most of the code is written
  in a particular order. Thus it is generally worthwhile to run iterative-strict
  matching, but not as worthwhile for iterative-fuzzy.
  
4) Reference/template excision:
  If there is reference/template code that is initially given to the student
  to work off of, simply include that in a folder ID'd with [tag]-REFERENCE. The
  scanner will automatically detect that a reference is given and then excise
  all matching blocks from each user file that has a reference given. The
  excision algorithm is the same as the one used for iterative-strict matching.
  
  To not use reference excision, either remove the folder or specify the 
  '--noref' option.

---
What the scanner does:

1) The first step is to parse the input, find the list of files stated, and then
   look in the directory structure for the set of usernames to scan. This is
   done in the first part of the 'go' function.
   
2) Process the userfiles, saving the files to disk and to memory. The file
   stripping is done in the 'strip_file' function. What it does is run a bash
   command that removes all C-style comments, {} braces, and whitespace from
   a file. The comments and brace removal is done in the sed script
  'remccoms3.sed' and whitespace removal is done in the python script 
  'remspace.py'. The stripped file is saved in the folder as [filename].stripped
  
  Note: as of now, the file processing is somewhat C and ASM specific. We remove
        only C and ASM style comments, namely anything //, /* */, #. The #
        removal WILL get rid of preprocessor definitions, but those are a
        minimal number of lines compared to the overall code.

3) Run a diff, either 'strict_ratio' or 'loose_ratio' depending on which options
   are set. Iterative matching is set with an argument into these functions.
   
4) Output stuff, done at the end of 'go'.

5) If you are running this inside a python shell, in general, you should save
   the results of go:
   
      results = go(folder, tag, files, fuzzy?, itr?, ref?)

   The results are organized as a map of:
   
      [folder][user1][user2] -> (ratio, diff)
    
   The latest results are stored in the global variable 'latest_results'. With
   the saved results, you can run functions such as:
   
      a) print_u2u(user1, user2) which prints the diff, ratio between two users
      b) output_to_csv which spits out the results into CSV format
      
-------------------------------------------------------------------------------
Demo word_extractor.py using the following commands:

    python word_extractor.py examples\cs24hw4-0\exceptions\my_setjmp.s examples\cs24hw4-1\exceptions\my_setjmp.s

    python word_extractor.py examples\cs24hw4-0\exceptions\my_setjmp.s.stripped examples\cs24hw4-1\exceptions\my_setjmp.s.stripped