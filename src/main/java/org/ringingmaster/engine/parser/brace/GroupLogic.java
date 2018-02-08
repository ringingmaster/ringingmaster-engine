package org.ringingmaster.engine.parser.brace;

import org.ringingmaster.engine.parser.Parse;
import org.ringingmaster.engine.parser.ParseType;

import java.util.function.Function;

public class GroupLogic extends SkeletalBraceLogic implements Function<Parse, Parse> {

    public GroupLogic() {
        super(ParseType.GROUP_OPEN, ParseType.GROUP_CLOSE, "group", 4);
    }
}
