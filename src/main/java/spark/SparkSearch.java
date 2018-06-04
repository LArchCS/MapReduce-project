package spark;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;

public class SparkSearch {
	private static ScriptEngineManager manager = new ScriptEngineManager();
    private static ScriptEngine engine = manager.getEngineByName("js");
    private static SparkSession spark;
    private static String fileName;
    private static HashMap<String, String> index;
    private static List<String> searchDomain;
    private static String translated;
    
    public SparkSearch(String fileName) {
    	spark = SparkSession.builder().master("local[*]").appName("Spark Search").getOrCreate();
    	this.fileName = fileName; //Most likely, the file name will be that of the inverted index folder.
    	index = new HashMap<String, String>();
    	fillHashMap();
    }
    
    private static void fillHashMap() {
    	File folder = new File(fileName);
		if (folder.isDirectory()) {
			for (File file: folder.listFiles()) {
				index.put(file.getName(), file.getAbsolutePath());
			}
		}
    }
    
    public static List<String> makeQuery(String query) throws ScriptException {
    	List<String> results = evalLoop(query).collect();
    	return results;
    }
    
    private static JavaRDD<String> evalLoop(String query) throws ScriptException {
    	searchDomain = new ArrayList<String>();
    	translated = "";
		String[] terms = query.split(" and ");
		
		for (int i=0; i < terms.length - 1; i++) {
			if (terms[i].contains("(")) {
	    		translated += construct(terms[i].substring(1, terms[i].length()-1)) + "&&";
	    	}
	    	else {
	    		translated += construct(terms[i]) + "&&";
	    	}
		}
		if (terms[terms.length-1].contains("(")) {
			translated += construct(terms[terms.length-1].substring(1, terms[terms.length-1].length()-1));
		}
		else {
			translated += construct(terms[terms.length-1]);
		}
//		System.out.println(index.get(searchDomain.get(0)));
//		System.out.println(searchDomain.size());
//		System.out.println(searchDomain.get(0));
		JavaRDD<String> results = spark.read().textFile(index.get(searchDomain.get(0))).toJavaRDD();
		for (int j = 1; j < searchDomain.size(); j++) {
			results = results.union(spark.read().textFile(index.get(searchDomain.get(j))).toJavaRDD());
		}
		results = results.filter(s -> evaluate(s, translated));
		return results;
	}
    
    private static String construct(String term) throws ScriptException {
//		engine.put("s", s);
		String[] parts = term.split(" or ");
		String predicate = "";
		for(int i=0; i<parts.length - 1; i++) {
			if (!searchDomain.contains(parts[i].substring(0, 2) + ".txt")) {
				searchDomain.add(parts[i].substring(0, 2) + ".txt");
			}
			String part = "term" + i;
			engine.put(part, parts[i]);
			if (parts[i].substring(0, 3).equals("not(")) {
				predicate += "!s.contains(term" + i + ") || ";
			}
			else {
				predicate += "s.contains(term" + i + ") || ";
			}
		}
		//The case where length = n
		if (!searchDomain.contains(parts[parts.length-1].substring(0, 2) + ".txt")) {
			searchDomain.add(parts[parts.length-1].substring(0, 2) + ".txt");
		}
		String part = "term" + (parts.length-1);
		engine.put(part, parts[parts.length-1]);
		if (parts[parts.length-1].substring(0, 3).equals("not(")) {
			predicate += "!s.contains(term" + (parts.length-1) + ")";
		}
		else {
			predicate += "s.contains(term" + (parts.length-1) + ")";
		}
		return predicate;
//		Object result = engine.eval(predicate);
//		return Boolean.TRUE.equals(result);
	}
    
	public static boolean evaluate(String line, String logic) throws ScriptException {
		engine.put("s", line);
		return Boolean.TRUE.equals(engine.eval(logic));
	}

}
