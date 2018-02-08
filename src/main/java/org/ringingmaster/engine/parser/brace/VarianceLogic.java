package org.ringingmaster.engine.parser.brace;

import org.ringingmaster.engine.parser.Parse;
import org.ringingmaster.engine.parser.ParseType;

import java.util.function.Function;

public class VarianceLogic extends SkeletalBraceLogic implements Function<Parse, Parse> {

    public VarianceLogic() {
        super(ParseType.VARIANCE_OPEN, ParseType.VARIANCE_CLOSE, "variance", 1);
    }
}
