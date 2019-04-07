package org.ringingmaster.engine.parser.brace;

import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;

import java.util.function.Function;

public class ValidateVarianceMatchingBraceLogic extends SkeletalMatchingBraceLogic implements Function<Parse, Parse> {

    public ValidateVarianceMatchingBraceLogic() {
        super(ParseType.VARIANCE_OPEN, ParseType.VARIANCE_CLOSE, "variance", 1);
    }
}
