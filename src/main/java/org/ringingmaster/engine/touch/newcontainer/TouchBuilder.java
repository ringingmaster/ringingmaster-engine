package org.ringingmaster.engine.touch.newcontainer;

import java.util.Optional;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class TouchBuilder {

    private Touch prototype;

    private Optional<String> title;

    public TouchBuilder defaults() {
        title = Optional.of("");
        return this;
    }

    TouchBuilder prototypeOf(Touch prototype) {
        this.prototype = prototype;
        return this;
    }

    TouchBuilder setTitle(String title) {
        this.title = Optional.of(title);
        return this;
    }

    Touch build() {
        return new Touch(title.orElseGet(()->prototype.getTitle()));
    }
}
