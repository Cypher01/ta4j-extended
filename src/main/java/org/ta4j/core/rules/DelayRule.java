package org.ta4j.core.rules;

import org.ta4j.core.TradingRecord;

/**
 * Another indexes-based rule. Satisfied for every index after a defined fixed index.
 */
public class DelayRule extends AbstractRule {
	private final int index;

	public DelayRule(int index) {
		this.index = index;
	}

	@Override
	public boolean isSatisfied(int index, TradingRecord tradingRecord) {
		boolean satisfied = index > this.index;
		traceIsSatisfied(index, satisfied);
		return satisfied;
	}
}
