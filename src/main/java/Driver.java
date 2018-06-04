import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class Driver {
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		//It needs to be like this because on the cluster, this is how it runs:
		//yarn jar Driver /data/wiki_csv /user/cs132g7/output
		//Needs to be 0 and 1 for local, and 1 and 2 for the cluster...
		File folder = new File(args[0]);
		Path out = new Path(args[1]);
//		File folder = new File(args[1]);
//		Path out = new Path(args[2]);
		Path inp;
		System.out.println("Does the folder exist? " +folder.exists());
		System.out.println("What is the identity of this folder? " +folder.getName());
		System.out.println("Is this folder a directory? " +folder.isDirectory());
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "inverted index");
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		TextOutputFormat.setCompressOutput(job,true);
		TextOutputFormat.setOutputCompressorClass(job,GzipCodec.class);
		TextOutputFormat.setOutputPath(job, out);
		
		//Need this for local...
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isFile() && !fileEntry.isHidden()) {
				inp = new Path(fileEntry.getPath());
		        TextInputFormat.addInputPath(job, inp);
			}
	    }
		
		//Need this for the cluster...
//		for (int i=0; i < 136; i++) {
//			inp = new Path("/data/wiki_csv/wiki" + i + ".csv");
//	        TextInputFormat.addInputPath(job, inp);
//		}

		job.setJarByClass(Driver.class);
		job.setMapperClass(StringIndexMapper.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setReducerClass(InvertReducer.class);

		job.waitForCompletion(true);
	}
}