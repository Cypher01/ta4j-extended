package org.ta4j.core.indicators.helpers;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LogicIndicator extends CachedIndicator<Boolean> {
	private final List<Indicator<Boolean>> indicators;
	private final Predicate<List<Boolean>> transform;

	public static LogicIndicator not(Indicator<Boolean> indicator) {
		return new LogicIndicator(Collections.singletonList(indicator), indicatorList -> indicatorList.stream().noneMatch(value -> value));
	}

	public static LogicIndicator and(Indicator<Boolean>... indicators) {
		return and(Arrays.asList(indicators));
	}

	public static LogicIndicator and(List<Indicator<Boolean>> indicators) {
		return new LogicIndicator(indicators, indicatorList -> indicatorList.stream().allMatch(value -> value));
	}

	public static LogicIndicator or(Indicator<Boolean>... indicators) {
		return or(Arrays.asList(indicators));
	}

	public static LogicIndicator or(List<Indicator<Boolean>> indicators) {
		return new LogicIndicator(indicators, indicatorList -> indicatorList.stream().anyMatch(value -> value));
	}

	public LogicIndicator(List<Indicator<Boolean>> indicators, Predicate<List<Boolean>> transform) {
		super(indicators.get(0));

		this.indicators = indicators;
		this.transform = transform;
	}

	@Override
	protected Boolean calculate(int index) {
		return transform.test(indicators.stream().map(indicator -> indicator.getValue(index)).collect(Collectors.toList()));
	}

	@Override
	public int getUnstableBars() {
		return indicators.stream().mapToInt(Indicator::getUnstableBars).max().orElse(0);
	}
}
