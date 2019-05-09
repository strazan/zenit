package main.java.zenit.javacodecompiler;

public interface Buffer<C> {
	
	public void put(C element);
	
	public C get();
	
	public boolean isEmpty();

}
