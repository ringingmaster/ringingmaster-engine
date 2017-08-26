package org.ringingmaster.engine.touch.newcontainer;

import net.jcip.annotations.Immutable;

/**
 * Raw immutable POJO for a touch
 *
 * @author Lake
 */
@Immutable
public class Touch {

    private final String title;

    Touch(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
