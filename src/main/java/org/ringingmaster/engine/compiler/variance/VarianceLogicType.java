package org.ringingmaster.engine.compiler.variance;

/**
 * TODO comments???
 * User: Stephen
 */
enum VarianceLogicType {
    OMIT("Omit from"),
    INCLUDE("Include in")
    ;

    private final String humanReadable;

    VarianceLogicType(String humanReadable) {
        this.humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return humanReadable;
    }

}
