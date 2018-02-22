package org.ringingmaster.engine.parser.brace;

import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;

import java.util.function.Function;

public class MultiplierGroupLogic extends SkeletalBraceLogic implements Function<Parse, Parse> {

    public MultiplierGroupLogic() {
        super(ParseType.MULTIPLIER_GROUP_OPEN, ParseType.MULTIPLIER_GROUP_CLOSE, "group", 4);
    }
}
