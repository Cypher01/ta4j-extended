package org.ta4j.core.rules;

import org.ta4j.core.Rule;
import org.ta4j.core.TradingRecord;

/**
 * Rule to check if another rule is satisfied for all indexes in a given period.
 */
public class LookbackAllRule extends AbstractRule {
	private final Rule rule;
	private final int barCount;

	public LookbackAllRule(Rule rule, int barCount) {
		this.rule = rule;
		this.barCount = barCount;
	}

	@Override
	public boolean isSatisfied(int index, TradingRecord tradingRecord) {
		int lookbackIndex = Math.max(-1, index - barCount);
		boolean satisfied = true;

		for (int i = index; i > lookbackIndex; i--) {
			satisfied = satisfied && rule.isSatisfied(i);
		}

		traceIsSatisfied(index, satisfied);
		return satisfied;
	}
}
