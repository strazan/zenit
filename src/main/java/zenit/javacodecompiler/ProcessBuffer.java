package main.java.zenit.javacodecompiler;

public class ProcessBuffer implements Buffer<Process>{
	
	private Process buffer;
	
	public synchronized void put(Process process) {
		if (buffer == null) {
			buffer = process;
			notifyAll();
		}
	}
	
	public synchronized Process get() {
		if (buffer == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (buffer != null) {
			Process process = buffer;
			buffer = null;
			return process;
		} else {
			return null;
		}
	}

	@Override
	public boolean isEmpty() {
		if (buffer == null) {
			return true;
		} else {
			return false;
		}
	}
}