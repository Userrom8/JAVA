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
		Reader.file = file;
		readerID = ++ReaderWriterCount.readerCount;
		EOF = false;
	}

	void read() {
		while (true) {
			atomic.lock.readLock().lock();
			EOF = false;
			try (BufferedReader rd = new BufferedReader(new FileReader(file))) {
				while (!EOF) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					String content = rd.readLine();
					EOF = content == null ? true : false;
					System.out.println("reader " + readerID + " read: \"" + (EOF ? "\bEOF" : content + "\""));
				}
			} catch (IOException e) {
			} finally {
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
		Writer.file = file;
		writerID = ++ReaderWriterCount.writerCount;
	}

	void write() {
		while (true) {
			atomic.lock.writeLock().lock();
			try (BufferedWriter wt = new BufferedWriter(new FileWriter(file, true))) {
				String content = "hi_from_wt-" + writerID;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				wt.newLine();
				wt.write(content);
				System.out.println("writer " + writerID + " wrote: \"" + content + "\"");
			} catch (IOException e) {
			} finally {
				atomic.lock.writeLock().unlock();
				try {
					Thread.sleep(20000);
				} catch (Exception e) {
				}
			}
		}
	}

	public void run() {
		write();
	}
}

public class reader_writer {
	static int readerID, writerID;

	static void resetFile(File file) {
		try (BufferedWriter cleaner = new BufferedWriter(new FileWriter(file))) {
			cleaner.write("start");
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) {
		File file = new File("file.txt");

		// FILE PREP - make fresh:
		resetFile(file);

		for (int i = 0; i < 5; i++) {
			Thread reader_thread = new Thread(new Reader(file));
			reader_thread.setDaemon(true); // Immediate termination after main thread(parent) terminates.
			reader_thread.start();

			Thread writer_thread = new Thread(new Writer(file));
			writer_thread.setDaemon(true); // Immediate termination after main thread(parent) terminates.
			writer_thread.start();
		}

		try {
			Thread.sleep(50000);
		} catch (InterruptedException e) {
		} finally {
			System.exit(0);
		}
	}
}