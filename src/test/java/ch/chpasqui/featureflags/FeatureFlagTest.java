package ch.chpasqui.featureflags;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FeatureFlagTest {
	@Test 
	void zeroPercentIsAlwaysFalse() {
		FeatureFlag flag = new FeatureFlag("test", true, 0);
		for (int i = 0; i < 1000; i++){
			assertFalse(flag.isEnabledFor(String.valueOf(i)));
		} 
	}

	@Test
	void hundredPercentIsAlwaysTrue() {
		FeatureFlag flag = new FeatureFlag("test", true, 100);
		for (int i = 0; i < 1000; i++){
			assertTrue(flag.isEnabledFor(String.valueOf(i)));
		} 
	}

	@Test
	void sameUserAlwaysGetSameResult() {
		FeatureFlag flag = new FeatureFlag("test", true, 30);
		boolean first = flag.isEnabledFor("42");
		for (int i = 0; i < 1000; i++){
			assertEquals(first, flag.isEnabledFor("42"));
		}
	}
	
	@Test
	void isEnabledOffAlwaysFalse() {
		FeatureFlag flag = new FeatureFlag("test", false, 30);
		for (int i = 0; i < 1000; i++){
			assertFalse(flag.isEnabledFor(String.valueOf(i)));
		} 
	}
}
