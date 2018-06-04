import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class WrapperTester {
	public static void main(String[] args) {
		//testOne();
		List<PositionWrapper> wrappers = makeList(20); //Get a list of x PositionWrappers
		HashMap<String, List<String>> results = testTwo(wrappers); //Attempt to organize the list in a new way
		System.out.println(results.toString());
		String[] separated = results.toString().split("],");
		int i = 0;
		while (i < separated.length) {
			String[] keyValue = separated[i].split("=");
			System.out.println("word: " + keyValue[0] + "; value: " + keyValue[1].substring(1));
			i++;
		}
	}
	
	public static void testOne() {
		//PositionWrapper has two fields and a few different methods.
		//This test is to familiarize ourselves with how this object works.
		PositionWrapper wrapper = new PositionWrapper(32, 4557);
		System.out.println(wrapper.toString());
		System.out.println(wrapper.fileId);
		System.out.println(wrapper.fileIdx);
		PositionWrapper copy = new PositionWrapper(wrapper.toString());
		System.out.println(copy.toString()); //Should be the same as 'wrapper'
				
		System.out.println(PositionWrapper.deserialize(wrapper.toString())[0]);
		System.out.println(PositionWrapper.deserialize(wrapper.toString())[1]);
		System.out.println(PositionWrapper.serialize(342, 234436).toString());
	}
	
	public static HashMap<String, List<String>> testTwo(List<PositionWrapper> wrappers) {
//		for (PositionWrapper wrapper : wrappers) {
//			System.out.println(wrapper.toString());
//		}
		HashMap<String, List<String>> results = new HashMap<String, List<String>>();
		
		for (PositionWrapper wrapper : wrappers) {
//			String translate = wrapper.toString();
//			translate = translate.substring(1, translate.length()-1);
//			String[] components = translate.split(","); //[0] = fileID, [1] = fileIndex
			if (!results.containsKey(""+ wrapper.fileId)) { //We don't have the fileID listed
				results.put("" + wrapper.fileId, new ArrayList<String>());
				results.get("" + wrapper.fileId).add(""+wrapper.fileIdx);
			}
			
			else { //We do have the fileID
				results.get("" + wrapper.fileId).add(""+wrapper.fileIdx);
			}
		}
		return results;
	}
	
	//Creates a List of 'limit' PositionWrappers for use in testTwo
	public static List<PositionWrapper> makeList(int limit) {
		Random rng = new Random();
		List<PositionWrapper> wrappers = new ArrayList<PositionWrapper>();
		
		for (int i=0; i<limit; i++) {
			wrappers.add(new PositionWrapper(rng.nextInt(5), rng.nextInt(10001)));
		}
		
		return wrappers;
		
	}
	
}
