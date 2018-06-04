import java.io.IOException; 
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.io.Text; 
import org.apache.hadoop.mapreduce.Reducer; 


public class InvertReducer extends Reducer<Text, Text, Text, Text> {
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context ctx) throws IOException, InterruptedException {
		HashMap<Integer, Integer> indices = new HashMap<>();
		ArrayList<PositionWrapper> wrappers = new ArrayList<PositionWrapper>();
		HashMap<String, ArrayList<String>> results = new HashMap<>();
		for (Text t : values) {
			String v = t.toString();
			PositionWrapper wrapper = new PositionWrapper(v);
			wrappers.add(wrapper);
			indices.put(wrapper.fileId, indices.getOrDefault(wrapper.fileId, 0) + 1);
		}
		String word = key.toString();
		wrappers.sort((PositionWrapper a, PositionWrapper b) -> indices.get(b.fileId) - indices.get(a.fileId));
		//ArrayList<String> res = new ArrayList<String>();
		wrappers.forEach(wrapper -> {
			if (!results.containsKey(""+ wrapper.fileId)) { //We don't have the fileID listed
				results.put("" + wrapper.fileId, new ArrayList<String>());
				results.get("" + wrapper.fileId).add(""+wrapper.fileIdx);
			}
			
			else { //We do have the fileID
				results.get("" + wrapper.fileId).add(""+wrapper.fileIdx);
			}
		});
		//System.out.println(res);
		ctx.write(new Text(String.format("%s -> %s", word, String.join(",", results.toString()))), null);
	}
}