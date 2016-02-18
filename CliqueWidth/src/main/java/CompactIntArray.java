
public class CompactIntArray {
	
	byte[] data; 
	
	public CompactIntArray(int maxValue, int size){
		if(maxValue>127){
			throw new Error("Not yet implemented");
		}
		
		data = new byte[size];
		
	}

}
