package org.ringingmaster.engine.notation;

import javax.annotation.concurrent.Immutable;

/**
 * TODO comments???
 * User: Stephen
 */
@Immutable
class DefaultCallingPosition implements CallingPosition {

    private final int callInitiationRow;
    private final int leadOfTenor;
    private final String name;

    DefaultCallingPosition(int callInitiationRow, int leadOfTenor, String name) {
        this.callInitiationRow = callInitiationRow;
        this.leadOfTenor = leadOfTenor;
        this.name = name;
    }

    @Override
    public int getCallInitiationRow() {
        return callInitiationRow;
    }

    @Override
    public int getLeadOfTenor() {
        return leadOfTenor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(CallingPosition other) {
        int result = Integer.compare(getLeadOfTenor(), other.getLeadOfTenor());
        if (result != 0) {
            return result;
        }
        result = Integer.compare(getCallInitiationRow(), other.getCallInitiationRow());
        if (result != 0) {
            return result;
        }

        return 0;
    }

    @Override
    public String toString() {
        return "{'" + name + '\'' +
                ", lead " + leadOfTenor +
                "/row " + callInitiationRow +
                '}';
    }
}
