package skylands.task;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public enum Tasks {
	INSTANCE;

	private int currentTick = 0;

	private static class Task<T> {
		public int arrivalTick;
		public Supplier<T> task;
		public CompletableFuture<T> future;
	}

	private final PriorityQueue<Task<?>> queue = new PriorityQueue<>(Comparator.comparingInt(o -> o.arrivalTick));

	public void tickTasks() {
		if (!queue.isEmpty()) {
			while (queue.peek() != null && queue.peek().arrivalTick <= currentTick) {
				Task poll = queue.poll();
				try {
					poll.future.complete(poll.task.get());
				} catch (Throwable throwable) {
					poll.future.completeExceptionally(throwable);
				}
			}
		}
		currentTick++;
	}

	public <T> CompletableFuture<T> supplyNextTick(Supplier<T> supplier) {
		return schedule(1, supplier);
	}

	public <T> CompletableFuture<T> runNextTick(Runnable runnable) {
		return supplyNextTick(() -> {
			runnable.run();
			return null;
		});
	}

	public CompletableFuture<Void> nextTick() {
		return supplyNextTick(() -> null);
	}

	public CompletableFuture<Void> nextNTick(int nTicks) {
		return schedule(currentTick + nTicks, () -> null);
	}

	public <T> CompletableFuture<T> schedule(int delayInTicks, Supplier<T> supplier) {
		Task<T> task = new Task<>();
		task.arrivalTick = currentTick + delayInTicks;
		task.task = supplier;
		task.future = new CompletableFuture<>();
		queue.add(task);
		return task.future;
	}

}
