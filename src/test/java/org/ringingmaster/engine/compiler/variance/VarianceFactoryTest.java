package org.ringingmaster.engine.compiler.variance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
@RunWith(Parameterized.class)
public class VarianceFactoryTest {


    private final String varianceString;
    private final Class instanceType;


    public VarianceFactoryTest(String varianceString, Class instanceType) {
        this.varianceString = varianceString;
        this.instanceType = instanceType;
    }



    @Parameterized.Parameters
    public static Collection<Object[]> testValues() {
        return Arrays.asList(new Object[][]{
                {"-o", OddEvenVariance.class},
                {"-O", OddEvenVariance.class},
                {"-odd", OddEvenVariance.class},
                {"-ODD", OddEvenVariance.class},
                {"-e", OddEvenVariance.class},
                {"-E", OddEvenVariance.class},
                {"-even", OddEvenVariance.class},
                {"-EVEN", OddEvenVariance.class},

                {"+o", OddEvenVariance.class},
                {"+O", OddEvenVariance.class},
                {"+odd", OddEvenVariance.class},
                {"+ODD", OddEvenVariance.class},
                {"+e", OddEvenVariance.class},
                {"+E", OddEvenVariance.class},
                {"+even", OddEvenVariance.class},
                {"+EVEN", OddEvenVariance.class},

                {"-1,3", SpecifiedPartsVariance.class},
                {"+1,3", SpecifiedPartsVariance.class},
        });
    }



    @Test
    public void happyPath() {
        Variance variance = VarianceFactory.parseVariance(varianceString);
        assertTrue(instanceType.isInstance(variance));

    }


}