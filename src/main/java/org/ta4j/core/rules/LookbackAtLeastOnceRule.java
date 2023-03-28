package org.ta4j.core.rules;

import org.ta4j.core.Rule;
import org.ta4j.core.TradingRecord;

/**
 * Rule to check if another rule is satisfied at least once in a given period.
 */
public class LookbackAtLeastOnceRule extends AbstractRule {
	private final Rule rule;
	private final int barCount;

	public LookbackAtLeastOnceRule(Rule rule, int barCount) {
		this.rule = rule;
		this.barCount = barCount;
	}

	@Override
	public boolean isSatisfied(int index, TradingRecord tradingRecord) {
		int lookbackIndex = Math.max(-1, index - barCount);
		boolean satisfied = false;

		for (int i = index; i > lookbackIndex; i--) {
			if (rule.isSatisfied(i)) {
				satisfied = true;
				break;
			}
		}

		traceIsSatisfied(index, satisfied);
		return satisfied;
	}
}
