import com.yilnz.surfing.selenium.util.PromiseUtil;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class UtilsTest {

	@Test
	public void test1(){
		List<Callable<String>> execs = Lists.newArrayList();
		execs.add(()-> {
			Thread.sleep(5000);
			return "1";
		});
		execs.add(()-> {
			return "2";
		});
		CompletableFuture<String> race = PromiseUtil.race(execs);
		try {
			String s = race.get();
			System.out.println(s);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2(){
		List<Callable<String>> execs = Lists.newArrayList();
		execs.add(()-> {
			Thread.sleep(5000);
			return "1";
		});
		execs.add(()-> {
			if(true) {
				throw new RuntimeException("err1");
			}
			return "2";
		});
		CompletableFuture<String> race = PromiseUtil.race(execs);
		try {
			String s = race.get();
			System.out.println(s);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3(){
		List<Callable<String>> execs = Lists.newArrayList();
		execs.add(()-> {
			Thread.sleep(5000);
			if(true) {
				throw new RuntimeException("err1");
			}
			return "1";
		});
		execs.add(()-> {
			if(true) {
				throw new RuntimeException("err2");
			}
			return "2";
		});
		CompletableFuture<String> race = PromiseUtil.race(execs);
		try {
			String s = race.get();
			System.out.println(s);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}
