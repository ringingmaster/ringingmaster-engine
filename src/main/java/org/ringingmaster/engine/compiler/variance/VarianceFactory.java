package org.ringingmaster.engine.compiler.variance;

import com.google.common.base.Function;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.compiler.variance.OddEvenVariance.OddEvenVarianceType.EVEN;
import static org.ringingmaster.engine.compiler.variance.OddEvenVariance.OddEvenVarianceType.ODD;
import static org.ringingmaster.engine.compiler.variance.VarianceLogicType.INCLUDE;
import static org.ringingmaster.engine.compiler.variance.VarianceLogicType.OMIT;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class VarianceFactory {

    private static final Logger log = LoggerFactory.getLogger(VarianceFactory.class);

    // includes the non capturing group
    public static final String OMIT_INCLUDE_REGEX = "(?:[-+])";

    // includes the non capturing group
    public static final String ODD_EVEN_REGEX = "(?:odd|even|[oe])";

    // includes the non capturing group
    public static final String SPECIFIED_PARTS_REGEX = "(?:(?:[0-9]+)(?:[,][0-9]+)*)";


    public static Variance nullVariance() {
        return NullVariance.getInstance();
    }


    //NOTE: This is closely related to the regex in AssignParseType::addVarianceLexerDefinitions
    private static final String REGEX = "(?i)(" + OMIT_INCLUDE_REGEX + ")(?:(" + ODD_EVEN_REGEX + ")|(" + SPECIFIED_PARTS_REGEX + "+))";
    private static final Pattern PATTERN = Pattern.compile(REGEX);//. represents single character

    public static Variance parseVariance(String input) {

        log.trace("Parsing variance string [ {} ]", input);

        String varianceString = input.toUpperCase();

        Matcher m = PATTERN.matcher(varianceString);
        if (m.find()) {
            checkState(m.groupCount() == 3);

            // Process the Omit / Include
            String omitInclude = m.group(1);
            VarianceLogicType varianceLogicType = parseOmitInclude(omitInclude);

            // Process parts
            String oddEven = m.group(2);
            String parts = m.group(3);
            checkState(oddEven == null ^ parts == null);

            if (oddEven != null) {
                return new OddEvenVariance(varianceLogicType, parseOddEven(oddEven));
            }

            else if (parts != null) {
                return new SpecifiedPartsVariance(varianceLogicType, parseParts(parts));
            }
        }

        throw new IllegalArgumentException("[" + input + "] does not form a valid variance definition");
    }


    static VarianceLogicType parseOmitInclude(String omitInclude) {

        switch (omitInclude) {
            case "-":
                return OMIT;
            case "+":
                return INCLUDE;
            default:
                throw new IllegalArgumentException("[" + omitInclude + "] is not a valid VarianceLogicType");
        }
    }

    static OddEvenVariance.OddEvenVarianceType parseOddEven(String oddEven) {

        switch (oddEven) {
            case "odd":
            case "o":
                return ODD;
            case "even":
            case "e":
                return EVEN;
            default:
                throw new IllegalArgumentException("[" + oddEven + "] is not a valid OddEvenVarianceType");
        }
    }

    static Set<Integer> parseParts(String parts) {
        return Arrays.stream(parts.split(","))
                .map(Integer::parseInt)
                .map(new Function<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer apply(@Nullable Integer input) {
                        return input.intValue() - 1 ;
                    }
                })
                .collect(Collectors.toSet());
    }
}
