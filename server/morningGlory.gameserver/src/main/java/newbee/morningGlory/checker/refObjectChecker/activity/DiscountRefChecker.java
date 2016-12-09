package newbee.morningGlory.checker.refObjectChecker.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.store.ref.DiscountItemRef;
import newbee.morningGlory.mmorpg.store.ref.DiscountRef;

import org.apache.log4j.Logger;

import sophia.game.ref.GameRefObject;

public class DiscountRefChecker extends BaseRefChecker<DiscountRef> {
	private static Logger logger = Logger.getLogger(DiscountRefChecker.class);

	@Override
	public String getDescription() {
		return "打折";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		DiscountRef ref = (DiscountRef) gameRefObject;

		Map<Short, Map<String, DiscountItemRef>> discountItemRefMap = ref.getDiscountItemRefMap();

		//Map<String, DiscountConfigRef> discountConfigRefMap = ref.getDiscountConfigRefMap();

		for (Entry<Short, Map<String, DiscountItemRef>> entry : discountItemRefMap.entrySet()) {
			if (entry.getKey() < 0) {
				if (logger.isDebugEnabled()) {
					logger.debug(entry.getKey());
				}
				error(ref, "批次非法");
			}

			Map<String, DiscountItemRef> itemRefMap = entry.getValue();
			for (Entry<String, DiscountItemRef> itemRefEntry : itemRefMap.entrySet()) {
				short limitNum = itemRefEntry.getValue().getPersonalLimitNum();
				if (limitNum != -1 && limitNum < 0) {
					if (logger.isDebugEnabled()) {
						logger.debug(limitNum);
					}
					error(ref, "个人限批数量非法");
				}
			}
		}

		/*
		for (Entry<String, DiscountConfigRef> entry : discountConfigRefMap.entrySet()) {
			DiscountConfigRef configRef = entry.getValue();
			short batch = configRef.getBatch();
			String beginTime = configRef.getBeginTime();
			String endTime = configRef.getEndTime();

			if (batch < 0) {
				if (logger.isDebugEnabled()) {
					logger.debug(batch);
				}
				error(ref, "个人限批数量非法");
			}

			String defualtTimeFormat = "yyyyMMddHHmmss";
			
			if (!checkTime(endTime, defualtTimeFormat)) {
				if (logger.isDebugEnabled()) {
					logger.debug(endTime);
				}
				error(ref, "结束时间非法");
			}

			if (!checkTime(beginTime, defualtTimeFormat)) {
				if (logger.isDebugEnabled()) {
					logger.debug(beginTime);
				}
				error(ref, "开始时间非法");
			}
		}
		*/
	}

	private boolean checkTime(String time, String format) {
		boolean flag = false;
		try {
			new SimpleDateFormat(format).parse(time);
			flag = true;
		} catch (ParseException e) {
			flag = false;
		}
		return flag;
	}

}
