package app.sand.throughputtest;

public class MemoryRWJni {

	public MemoryRWJni() {
		
	}
	public native String MemoryReadRate(long bytecount);
	public native String MemoryWriteRate(long bytecount);
	static {
		System.loadLibrary("MemoryRWJni");
	}
}