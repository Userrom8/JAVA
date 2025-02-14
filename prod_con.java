//this code uses randomizer for a more realistic producer consumer behaviour
//for bounded time execution, adjust the sleep() parameter(scale - ms) in main. [Default - 30s]
//[OUTDATED]for unbounded time execution, either remove the exit(0) or, un-comment the joins after sleep


import java.util.Random;

class OperationDelay {
	static int[] times = {100, 500, 1000, 1500, 2000, 2500, 3000};
	static Random random = new Random();

	static int time() {
		return times[random.nextInt(times.length)];
	}
}

class buffer {
	static final int size = 10;
}

class Item {
	int count;

	Item() {
		count = 0;
	}
}

class producer implements Runnable {
	Item item;

	producer(Item item) {
		this.item = item;
	}

	void produce() {
		if (buffer.size == item.count) {
			System.out.println("producer tried to produce but, inventory is full !");
			return;
		}
		synchronized (item) {
			item.count++;
			System.out.println("<PRODUCTION>PRODUCER PRODUCED AN ITEM, current inventory size: " + item.count);
		}
	}
	public void run() {
		while (true) {
			produce();
			try {
				Thread.sleep(OperationDelay.time());
			} catch (InterruptedException e) {}
		}
	}
}

class consumer implements Runnable {
	Item item;

	consumer(Item item) {
		this.item = item;
	}

	void consume() {
		if (item.count < 1) {
			System.out.println("consumer tried to consume but, inventory is empty !");
			return;
		}
		synchronized (item) {
			item.count--;
			System.out.println("<CONSUMPTION>CONSUMER CONSUMED AN ITEM, current inventory size: " + item.count);
		}
	}
	public void run() {
		while (true) {
			consume();
			try {
				Thread.sleep(OperationDelay.time());
			} catch (InterruptedException e) {}
		}
	}
}

public class prod_con {
	public static void main(String[] args) {
		Item item = new Item();
		Thread prod_thread = new Thread(new producer(item));
		Thread con_thread = new Thread(new consumer(item));

		prod_thread.start();
		con_thread.start();

		try {
			Thread.sleep(30000);
			prod_thread.interrupt();
			con_thread.interrupt();
		} catch (InterruptedException e) {}

		
		//The threads are still alive, just inactive.
		//this can be solidified by the following...
		// System.out.println(prod_thread.isAlive()+" "+con_thread.isAlive());



		System.exit(0);
	}
}