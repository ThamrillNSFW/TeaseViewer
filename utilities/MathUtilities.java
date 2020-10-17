package utilities;

public class MathUtilities {

	public static float clamp(float val) {
		return clamp(val, 0, 1);
	}

	public static float clamp(float val, float min, float max) {
		if (val < min) {
			return min;
		}
		if (val > max) {
			return max;
		}
		return val;
	}

	public static float discretizeFraction(float val, int intervals) {

		return Math.round(val * (intervals - 1)) * 1f / (intervals - 1);

	}

	public static float discretizedNthFraction(int n, int intervals) {
		return n*1f/(intervals+1);
	}

}
