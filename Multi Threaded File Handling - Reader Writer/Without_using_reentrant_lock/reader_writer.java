import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.io.*;

class atomic {
	static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
}

class ReaderWriterCount {
	static int readerCount, writerCount;
}

class Reader implements Runnable {
	static File file;
	int readerID;
	boolean EOF;

	Reader(File file) {
		this.file = file;
		readerID = ++ReaderWriterCount.readerCount;
		EOF = false;
	}

	void read() {
		while (true) {
			atomic.lock.readLock().lock();
			try (BufferedReader rd = new BufferedReader(new FileReader(file))) {
				while (!EOF) {
					String content = rd.readLine();
					EOF = content == null ? true : false;
					System.out.println("reader " + readerID + " read: \"" + (EOF ? "\bEOF" : content + "\""));
				}
			} catch (IOException e) {}
			finally {
				atomic.lock.readLock().unlock();
			}
		}
	}


	public void run() {
		read();
	}
}

class Writer implements Runnable {
	static File file;
	int writerID;

	Writer(File file) {
		this.file = file;
		writerID = ++ReaderWriterCount.writerCount;
	}

	void write() {
		atomic.lock.writeLock().lock();
		try(BufferedWriter wt = new BufferedWriter(new FileWriter(file, true))) {
			String content = "hi_from_wt-" + writerID;
			wt.newLine();
			wt.write(content);
			System.out.println("writer " + writerID + " wrote: \"" + content + "\"");
		} catch (IOException e) {}
		finally {
			atomic.lock.writeLock().unlock();
		}
	}

	public void run() {
		write();
	}
}

public class reader_writer {
	static int readerID, writerID;

	public static void main(String[] args) {
		File file = new File("file.txt");

		for (int i = 0; i < 5; i++) {
			Thread reader_thread = new Thread(new Reader(file));
			reader_thread.start();

			Thread writer_thread = new Thread(new Writer(file));
			writer_thread.start();
		}

		try {
			Thread.sleep(10000);
			//reader_thread.interrupt();
			//writer_thread.interrupt();
		} catch (InterruptedException e) {}
	}
}