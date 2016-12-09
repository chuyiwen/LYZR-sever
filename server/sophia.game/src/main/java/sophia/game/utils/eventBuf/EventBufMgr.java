package sophia.game.utils.eventBuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;

/**
 * 多个事件，定期分批执行<br>
 * 
 * @version 1.0
 */
public class EventBufMgr {
	private static final Logger logger = Logger.getLogger(EventBufMgr.class);
	
	/** 定时任务线程池 */
	public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);

	private static EventBufMgr instance = new EventBufMgr();

	public static EventBufMgr getInstance() {
		return instance;
	}

	private EventBufMgr() {
	}

	public static int DefaultBatchSize = 100;
	public static int DefaultTotalUseTime = 10 * 1000;
	public static TimeUnit DefaultTimeUnit = TimeUnit.MILLISECONDS;

	/**
	 * 优化过的处理总时间
	 * 
	 * @param totalSize
	 * @return
	 */
	public Pair<Integer, TimeUnit> getOptimizationTotalUseTime(int totalSize, int batchSize) {
		int time = (int) (totalSize / batchSize * 1000 * 1.5);
		return new Pair<Integer, TimeUnit>(time, TimeUnit.MILLISECONDS);
	}

	/**
	 * 添加执行，每批处理数量和处理总耗时取默认数值，分别取值100和10秒
	 * 
	 * @param collection
	 *            实体对象集合，比如玩家集合
	 * @param executor
	 *            执行器
	 */
	public void addEvent(Collection<? extends IDoer> collection, Executable executor) {
		addEvent(collection, null, executor);
	}

	/**
	 * 添加执行，每批处理数量和处理总耗时取默认数值，分别取值100和10秒
	 * 
	 * @param collection
	 *            实体对象集合，比如玩家集合
	 * @param first
	 *            优先发送的集合，比如聊天时自己、好友或者队友等
	 * @param executor
	 *            执行器
	 */
	public void addEvent(Collection<? extends IDoer> collection, Collection<? extends IDoer> first, Executable executor) {
		addEvent(collection, first, executor, DefaultBatchSize, DefaultTotalUseTime, DefaultTimeUnit);
	}

	/**
	 * 添加执行
	 * 
	 * @param collection
	 *            实体对象集合，比如玩家集合
	 * @param first
	 *            优先发送的集合，比如聊天时自己、好友或者队友等
	 * @param executor
	 *            执行器
	 * @param batchSize
	 *            每批处理数量
	 * @param totalUseTime
	 *            处理总耗时
	 * @param timeUnit
	 *            事件单位
	 */
	public void addEvent(Collection<? extends IDoer> collection, Collection<? extends IDoer> first, Executable executor, int batchSize, int totalUseTime, TimeUnit timeUnit) {
		int kp = collection.size() % batchSize;
		int batch = collection.size() / batchSize;
		batch = kp == 0 ? batch : batch + 1;

		List<IDoer> list = new ArrayList<IDoer>(collection);
		for (int i = 0; i < batch; i++) {
			int fromIndex = i * batchSize;
			int toIndex = fromIndex + batchSize;
			toIndex = toIndex > list.size() ? list.size() : toIndex;

			List<IDoer> doers = list.subList(fromIndex, toIndex);
			if (i == 0 && first != null) {
				for (IDoer doer : first) {
					if (!doers.contains(doer)) {
						doers.add(0, doer);
					}
				}
			}
			int delayTime = totalUseTime / batch * i;
			scheduledExecutorService.schedule(new EventBufRunner(doers, executor), delayTime, timeUnit);
		}
	}

	private static class TestPlayer implements IDoer {

	}

	private static AtomicInteger times = new AtomicInteger();

	public static void main(String[] args) {

		ArrayList<TestPlayer> characters = new ArrayList<TestPlayer>();
		for (int i = 0; i < 111; i++) {
			characters.add(new TestPlayer());
		}

		Pair<Integer, TimeUnit> pair = EventBufMgr.getInstance().getOptimizationTotalUseTime(characters.size(), EventBufMgr.DefaultBatchSize);
		EventBufMgr.getInstance().addEvent(characters, null, new Executable() {

			@Override
			public void execute(IDoer doer) {
				times.addAndGet(1);
				if (logger.isDebugEnabled())
					logger.debug(times.get());
				throw new RuntimeException("ERROR");
			}
		}, EventBufMgr.DefaultBatchSize, pair.getKey(), pair.getValue());

	}
}
