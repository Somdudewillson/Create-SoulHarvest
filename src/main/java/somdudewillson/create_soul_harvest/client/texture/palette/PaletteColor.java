package somdudewillson.create_soul_harvest.client.texture.palette;

import java.util.Objects;

public class PaletteColor {
	private final int r;
	private final int g;
	private final int b;
	private final int count;
	private final int mergedFrom;

	// Cached Lab coordinates
	private transient Double labL = null;
	private transient Double labA = null;
	private transient Double labB = null;

	private PaletteColor(int r, int g, int b, int count, int mergedFrom) {
		this.r = Math.max(0, Math.min(255, r));
		this.g = Math.max(0, Math.min(255, g));
		this.b = Math.max(0, Math.min(255, b));
		this.count = Math.max(1, count);
		this.mergedFrom = Math.max(0, mergedFrom);
	}

	public PaletteColor(int r, int g, int b, int count) {
		this(r, g, b, count, 0);
	}

	public static PaletteColor fromRgb(int rgb, int count) {
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;
		return new PaletteColor(r, g, b, count);
	}

	public int getR() {
		return r;
	}

	public int getG() {
		return g;
	}

	public int getB() {
		return b;
	}

	public int getCount() {
		return count;
	}

	public int getMergedFrom() {
		return mergedFrom;
	}

	public int toRgbInt() {
		return (r << 16) | (g << 8) | b;
	}

	public int toArgbInt() {
		return 0xFF000000 | toRgbInt();
	}

	public String toHex() {
		return String.format("#%02X%02X%02X", r, g, b);
	}

	/**
	 * Merges this color with another, weighting the RGB values by their respective pixel counts.
	 */
	public PaletteColor merge(PaletteColor other) {
		long totalCount = (long) this.count + other.count;
		int newR = (int) Math.round(((double) this.r * this.count + (double) other.r * other.count) / totalCount);
		int newG = (int) Math.round(((double) this.g * this.count + (double) other.g * other.count) / totalCount);
		int newB = (int) Math.round(((double) this.b * this.count + (double) other.b * other.count) / totalCount);
		return new PaletteColor(newR, newG, newB, (int) Math.min(Integer.MAX_VALUE, totalCount), mergedFrom + other.mergedFrom + 1);
	}

	/**
	 * Calculates the perceptual color difference (CIELAB Delta E 1976) between this color and another.
	 */
	public double deltaE(PaletteColor other) {
		ensureLab();
		other.ensureLab();
		double dL = this.labL - other.labL;
		double da = this.labA - other.labA;
		double db = this.labB - other.labB;
		return Math.sqrt(dL * dL + da * da + db * db);
	}

	private synchronized void ensureLab() {
		if (labL != null) return;

		// Convert sRGB to linear RGB
		double rLin = pivotRgb(r / 255.0);
		double gLin = pivotRgb(g / 255.0);
		double bLin = pivotRgb(b / 255.0);

		// Convert linear RGB to CIEXYZ (D65 illuminant)
		double x = (0.4124564 * rLin + 0.3575761 * gLin + 0.1804375 * bLin) / 0.95047;
		double y = (0.2126729 * rLin + 0.7151522 * gLin + 0.0721750 * bLin) / 1.00000;
		double z = (0.0193339 * rLin + 0.1191920 * gLin + 0.9503041 * bLin) / 1.08883;

		// Convert CIEXYZ to CIELAB
		double fx = pivotXyz(x);
		double fy = pivotXyz(y);
		double fz = pivotXyz(z);

		this.labL = Math.max(0.0, 116.0 * fy - 16.0);
		this.labA = 500.0 * (fx - fy);
		this.labB = 200.0 * (fy - fz);
	}

	private static double pivotRgb(double n) {
		return (n > 0.04045) ? Math.pow((n + 0.055) / 1.055, 2.4) : (n / 12.92);
	}

	private static double pivotXyz(double n) {
		double delta = 6.0 / 29.0;
		double deltaSq = delta * delta;
		double deltaCb = deltaSq * delta;
		return (n > deltaCb) ? Math.cbrt(n) : (n / (3.0 * deltaSq) + 4.0 / 29.0);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PaletteColor that)) return false;
		return r == that.r && g == that.g && b == that.b;
	}

	@Override
	public int hashCode() {
		return Objects.hash(r, g, b);
	}

	@Override
	public String toString() {
		return String.format("PaletteColor[%s, count=%d]", toHex(), count);
	}
}
