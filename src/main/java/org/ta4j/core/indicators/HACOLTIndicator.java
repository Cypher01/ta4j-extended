package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.candles.RealBodyIndicator;
import org.ta4j.core.indicators.candles.RealRangeIndicator;
import org.ta4j.core.indicators.helpers.BooleanCombineIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LogicIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.MedianPriceIndicator;
import org.ta4j.core.indicators.helpers.OHLC4PriceIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.indicators.helpers.PreviousBooleanValueIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * Heikin Ashi Candles Oscillator Long Term (HACOLT) indicator by LazyBear.
 * <a href="https://www.tradingview.com/script/4zuhGaAU-Vervoort-Heiken-Ashi-LongTerm-Candlestick-Oscillator-LazyBear/">TradingView</a>
 *
 * TODO: The implementation is buggy, the values differ from TradingView
 *
 * Original PineScript implementation:
 *
 * length = input(defval=55, title="TEMA Period")
 * emaLength = input(defval=60, title="EMA Period")
 * candleSizeFactor = input(defval=1.1, title="Candle size factor")
 *
 * calc_tema(src, length) =>
 *     ema1 = ema(src, length)
 *     ema2 = ema(ema1, length)
 *     ema3 = ema(ema2, length)
 *     3 * (ema1 - ema2) + ema3
 *
 * haOpen = nz((haOpen[1] + ohlc4) / 2, ohlc4)
 * haClose = (haOpen + max(high, haOpen) + min(low, haOpen) + ohlc4) / 4
 * thaClose = calc_tema(haClose, length)
 * thl2 = calc_tema(hl2, length)
 * haCloseSmooth = 2 * thaClose - calc_tema(thaClose, length)
 * hl2Smooth = 2 * thl2 - calc_tema(thl2, length)
 * shortCandle = abs(close - open) < ((high - low) * candleSizeFactor)
 * keepn1 = ((haClose >= haOpen) and (haClose[1] >= haOpen[1])) or (close >= haClose) or (high > high[1]) or (low > low[1]) or (hl2Smooth >= haCloseSmooth)
 * keepall1 = keepn1 or (keepn1[1] and (close >= open) or (close >= close[1]))
 * keep13 = shortCandle and (high >= low[1])
 * utr = keepall1 or (keepall1[1] and keep13)
 * keepn2 = (haClose < haOpen) and (haClose[1] < haOpen[1]) or (hl2Smooth < haCloseSmooth)
 * keep23 = shortCandle and (low <= high[1])
 * keepall2 = keepn2 or (keepn2[1] and (close < open) or (close < close[1]))
 * dtr = keepall2 or (keepall2[1] and keep23)
 * upw = dtr == 0 and dtr[1] and utr
 * dnw = utr == 0 and utr[1] and dtr
 * upwWithOffset = upw != dnw ? upw : nz(upwWithOffset[1])
 *
 * buySig = upw or (not dnw and (na(upwWithOffset) ? 0 : upwWithOffset))
 * ltSellSig = close < ema(close, emaLength)
 * neutralSig = buySig or (ltSellSig ? 0 : nz(neutralSig[1]))
 * hacolt = buySig ? 1 : neutralSig ? 0 : -1
 *
 * Cleaned up PineScript v6 implementation:
 *
 * //@version=6
 * indicator("HACOLT", shorttitle = "HACOLT", overlay = false)
 *
 * import TradingView/ta/9
 *
 * length = input.int(55, title = "TEMA Period")
 * emaLength = input.int(60, title = "EMA Period")
 * candleSizeFactor = input.float(1.1, title = "Candle size factor")
 *
 * var float haOpen = na
 * haOpen := na(haOpen[1]) ? ohlc4 : (haOpen[1] + ohlc4) / 2
 * haClose = (haOpen + math.max(high, haOpen) + math.min(low, haOpen) + ohlc4) / 4
 *
 * thl2 = ta.tema(hl2, length)
 * thaClose = ta.tema(haClose, length)
 * hl2Smooth = 2 * thl2 - ta.tema(thl2, length)
 * haCloseSmooth = 2 * thaClose - ta.tema(thaClose, length)
 *
 * shortCandle = math.abs(close - open) < ((high - low) * candleSizeFactor)
 *
 * keepn1 = (haClose >= haOpen) and (haClose[1] >= haOpen[1]) or (close >= haClose) or (high > high[1]) or (low > low[1]) or (hl2Smooth >= haCloseSmooth)
 * keepall1 = keepn1 or keepn1[1] and (close >= open) or (close >= close[1])
 * keep13 = shortCandle and (high >= low[1])
 * keepn2 = (haClose < haOpen) and (haClose[1] < haOpen[1]) or (hl2Smooth < haCloseSmooth)
 * keep23 = shortCandle and (low <= high[1])
 * keepall2 = keepn2 or keepn2[1] and (close < open) or (close < close[1])
 *
 * dtr = keepall2 or keepall2[1] and keep23
 * utr = keepall1 or keepall1[1] and keep13
 * upw = not dtr and dtr[1] and utr
 * dnw = not utr and utr[1] and dtr
 * var bool upwWithOffset = false
 * upwWithOffset := upw != dnw ? upw : upwWithOffset[1]
 *
 * buySig = upw or not dnw and upwWithOffset
 * ltSellSig = close < ta.ema(close, emaLength)
 * var bool neutralSig = false
 * neutralSig := buySig or not ltSellSig and neutralSig[1]
 *
 * hacolt = buySig ? 1 : neutralSig ? 0 : -1
 *
 * plot(hacolt, style = plot.style_columns, color = hacolt > 0 ? color.green : hacolt < 0 ? color.red : color.blue, title = "HACOLT")
 */
public class HACOLTIndicator extends CachedIndicator<Num> {
	private final Indicator<Boolean> buySig;
	private final Indicator<Boolean> neutralSig;
	private final int unstableBars;

	public HACOLTIndicator(BarSeries series, int barCount, int barCountEma, double candleSizeFactor) {
		super(series);

		OpenPriceIndicator open = new OpenPriceIndicator(series);
		HighPriceIndicator high = new HighPriceIndicator(series);
		LowPriceIndicator low = new LowPriceIndicator(series);
		ClosePriceIndicator close = new ClosePriceIndicator(series);
		MedianPriceIndicator hl2 = new MedianPriceIndicator(series);
		OHLC4PriceIndicator ohlc4 = new OHLC4PriceIndicator(series);
		HeikinAshiOpenIndicator haOpen = new HeikinAshiOpenIndicator(ohlc4);
		HeikinAshiCloseIndicator haClose = new HeikinAshiCloseIndicator(high, low, ohlc4, haOpen);

		PreviousValueIndicator highPrev = new PreviousValueIndicator(high);
		PreviousValueIndicator lowPrev = new PreviousValueIndicator(low);
		PreviousValueIndicator closePrev = new PreviousValueIndicator(close);
		PreviousValueIndicator haOpenPrev = new PreviousValueIndicator(haOpen);
		PreviousValueIndicator haClosePrev = new PreviousValueIndicator(haClose);

		TripleEMAIndicator thl2 = new TripleEMAIndicator(hl2, barCount);
		TripleEMAIndicator thaClose = new TripleEMAIndicator(haClose, barCount);
		CombineIndicator hl2Smooth = CombineIndicator.minus(TransformIndicator.multiply(thl2, 2), new TripleEMAIndicator(thl2, barCount));
		CombineIndicator haCloseSmooth = CombineIndicator.minus(TransformIndicator.multiply(thaClose, 2), new TripleEMAIndicator(thaClose, barCount));

		BooleanCombineIndicator shortCandle = BooleanCombineIndicator.isLessThan(TransformIndicator.abs(new RealBodyIndicator(series)), TransformIndicator.multiply(new RealRangeIndicator(series), candleSizeFactor));

		BooleanCombineIndicator haGreen = BooleanCombineIndicator.isGreaterThanOrEqual(haClose, haOpen);
		BooleanCombineIndicator haPrevGreen = BooleanCombineIndicator.isGreaterThanOrEqual(haClosePrev, haOpenPrev);
		BooleanCombineIndicator closeGreaterHaClose = BooleanCombineIndicator.isGreaterThanOrEqual(close, haClose);
		BooleanCombineIndicator highRising = BooleanCombineIndicator.isGreaterThan(high, highPrev);
		BooleanCombineIndicator lowRising = BooleanCombineIndicator.isGreaterThan(low, lowPrev);
		BooleanCombineIndicator hl2SmoothGreaterHaCloseSmooth = BooleanCombineIndicator.isGreaterThanOrEqual(hl2Smooth, haCloseSmooth);
		LogicIndicator keepn1 = LogicIndicator.or(LogicIndicator.and(haGreen, haPrevGreen), closeGreaterHaClose, highRising, lowRising, hl2SmoothGreaterHaCloseSmooth);

		BooleanCombineIndicator green = BooleanCombineIndicator.isGreaterThanOrEqual(close, open);
		BooleanCombineIndicator closeRising = BooleanCombineIndicator.isGreaterThanOrEqual(close, closePrev);
		LogicIndicator keepall1 = LogicIndicator.or(keepn1, LogicIndicator.and(new PreviousBooleanValueIndicator(keepn1), LogicIndicator.or(green, closeRising)));

		BooleanCombineIndicator highGreaterPrevLow = BooleanCombineIndicator.isGreaterThanOrEqual(high, lowPrev);
		LogicIndicator keep13 = LogicIndicator.and(shortCandle, highGreaterPrevLow);

		BooleanCombineIndicator haRed = BooleanCombineIndicator.isLessThan(haClose, haOpen);
		BooleanCombineIndicator haPrevRed = BooleanCombineIndicator.isLessThan(haClosePrev, haOpenPrev);
		BooleanCombineIndicator hl2SmoothLessHaCloseSmooth = BooleanCombineIndicator.isLessThan(hl2Smooth, haCloseSmooth);
		LogicIndicator keepn2 = LogicIndicator.and(haRed, LogicIndicator.or(haPrevRed, hl2SmoothLessHaCloseSmooth));

		LogicIndicator keep23 = LogicIndicator.and(shortCandle, BooleanCombineIndicator.isLessThanOrEqual(low, highPrev));

		BooleanCombineIndicator red = BooleanCombineIndicator.isLessThan(close, open);
		BooleanCombineIndicator closeFalling = BooleanCombineIndicator.isLessThan(close, closePrev);
		LogicIndicator keepall2 = LogicIndicator.or(keepn2, LogicIndicator.and(new PreviousBooleanValueIndicator(keepn2), LogicIndicator.or(red, closeFalling)));

		LogicIndicator utr = LogicIndicator.or(keepall1, LogicIndicator.and(new PreviousBooleanValueIndicator(keepall1), keep13));
		LogicIndicator dtr = LogicIndicator.or(keepall2, LogicIndicator.and(new PreviousBooleanValueIndicator(keepall2), keep23));
		LogicIndicator upw = LogicIndicator.and(LogicIndicator.not(dtr), new PreviousBooleanValueIndicator(dtr), utr);
		LogicIndicator dnw = LogicIndicator.and(LogicIndicator.not(utr), new PreviousBooleanValueIndicator(utr), dtr);
		UpwWithOffset upwWithOffset = new UpwWithOffset(upw, dnw);

		this.buySig = LogicIndicator.or(upw, LogicIndicator.and(LogicIndicator.not(dnw), upwWithOffset));
		BooleanCombineIndicator ltSellSig = BooleanCombineIndicator.isLessThan(close, new EMAIndicator(close, barCountEma));
		this.neutralSig = new NeutralSig(this.buySig, ltSellSig);
		this.unstableBars = Math.max(barCount, barCountEma);
	}

	@Override
	protected Num calculate(int index) {
		if (buySig.getValue(index)) {
			return one();
		} else if (neutralSig.getValue(index)) {
			return zero();
		} else {
			return numOf(-1);
		}
	}

	@Override
	public int getUnstableBars() {
		return unstableBars;
	}

	public static class HeikinAshiOpenIndicator extends CachedIndicator<Num> {
		private final OHLC4PriceIndicator ohlc4;

		public HeikinAshiOpenIndicator(OHLC4PriceIndicator ohlc4) {
			super(ohlc4);

			this.ohlc4 = ohlc4;
		}

		@Override
		protected Num calculate(int index) {
			if (index == 0) {
				return ohlc4.getValue(index);
			} else {
				return getValue(index - 1).plus(ohlc4.getValue(index)).dividedBy(numOf(2));
			}
		}

		@Override
		public int getUnstableBars() {
			return 0;
		}
	}

	public static class HeikinAshiCloseIndicator extends CachedIndicator<Num> {
		private final HighPriceIndicator high;
		private final LowPriceIndicator low;
		private final OHLC4PriceIndicator ohlc4;
		private final HeikinAshiOpenIndicator haOpen;

		public HeikinAshiCloseIndicator(HighPriceIndicator high, LowPriceIndicator low, OHLC4PriceIndicator ohlc4, HeikinAshiOpenIndicator haOpen) {
			super(high);

			this.high = high;
			this.low = low;
			this.ohlc4 = ohlc4;
			this.haOpen = haOpen;
		}

		@Override
		protected Num calculate(int index) {
			return haOpen.getValue(index)
					.plus(high.getValue(index).max(haOpen.getValue(index)))
					.plus(low.getValue(index).min(haOpen.getValue(index)))
					.plus(ohlc4.getValue(index))
					.dividedBy(numOf(4));
		}

		@Override
		public int getUnstableBars() {
			return 0;
		}
	}

	private static class UpwWithOffset extends CachedIndicator<Boolean> {
		private final Indicator<Boolean> upw;
		private final Indicator<Boolean> dnw;

		private UpwWithOffset(Indicator<Boolean> upw, Indicator<Boolean> dnw) {
			super(upw);

			this.upw = upw;
			this.dnw = dnw;
		}

		@Override
		protected Boolean calculate(int index) {
			Boolean prevValue;

			if (index == 0) {
				prevValue = false;
			} else {
				prevValue = getValue(index - 1);
			}

			if (upw.getValue(index) != dnw.getValue(index)) {
				return upw.getValue(index);
			} else {
				return prevValue;
			}
		}

		@Override
		public int getUnstableBars() {
			return 0;
		}
	}

	private static class NeutralSig extends CachedIndicator<Boolean> {
		private final Indicator<Boolean> buySig;
		private final Indicator<Boolean> ltSellSig;

		public NeutralSig(Indicator<Boolean> buySig, Indicator<Boolean> ltSellSig) {
			super(buySig);

			this.buySig = buySig;
			this.ltSellSig = ltSellSig;
		}

		@Override
		protected Boolean calculate(int index) {
			Boolean prevValue;

			if (index == 0) {
				prevValue = false;
			} else {
				prevValue = getValue(index - 1);
			}

			return buySig.getValue(index) || !ltSellSig.getValue(index) && prevValue;
		}

		@Override
		public int getUnstableBars() {
			return 0;
		}
	}
}
