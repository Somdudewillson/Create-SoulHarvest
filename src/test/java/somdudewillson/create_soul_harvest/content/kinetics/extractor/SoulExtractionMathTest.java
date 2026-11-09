package somdudewillson.create_soul_harvest.content.kinetics.extractor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SoulExtractionMathTest {

	@Test
	public void testMinimumRpmThreshold() {
		assertEquals(0.0f, SoulExtractionMath.calculatePoints(0.0f));
		assertEquals(0.0f, SoulExtractionMath.calculatePoints(10.0f));
		assertEquals(0.0f, SoulExtractionMath.calculatePoints(15.9f));
		assertEquals(0.0f, SoulExtractionMath.calculatePoints(-15.9f));

		assertTrue(SoulExtractionMath.calculatePoints(16.0f) >= 0.0f);
		assertTrue(SoulExtractionMath.calculatePoints(16.1f) > 0.0f);
	}

	@Test
	public void testMaxRpmPoints() {
		float maxPoints = SoulExtractionMath.calculatePoints(256.0f);
		assertEquals(100.0f, maxPoints, 0.001f);

		// Negative rotation (opposite direction) should produce the same points
		float negMaxPoints = SoulExtractionMath.calculatePoints(-256.0f);
		assertEquals(100.0f, negMaxPoints, 0.001f);

		// Exceeding 256 RPM clamps to 100 points
		float overMaxPoints = SoulExtractionMath.calculatePoints(512.0f);
		assertEquals(100.0f, overMaxPoints, 0.001f);
	}

	@Test
	public void testDiminishingReturnsCurve() {
		// Halfway between 16 and 256 is 136 RPM
		float midPoints = SoulExtractionMath.calculatePoints(136.0f);
		// Linear halfway would be 50 points; concave diminishing returns (alpha = 0.6) yields > 50 points (approx 65.97)
		assertTrue(midPoints > 60.0f, "Mid RPM should yield diminishing returns (>60 points), got: " + midPoints);

		// Quarter RPM
		float quarterPoints = SoulExtractionMath.calculatePoints(76.0f);
		assertTrue(quarterPoints > 35.0f, "Quarter RPM should yield > 35 points due to steep initial curve, got: " + quarterPoints);
	}

	@Test
	public void testMultiblockCeiling() {
		// 1 max extractor gives 100 points -> 100 / 250 = 40% of max potentia
		long oneExtractorPotentia = SoulExtractionMath.calculateTotalPotentia(100.0f, 100L);
		assertEquals(40L, oneExtractorPotentia);

		// 2 max extractors give 200 points -> 200 / 250 = 80% of max potentia
		long twoExtractorsPotentia = SoulExtractionMath.calculateTotalPotentia(200.0f, 100L);
		assertEquals(80L, twoExtractorsPotentia);

		// 2.5 max extractors give 250 points -> 100% of max potentia
		long limitPotentia = SoulExtractionMath.calculateTotalPotentia(250.0f, 100L);
		assertEquals(100L, limitPotentia);

		// 3 max extractors (300 points) hits the saturation limit (still 100L)
		long overLimitPotentia = SoulExtractionMath.calculateTotalPotentia(300.0f, 100L);
		assertEquals(100L, overLimitPotentia);
	}

	@Test
	public void testProportionalShareAllocation() {
		// Case 1: Two identical extractors at 256 RPM (100 pts each, total 200 pts, pool 80)
		long share1 = SoulExtractionMath.calculateShare(100.0f, 200.0f, 80L);
		long share2 = SoulExtractionMath.calculateShare(100.0f, 200.0f, 80L);
		assertEquals(40L, share1);
		assertEquals(40L, share2);

		// Case 2: Three identical extractors at 256 RPM (100 pts each, total 300 pts, pool capped at 100)
		long sA = SoulExtractionMath.calculateShare(100.0f, 300.0f, 100L);
		long sB = SoulExtractionMath.calculateShare(100.0f, 300.0f, 100L);
		long sC = SoulExtractionMath.calculateShare(100.0f, 300.0f, 100L);
		assertEquals(33L, sA);
		assertEquals(33L, sB);
		assertEquals(33L, sC);

		// Case 3: Unequal extractors (e.g. 100 pts vs 50 pts, total 150 pts, pool 60)
		long sLarge = SoulExtractionMath.calculateShare(100.0f, 150.0f, 60L);
		long sSmall = SoulExtractionMath.calculateShare(50.0f, 150.0f, 60L);
		assertEquals(40L, sLarge);
		assertEquals(20L, sSmall);
	}
}
