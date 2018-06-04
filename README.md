# MapReduce-project

Group: cs132g7

### Summary

This is our implementation of our basic inverted index. It implements a single MapReduce job, which in the end gives out an ordered list of words and the documents in which they appear in.

As of the March 27 update, it now provides the positions of the tokens within each document the tokens appear in.

It should be run on the hadoop cluster with the following command:

`yarn jar cs132g7_inverted-index.jar Driver /data/wiki_csv /user/cs132g7/output`

## Breakdown

*Last modified: March 27, 2018*

The code is divided into 6 files:

**Driver.java**
 
- This is the file of execution, in which it takes in an input directory and specifies an output directory. However, as of this implementation, it performs a for loop to add all of the wiki-csv files on HDFS. At the moment, this is hard-coded; for whatever reason, /data/wiki_csv is not recognized as a proper path when running the yarn jar command.


- As of the March 27 update, the output files are now in the form of Gzip files in order to save on space.
  
**StringIndexMapper.java**

 - This is our mapper program. It takes in the document contents of a Wikipedia page, then tokenizes the contents based on guidelines provided by StringUtils.java. It will be explained more when StringUtils.java is covered, but it gets rid of the most commonly seen words in the English language (it's useless trying to use those as an index) and gets rid of scrub words (i.e: Buffalo!, Buffalo?, Buffalo..., etc.).
 
 
 - As of the update from March 27, the method now takes the fileID of the document and the position of the word in the document and packages them in a PostionWrapper object. This is a custom object that takes in two values, a fileID and the position number of the token in the file with the given fileID. This PositionWrapper object is then serialized, or turned into a string containing both the fileID and position number of the word.
 
 
 - The method outputs a key-value pair of <word, wrapper> to the shufflers and reducers, where "wrapper" is the serialized PositionWrapper object containing the document's fileID and the position of the word in the document.

**InvertReducer.java**

 - As of the update from March 27, this program takes in a word and an iterator of wrappers (which contain the document's file ID and the position of the token in the document), then uses those wrappers in order to: 1) determine which documents the word appeared in, and 2) count the number of times the word appeared in that specific document, and 3) determine where in the document the word appeared. This is possible thanks to the fact that there are multiple copies of the same document ID in the iterator that was passed to the reducer.
 
 
 - A reducer creates a HashMap of indices and an ArrayList of Wrappers before iterating through the list of wrapper strings it received. Each wrapper is added to the ArrayList, and the HashMap of indices is updated for every wrapper.
 
 
 - After the ArrayList of Wrappers has been created, it gets sorted by the HashMap of indices based on token frequency and document ID.
 
 
 - The method finishes by writing a key-value pair of a word and a ranked list of distinct documents in which it appeared in, plus the positions it appeared in within each document, for each wrapper in the ArrayList.
 
**Counter.java**

 - This file houses the Singleton that keeps track of the number of times the word appears in all the documents that have it. It does this by creating a HashSet and tracking the number of times the word has appeared in the document.
 
 
 - At the moment, the implementation of the Hadoop cluster makes the use of Singletons ineffective, as one Singleton per reducer will be created. We will need to revisit this at a later time.
 
**StringUtils.java**
 
  - This program utilizes the Lucene library from Apache to help reformat the words received during the Map phase of the execution. It performs several functions for the mappers that use it:
  
 
  1. Stemming (bringing the word to its root form)
  
  2. Scrub word cleaning (removal of numbers, punctuation and setting the word to lower case)
  
  3. Stop word removal (words that appear too often to be useful)
  
  
  - As discussed in the StringIndexMapper.java section, this is called every time that a mapper tokenizes the contents of a document.
  
 **PositionWrapper.java**
 
  - Added as of the update on March 27, this program exists to make packing more data into the key-value pair a mapper sends to a reducer easier. In particular, a PositionWrapper object takes in two fields, a document's file ID and the position of a particular token within that document. It is then "serialized," or turned into a String, then sent with the token itself in a key-value pair to a reducer.