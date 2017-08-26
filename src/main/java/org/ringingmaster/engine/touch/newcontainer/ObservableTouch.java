package org.ringingmaster.engine.touch.newcontainer;


import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class ObservableTouch {

    private Touch currentTouch = new TouchBuilder().defaults().build();
    private BehaviorSubject<Touch> subject = BehaviorSubject.create();

    Observable<Touch> observable() {
        return subject;
    };

    void setTitle(String title) {
        checkNotNull(title);

        if (Objects.equals(currentTouch.getTitle(), title)) {
            return;
        }

        currentTouch = new TouchBuilder()
                .prototypeOf(currentTouch)
                .setTitle(title)
                .build();

        subject.onNext(currentTouch);
    }


}
