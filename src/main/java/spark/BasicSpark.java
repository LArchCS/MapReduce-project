package spark;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.util.List;
import java.util.ArrayList;

public class BasicSpark {
	public static ScriptEngineManager manager = new ScriptEngineManager();
    public static ScriptEngine engine = manager.getEngineByName("js");
    public static String translated = "";
	
	public static void main(String[] args) throws ScriptException {
		SparkSearch search = new SparkSearch("C:\\Users\\Cameron\\Desktop\\CS132a\\index");
		List<String> results = search.makeQuery("(cats and dogs) or frogs");
		for (String r:results)
			System.out.println(r);
	}
}