package org.ta4j.core.indicators.gchannel;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * Internal combined indicator that computes both the upper and lower G-Channel
 * band values together, returning them as a {@code Num[2]} array where index 0
 * is the upper band and index 1 is the lower band.
 *
 * <p>This class solves the {@link StackOverflowError} that occurred when the
 * upper and lower band indicators referenced each other and were computed
 * recursively on large bar series. The fix is twofold:
 * <ol>
 *   <li>Both values are computed in a single {@link CachedIndicator} to halve
 *       the recursion depth (one chain instead of two cross-referencing chains).</li>
 *   <li>{@link #getValue(int)} pre-populates the cache iteratively for all
 *       indices below the requested one before delegating to the caching layer,
 *       so the effective recursion depth when {@code calculate(i)} calls
 *       {@code getValue(i-1)} is always just one hop into an already-cached
 *       value.</li>
 * </ol>
 */
class GChannelBaseIndicator extends CachedIndicator<Num[]> {

    private final Indicator<Num> indicator;
    private final int barCount;
    private final Num numBarCount;

    GChannelBaseIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator);
        this.indicator = indicator;
        this.barCount = barCount;
        this.numBarCount = indicator.getBarSeries().numFactory().numOf(barCount);
    }

    /**
     * Overrides the default caching {@code getValue} to pre-populate the cache
     * sequentially from the last cached index up to {@code index - 1} before
     * computing {@code index} itself.
     *
     * <p>Without this pre-population, calling {@code getValue(N)} on a series
     * with N uncached bars would create a recursion chain N levels deep
     * ({@code getValue(N)} → {@code calculate(N)} → {@code getValue(N-1)} → …),
     * causing a {@link StackOverflowError} for large bar series.
     *
     * <p>With sequential pre-population every {@code calculate(i)} call finds
     * {@code i-1} already in the cache, so the recursion depth is bounded by a
     * small constant regardless of N.
     */
    @Override
    public Num[] getValue(int index) {
        if (highestResultIndex < index - 1) {
            int startFrom = Math.max(getBarSeries().getBeginIndex(), highestResultIndex + 1);
            for (int i = startFrom; i < index; i++) {
                super.getValue(i);
            }
        }
        return super.getValue(index);
    }

    @Override
    protected Num[] calculate(int index) {
        if (index == 0) {
            Num zero = getBarSeries().numFactory().zero();
            return new Num[]{zero, zero};
        }

        Num[] prev = getValue(index - 1);
        Num prevUpper = prev[0];
        Num prevLower = prev[1];

        Num price = indicator.getValue(index);
        Num diff = prevUpper.minus(prevLower).dividedBy(numBarCount);

        Num upper = price.max(prevUpper).minus(diff);
        Num lower = price.min(prevLower).plus(diff);

        return new Num[]{upper, lower};
    }

    @Override
    public int getCountOfUnstableBars() {
        return barCount;
    }
}

