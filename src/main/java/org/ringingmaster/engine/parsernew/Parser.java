package org.ringingmaster.engine.parsernew;

import io.reactivex.functions.Function;
import org.ringingmaster.engine.parsernew.assignparsetype.AssignParseType;
import org.ringingmaster.engine.touch.newcontainer.Touch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO comments???
 *
 * @author stevelake
 */
public class Parser implements Function<Touch, Parse> {

    private final Logger log = LoggerFactory.getLogger(Parser.class);

    @Override
    public Parse apply(Touch touch) {

        log.info("Parsing");


        new AssignParseType().parse(touch);


        //return new DefaultParse();

        return null;

    }
}
