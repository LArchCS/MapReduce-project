import java.io.IOException;
import java.util.Scanner;

import org.apache.hadoop.io.LongWritable; 
import org.apache.hadoop.io.Text; 
import org.apache.hadoop.mapreduce.Mapper;

public class StringIndexMapper extends Mapper<LongWritable, Text, Text, Text> {
	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		@SuppressWarnings("resource")
		Scanner token = new Scanner(value.toString());
		if (token.hasNext()) {
			int fileIdx = 0;
			String[] head = token.next().split(",");
			String fileId = head[0].trim();
			//String fileLink = head[1];
			Text word;
			Text wrapper;
			while (token.hasNext()) {
				wrapper = new Text(PositionWrapper.serialize(fileId, fileIdx));
				String s = StringUtils.cleanWord(token.next().trim());
				if (s.length() > 2) {
					word = new Text(s);
					context.write(word, wrapper);  // word, (fileId, fileIdx)
				}
				fileIdx += 1;
			}
		}
	}
}