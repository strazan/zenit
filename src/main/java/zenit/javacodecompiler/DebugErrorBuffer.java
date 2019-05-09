package main.java.zenit.javacodecompiler;

import java.util.LinkedList;

public class DebugErrorBuffer implements Buffer<DebugError> {
	
	private LinkedList<DebugError> buffer;
	
	public DebugErrorBuffer() {
		buffer = new LinkedList<DebugError>();
	}

	@Override
	public synchronized void put(DebugError error) {
		buffer.add(error);
	}

	@Override
	public synchronized DebugError get() {
		if (!isEmpty()) {
			return buffer.removeFirst();
		} else {
			return null;
		}
	}
	
	@Override
	public synchronized boolean isEmpty() {
		if (buffer.size() == 0) {
			return true;
		} else {
			return false;
		}
	}
}
