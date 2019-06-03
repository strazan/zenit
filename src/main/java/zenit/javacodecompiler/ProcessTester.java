package main.java.zenit.javacodecompiler;

public class ProcessTester extends Thread {

	Process process;

	public ProcessTester(Process process) {
		this.process = process;
	}

	public void run() {
		try {
			Thread.sleep(3000);
			System.out.println(process.isAlive());
			process.destroy();
			System.out.println(process.isAlive());
			
		} catch (InterruptedException e) {

		}
	}
}
