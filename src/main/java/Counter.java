import java.util.HashSet;


public class Counter {
	// static variable single_instance of type Singleton
	private static Counter instance = null;
	private HashSet<String> indices;
 
	// private constructor restricted to this class itself
	private Counter() {
		this.indices = new HashSet<String>();
	}

	// add a file index
	// called by mapper
	public static void addIndex(String id) {
		Counter.getInstance().indices.add(id);
	}

	// return the size of indices
	// called by reducer
	public static int getCount() {
		return Counter.getInstance().indices.size();
	}

	// although public function, do not call unless really necessary
	public static Counter getInstance() {
		if (instance == null) {
			instance = new Counter();
		}
		return instance;
	}
}