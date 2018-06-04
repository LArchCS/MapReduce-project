#MapReduce-Project: Using a Spark Query

*Last modified: April 14, 2018*

Group: cs132g7

Members: Nadav Raichman, Fan Di, Cameron Cho

### Summary

This is our implementation of a query searching system sorting through our inverted index. It uses Spark in order to scan through the files containing the inverted indices (created from our MapReduce program) and fetch the documentID's connected to the words that a user wanted to find.

The program in question, "SparkSearch.java", is run in the 'spark' package in our MapReduce project folder.

##Breakdown

###Requirements
Running this project requires a Maven build backing it, and the Maven project requires the following dependencies:

- hadoop-mapreduce-client-app (From org.apache.hadoop, version 3.0.0)


- hadoop-client (From org.apache.hadoop, version 3.1.0)


- spark-sql_2.11 (From org.apache.spark, version 2.3.0)


- spark-core_2.11 (From org.apache.spark, version 2.3.0)


- jackson-module-scala_2.11 (From com.fasterxml.jackson.module, version 2.7.8)



- spark-yarn_2.11 (From org.apache.spark, version 2.3.0)

In addition, you will need to have the following path variables set:

- HADOOP_HOME (Path to your hadoop-3.0.0 folder)


- PATH (Path to your hadoop-3.0.0/bin folder)


- SPARK_HOME (Path to your spark-2.3.0-bin-hadoop2.7 folder)


###Process Flow
We have set up our SparkSearch such that it is contained in its own object. This is how one would use our object:

1. You create a new SparkSearch object as follows:
```SparkSearch searchBar = new SparkSearch(<filename>)``
2. Call ```searchBar.makeQuery(<query string>)`` to get the system to run Spark. It will return a list of all the document ID's that contained your search terms.

*Note: you will need to have your method handle ScriptExceptions to use this object.*


###Program Construction
This program makes use of the ScriptEngine object in Java, which allows a user to employ the engine of different languages. In this instance, we are using the JavaScript engine in order to use its ability to take Strings that represent Boolean expressions and evaluate the expressions the Strings represent.

**public static List<String> makeQuery(String query)**
- The main method you will call whenever you create a SparkSearch object. it will create a Dataset<String> from the file(s) you passed into the object on which the Spark engine will perform the query search on.


*-Note: the query string is in conjunctive normal form.*


- After generating the Dataset, it and the query string are passed into the evalLoop(set, query) method.


**private static Dataset<String> evalLoop(Dataset<String> set, String query)**
- This method starts by splitting the query string into an array based on the presence of "and" keywords in it.


- For every term in the query created by the split, the method filters the dataset by calling the evaluate(line, term) method, testing to see if the dataset being queried has the search term in it on a per-line basis, trimming down the dataset with each pass.


- *There will be more discussion of this topic in the "Discussion" part of this README.*


- After every term has filtered out all lines in the dataset that do not contain the search terms, the resulting dataset is returned to the makeQuery() method.


**private static boolean evaluate(String s, String term)**
- This is where the real work of the object is performed. Before we start, it should be helpful to discuss what "terms" actually are. Put simply, a term is a search term in the query, i.e.: (cats or dogs); frogs. Both the words in the parentheses and the word by itself count as search terms. As stated before, the query was passed to us in conjunctive normal form.


- We first add the line to be queried against to the ScriptEngine's scope so that it can recognize the variable when evaluating it. Then, we split the term up into its components, if any, based on "or" keywords.


- For every component of the search term (arranged in such a way to avoid the goalpost problem), we add the term to the ScriptEngine's scope, then build up a mini-query from the components in proper Boolean format (i.e.: ```s.contains(dog) || s.contains(cat)``). It also checks if the user did NOT want to see a certain term.


- Once the mini-query has been constructed, the engine evaluates the query and returns the result of the evaluation to the evalLoop() method.


##Discussion
- You will see that there are two ways we implemented this program:



 1. We filter the dataset multiple times, once for each term in the search query. (located in "SparkImplementation2.txt")
 
 2. We filter the dataset once with the entire query. (located in "SparkImplementation1.txt)
 
 
- After testing each implementation, we found that the first approach took 15 minutes to complete a query, while the second approach took 17 minutes. I suspect that this discrepancy between methods is caused by the size of the dataset on which the query is performed, but given that each test used a one-word query, more testing must be done to prove this for certain. As such, we are using the first implementation of this program for now.


- One might question why it takes so long for this program to run, and then point to how the Spark application is set up: ```spark = SparkSession.builder().master("local").appName("Spark Search").getOrCreate();``


- The person might then argue that we should be using ```local[*]`` instead to optimize the usage of the machine in question. However, upon experimenting with this change, it caused my particular machine to throw an OutOfMemoryError, for any number of machines greater than one. I am uncertain if this is machine-specific, but until I can determine exactly why this happens, this program is stuck using only one local node.


- Our original intent was to use an SQLite database to store our inverted index instead of having to utilize Spark. However, the way the data table was constructed for the task made reading and writing into it incredibly slow for inserting an inverted index of roughly 5 GB in size. To give an idea of how long this task would take as-is, after letting the computer run the insertion algorithm for 15 hours, I found that only 440k entries had been created in the database, and the program had not managed to read past the words starting with the letter "a." We will revisit this strategy soon, mainly by using a different way of structuring the data table in which we store the entries.



