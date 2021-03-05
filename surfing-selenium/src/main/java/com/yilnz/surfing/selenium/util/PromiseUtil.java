package com.yilnz.surfing.selenium.util;

import com.yilnz.surfing.selenium.exception.RankedException;
import org.assertj.core.util.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PromiseUtil {

	public static  <R> CompletableFuture<R> race(Collection<? extends Callable<R>> jobs) {
		AtomicInteger atomicInteger = new AtomicInteger();
		final CompletableFuture<R> result = new CompletableFuture<>();
		if (jobs == null || jobs.isEmpty()) {
			result.completeExceptionally(new IllegalArgumentException("there must be at least one job"));
			return result;
		}
		final ExecutorService service = Executors.newFixedThreadPool(jobs.size());

		// accumulate all exceptions to rank later (only if all throw)
		final List<RankedException> exceptions = Collections.synchronizedList(Lists.newArrayList());
		final AtomicBoolean done = new AtomicBoolean(false);

		for (Callable<R> job: jobs) {
			service.execute(() -> {
				try {
					// this is where the actual work is done
					R res = job.call();
					// set result if still unset
					if (done.compareAndSet(false, true)) {
						// complete the future, move to service shutdown
						result.complete(res);
					}
					// beware of catching Exception, change to your own checked type
				} catch (Exception ex) {
					if (ex instanceof RankedException) {
						exceptions.add((RankedException) ex);
					} else {
						exceptions.add(new RankedException(ex));
					}
					if (exceptions.size() >= jobs.size()) {
						// the last to throw and only if all have thrown will run:
						Collections.sort(exceptions, (left, right) -> Integer.compare(left.getRank(), right.getRank()));
						// complete the future, move to service shutdown
						result.completeExceptionally(exceptions.get(0));
					}
				}
			});
		}
		// shutdown also on error, do not wait for this stage
		result.whenCompleteAsync((action, t) -> service.shutdownNow());
		return result;
	}
}
