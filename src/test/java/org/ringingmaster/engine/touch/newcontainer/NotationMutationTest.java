package org.ringingmaster.engine.touch.newcontainer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.junit.Assert;
import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NotationMutationTest {


    public static final NotationBody METHOD_A_6_BELL = buildNotation(NumberOfBells.BELLS_6, "METHOD A", "12");
    public static final NotationBody METHOD_B_6_BELL = buildNotation(NumberOfBells.BELLS_6, "METHOD B", "14");
    public static final NotationBody METHOD_C_6_BELL = buildNotation(NumberOfBells.BELLS_6, "METHOD C", "16");
    public static final NotationBody METHOD_D_6_BELL = buildNotation(NumberOfBells.BELLS_6, "METHOD D", "34");

    public static final NotationBody METHOD_A_7_BELL = buildNotation(NumberOfBells.BELLS_7, "METHOD A", "1");

    public static final NotationBody METHOD_A_8_BELL = buildNotation(NumberOfBells.BELLS_8, "METHOD A", "12");
    public static final NotationBody METHOD_B_8_BELL = buildNotation(NumberOfBells.BELLS_8, "METHOD B", "14");
    public static final NotationBody METHOD_C_8_BELL = buildNotation(NumberOfBells.BELLS_8, "METHOD C", "16");
    public static final NotationBody METHOD_D_8_BELL = buildNotation(NumberOfBells.BELLS_8, "METHOD D", "18");


    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableTouch observableTouch = new ObservableTouch();

        assertEquals(ImmutableList.of(), observableTouch.get().getAllNotations());
    }

    @Test
    public void canAddAndRemoveNotations() {

        ObservableTouch touch = new ObservableTouch();
        touch.addNotation(METHOD_A_6_BELL);

        List<NotationBody> retrievedNotations = touch.get().getAllNotations();
        assertEquals(1, retrievedNotations.size());
        assertEquals(METHOD_A_6_BELL, Iterators.getOnlyElement(retrievedNotations.iterator()));

        touch.addNotation(METHOD_B_6_BELL);

        retrievedNotations = touch.get().getAllNotations();
        assertEquals(2, retrievedNotations.size());

        touch.removeNotation(METHOD_A_6_BELL);
        retrievedNotations = touch.get().getAllNotations();
        assertEquals(METHOD_B_6_BELL, Iterators.getOnlyElement(retrievedNotations.iterator()));
    }


    @Test(expected = IllegalArgumentException.class)
    public void addingDuplicateNotationNameThrows() {
        ObservableTouch touch = null;
        NotationBody mockNotation2 = null;
        try {
            NotationBody mockNotation1 = mock(NotationBody.class);
            when(mockNotation1.getName()).thenReturn("Duplicate Name");
            when(mockNotation1.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);

            mockNotation2 = mock(NotationBody.class);
            when(mockNotation2.getName()).thenReturn("Duplicate Name");
            when(mockNotation2.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);

            touch = new ObservableTouch();
            touch.addNotation(mockNotation1);
        }
        catch (Exception e) {
            fail();
        }
        touch.addNotation(mockNotation2);
    }

    @Test
    public void notationCanBeExchanged() {
        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(NumberOfBells.BELLS_6);

        touch.addNotation(METHOD_A_6_BELL);
        assertEquals(METHOD_A_6_BELL, Iterators.getOnlyElement(touch.get().getAllNotations().iterator()));

        touch.updateNotation(METHOD_A_6_BELL, METHOD_B_6_BELL);
        assertEquals(METHOD_B_6_BELL, Iterators.getOnlyElement(touch.get().getAllNotations().iterator()));
    }

    @Test
    public void addingFirstNotationToNonSplicedSetsDefaultNotation() {
        ObservableTouch touch = new ObservableTouch();
        touch.setSpliced(false);
        touch.addNotation(METHOD_A_8_BELL);

        assertEquals(Optional.of(METHOD_A_8_BELL), touch.get().getNonSplicedActiveNotation());
        assertFalse(touch.get().isSpliced());
    }

    @Test
    public void addingFirstNotationToSplicedSetsDefaultNotation() {
        ObservableTouch touch = new ObservableTouch();
        touch.setSpliced(true);
        touch.addNotation(METHOD_A_8_BELL);

        assertEquals(Optional.of(METHOD_A_8_BELL), touch.get().getNonSplicedActiveNotation());
        assertFalse(touch.get().isSpliced());
    }

    @Test
    public void removingOnlyNotationRemovesActiveNotation() throws CloneNotSupportedException {
        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(NumberOfBells.BELLS_8);

        touch.addNotation(METHOD_A_8_BELL);

        touch.removeNotation(METHOD_A_8_BELL);
        assertEquals(Optional.empty() ,touch.get().getNonSplicedActiveNotation());
    }

    @Test
    public void removingActiveNotationChoosesNextNotationAlphabetically() {
        ObservableTouch touch = new ObservableTouch();
        touch.setSpliced(false);

        touch.addNotation(METHOD_A_6_BELL);
        touch.addNotation(METHOD_B_6_BELL);
        touch.addNotation(METHOD_C_6_BELL);
        touch.addNotation(METHOD_D_6_BELL);

        touch.setNonSplicedActiveNotation(METHOD_C_6_BELL);
        Assert.assertEquals(Optional.of(METHOD_C_6_BELL), touch.get().getNonSplicedActiveNotation());

        touch.removeNotation(METHOD_C_6_BELL);
        Assert.assertEquals(Optional.of(METHOD_D_6_BELL), touch.get().getNonSplicedActiveNotation());
        touch.removeNotation(METHOD_D_6_BELL);
        Assert.assertEquals(Optional.of(METHOD_B_6_BELL), touch.get().getNonSplicedActiveNotation());
        touch.removeNotation(METHOD_A_6_BELL);
        Assert.assertEquals(Optional.of(METHOD_B_6_BELL), touch.get().getNonSplicedActiveNotation());
        touch.removeNotation(METHOD_B_6_BELL);
        assertFalse(touch.get().getNonSplicedActiveNotation().isPresent());
    }

    @Test
    public void removingActiveNotationOnlyChoosesValidNotations() {
        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(NumberOfBells.BELLS_6);
        touch.setSpliced(false);

        touch.addNotation(METHOD_A_6_BELL);
        touch.addNotation(METHOD_B_8_BELL);//Invalid number of bells
        touch.addNotation(METHOD_C_6_BELL);
        touch.addNotation(METHOD_D_8_BELL);//Invalid number of bells

        touch.setNonSplicedActiveNotation(METHOD_C_6_BELL);
        Assert.assertEquals(Optional.of(METHOD_C_6_BELL), touch.get().getNonSplicedActiveNotation());

        touch.removeNotation(METHOD_C_6_BELL);
        Assert.assertEquals(Optional.of(METHOD_A_6_BELL), touch.get().getNonSplicedActiveNotation());
        touch.removeNotation(METHOD_A_6_BELL);
        assertFalse(touch.get().getNonSplicedActiveNotation().isPresent());
    }

    @Test
    public void removingNotationSwitchesToLexicographicallyNextActiveNotationWithinClosestNumberOfBells() {
        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(NumberOfBells.BELLS_8);

        touch.addNotation(METHOD_A_8_BELL);
        touch.addNotation(METHOD_B_8_BELL);
        touch.addNotation(METHOD_C_6_BELL);
        Assert.assertEquals(Optional.of(METHOD_A_8_BELL), touch.get().getNonSplicedActiveNotation());

        touch.setNonSplicedActiveNotation(METHOD_B_8_BELL);
        Assert.assertEquals(Optional.of(METHOD_B_8_BELL), touch.get().getNonSplicedActiveNotation());

        touch.removeNotation(METHOD_B_8_BELL);
        Assert.assertEquals(Optional.of(METHOD_A_8_BELL), touch.get().getNonSplicedActiveNotation());

        touch.removeNotation(METHOD_A_8_BELL);
        Assert.assertEquals(Optional.of(METHOD_C_6_BELL), touch.get().getNonSplicedActiveNotation());
    }

    @Test
    public void removingNotationSwitchesToClosestNumberOfBellsHighestFirst() {
        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(NumberOfBells.BELLS_8);

        touch.addNotation(METHOD_A_6_BELL);
        touch.addNotation(METHOD_A_7_BELL);
        touch.addNotation(METHOD_A_8_BELL);
        Assert.assertEquals(Optional.of(METHOD_A_6_BELL), touch.get().getNonSplicedActiveNotation());

        touch.setNonSplicedActiveNotation(METHOD_A_7_BELL);
        Assert.assertEquals(Optional.of(METHOD_A_7_BELL), touch.get().getNonSplicedActiveNotation());

        touch.removeNotation(METHOD_A_7_BELL);
        Assert.assertEquals(Optional.of(METHOD_A_8_BELL), touch.get().getNonSplicedActiveNotation());

        touch.removeNotation(METHOD_A_8_BELL);
        Assert.assertEquals(Optional.of(METHOD_A_6_BELL), touch.get().getNonSplicedActiveNotation());
    }

    @Test
    public void addingNotationsWithDifferentNumberOfBellsFiltersInappropriateNotations() {
        ObservableTouch touch = new ObservableTouch();

        touch.setSpliced(false);
        touch.addNotation(METHOD_A_6_BELL);
        touch.addNotation(METHOD_B_6_BELL);
        touch.addNotation(METHOD_A_8_BELL);

        assertEquals(3, touch.get().getAllNotations().size());
        assertEquals(1, touch.get().getNotationsInUse().size());
        assertEquals(2, touch.get().getValidNotations().size());

        touch.setSpliced(true);
        assertEquals(3, touch.get().getAllNotations().size());
        assertEquals(2, touch.get().getNotationsInUse().size());
        assertEquals(2, touch.get().getValidNotations().size());
    }

    @Test
    public void settingActiveNotationUnsetsSpliced() {
        ObservableTouch touch = new ObservableTouch();

        touch.addNotation(METHOD_A_6_BELL);
        touch.addNotation(METHOD_B_6_BELL);

        touch.setSpliced(true);
        assertEquals(true, touch.get().isSpliced());

        touch.setNonSplicedActiveNotation(METHOD_A_6_BELL);
        assertEquals(false, touch.get().isSpliced());
    }

    private static NotationBody buildNotation(NumberOfBells bells, String name, String notation1) {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(bells)
                .setName(name)
                .setUnfoldedNotationShorthand(notation1)
                .build();
    }

}