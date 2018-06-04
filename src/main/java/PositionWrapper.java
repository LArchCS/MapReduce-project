
public class PositionWrapper {
	public int fileId;
	public int fileIdx;
	
	public PositionWrapper(int fileId, int fileIdx) {
		this.fileId = fileId;
		this.fileIdx = fileIdx;
	}
	
	public PositionWrapper(String wrapper) {
		int[] content = PositionWrapper.deserialize(wrapper);
		this.fileId = content[0];
		this.fileIdx = content[1];
	}
	
	public String toString() {
		return String.format("(%d, %d)", this.fileId, this.fileIdx);
	}
	
	public static String serialize(int fileId, int fileIdx) {
		String wrapper = String.format("(%d, %d)", fileId, fileIdx);
		return wrapper;
	}
	
	public static String serialize(String fileId, int fileIdx) {
		String wrapper = String.format("(%s, %d)", fileId.trim(), fileIdx);
		return wrapper;
	}
	
	public static int[] deserialize(String wrapper) {
		wrapper = wrapper.substring(1, wrapper.length() - 1);  // remove "(" and ")"
		int[] res = new int[2];
		String[] content = wrapper.split(",");
		res[0] = Integer.parseInt(content[0].trim());
		res[1] = Integer.parseInt(content[1].trim());
		return res;
	}
}