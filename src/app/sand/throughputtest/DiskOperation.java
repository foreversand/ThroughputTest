package app.sand.throughputtest;



public class DiskOperation {

	public DiskOperation() {
		
	}
	private String memoryReadThroughput(long buffersize) {
		
		return new MemoryRWJni().MemoryReadRate(buffersize);
	}
	
	private String memoryWriteThroughput(long buffersize) {
		return new MemoryRWJni().MemoryWriteRate(buffersize);
	}
	
	private String fileCopyThroughput(int src, int dest, long buffersize){
		return "no result";
	}
	
	public String operationThroughputTest(int src, int dest, long buffersize) {
		if((src == 3) || (dest == 3)) {
			if(src == 3) {
				return fileCopyThroughput(3, 1, buffersize);
			}
			else {
				return fileCopyThroughput(1, 3, buffersize);
			}
		}
		else if (src == 1) {
			return memoryReadThroughput(buffersize);
		}
		else {
			return memoryWriteThroughput(buffersize);
		}
	}
	
	public String generateResultHeander(int src, int dest, long buffersize) {
		if((src == 3) || (dest == 3)) {
			if(src == 3) {
				return "Copy file from SD Card to Memory with buffersize = " + buffersize + " Bytes:\n";
			}
			else {
				return "Copy file from Memory to SD Card with buffersize = " + buffersize + " Bytes:\n";
			}
		}
		else if (src == 1) {
			return "Read data from Memory with buffersize =" + buffersize + " Bytes:\n";
		}
		else {
			return "Write data to Memory with buffersize =" + buffersize + " Bytes:\n";
		}
	}
	
}
