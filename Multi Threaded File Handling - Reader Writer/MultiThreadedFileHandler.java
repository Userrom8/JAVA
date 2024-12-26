//Please clear the "context_storage.txt" and "demo_file.txt" files prior to run!

import java.io.*;
import java.util.ArrayList;

record Objects(Thread thread, Writer writer) {
}

class FileMod {
	private BufferedReader reader;
	private BufferedWriter writer;

	FileMod(String filePath) {
		try {
			reader = new BufferedReader(new FileReader(filePath));
			writer = new BufferedWriter(new FileWriter(filePath, true));
		} catch (IOException e) {
			System.out.println("Error opening file: " + e.getMessage());
		}
	}

	synchronized String readLine() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			System.out.println("Error reading file: " + e.getMessage());
		}
		return null;
	}

	synchronized void writeLine(String content) {
		try {
			writer.write(content + "\n");
			writer.flush();
		} catch (IOException e) {
			System.out.println("Error writing to file: " + e.getMessage());
		}
	}

	synchronized void close() {
		try {
			if (reader != null)
				reader.close();
			if (writer != null)
				writer.close();
		} catch (IOException e) {
			System.out.println("Error closing file: " + e.getMessage());
		}
	}
}

class Sync {
	private final FileMod file;
	private boolean writing = false;
	private int readCount = 0;

	Sync(FileMod file) {
		this.file = file;
	}

	synchronized void startRead(int readerId) {
		try {
			while (writing)
				wait();
			readCount++;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	synchronized void finishRead() {
		readCount--;
		if (readCount == 0)
			notifyAll();
	}

	synchronized void startWrite() {
		try {
			while (writing || readCount > 0)
				wait();
			writing = true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	synchronized void finishWrite() {
		writing = false;
		notifyAll();
	}

	String readLine(int readerId, int lineNo) {
		startRead(readerId);
		String line = file.readLine();
		System.out.println("(Reader " + readerId + " read line " + lineNo + ") content: " + (line != null ? "\""+line+"\"" : "EOF"));
		finishRead();
		return line;
	}

	void writeLine(String content, int lineNo) {
		startWrite();
		file.writeLine(content);
		System.out.println("Writer wrote line " + lineNo + ": " + content);
		finishWrite();
	}
}

class Reader implements Runnable {
	private final Sync sync;
	private final int readerId;

	Reader(Sync sync, int readerId) {
		this.sync = sync;
		this.readerId = readerId;
	}

	@Override
	public void run() {
		int lineNo = 0;
		while (true) {
			lineNo++;
			String line = sync.readLine(readerId, lineNo);
			if (line == null)
				break;
		}
	}
}

class Writer implements Runnable {
	private final Sync sync;
	int lineNo;

	Writer(Sync sync, int currentLine) {
		this.sync = sync;
		lineNo = currentLine;
	}

	@Override
	public void run() {
		while (true) {
			lineNo++;
			sync.writeLine("Written Line: " + lineNo, lineNo);
			try {
				Thread.sleep(1000); // Simulate some delay
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}
}

public class MultiThreadedFileHandler {
	// an array containing references to each of the files allocated for new readers
	// and writers
	static ArrayList<FileMod> files = new ArrayList<FileMod>();

	static Objects ThreadCreator(String URL, int line, boolean writer) {
		FileMod file = new FileMod(URL);
		files.add(file);

		Writer w = new Writer(new Sync(file), line);

		return new Objects((new Thread(w)), w);
	}

	static Thread ThreadCreator(String URL, int readerID) {
		FileMod file = new FileMod(URL);
		files.add(file);

		return (new Thread(new Reader(new Sync(new FileMod(URL)), readerID)));
	}

	public static void main(String[] args) {
		String fileURL = "demo_file.txt";	//stores the data, read and written by threads
		String ContextFileURL = "context_storage.txt";	//auxilliary file, stores the current state of the main file 

		File mainFile = new File(ContextFileURL);
		BufferedReader mainAccessReader = null;
		BufferedWriter finalWriter = null;
		int currentLine = 0;

		try {
			mainAccessReader = new BufferedReader(new FileReader(mainFile));
			String lineVal = mainAccessReader.readLine();
			currentLine = (lineVal != null) ? Integer.parseInt(lineVal) : 0;
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		Objects writerThreadRef = ThreadCreator(fileURL, currentLine, true);

		Thread writerThread = writerThreadRef.thread();
		Writer writerRef = writerThreadRef.writer();

		Thread readerThread1 = ThreadCreator(fileURL, 1);
		Thread readerThread2 = ThreadCreator(fileURL, 2);

		writerThread.start();
		readerThread1.start();
		readerThread2.start();

		try {
			readerThread1.join();
			readerThread2.join();
			writerThread.interrupt();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			currentLine = writerRef.lineNo;
			for (FileMod file : files)
				file.close();
			try {
				if (mainAccessReader != null)
					mainAccessReader.close();
			} catch (IOException e) {
				System.err.println(e.getLocalizedMessage());
			}
			try {
				finalWriter = new BufferedWriter(new FileWriter(mainFile, false));
				finalWriter.write(Integer.toString(currentLine));
			} catch (IOException e) {
				System.err.println(e.getMessage());
			} finally {
				try {
					if (finalWriter != null)
						finalWriter.close();
				} catch (IOException e) {
					System.err.println(e.getLocalizedMessage());
				}
			}
		}
	}
}