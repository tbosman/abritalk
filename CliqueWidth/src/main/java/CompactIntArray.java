
public class CompactIntArray {
	
	byte[] data; 
	final int maxValue; 
	
	public CompactIntArray(int maxValue, int size){
		if(maxValue>127){
			throw new Error("Not yet implemented");
		}
		this.maxValue = maxValue;
		
		data = new byte[size];		
	}
	
	
	public int get(int idx){
		return data[idx];
	}
	
	public void set(int idx, int value){
		if( value > maxValue){
			throw new Error("Maximum value "+(maxValue)+" exceeded");
		}
		if(idx > data.length-1){
			throw new Error("Maximum index exceeded");
		}
		data[idx]  =(byte) value; 
		
		
	}
	
	/**
	 * Sets idx  to maximum allowed value for this array
	 * @param idx
	 */
	
	public void setMax(int idx){
		data[idx] = (byte) maxValue;
	}

}
