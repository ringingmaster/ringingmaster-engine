package org.ringingmaster.engine.composition;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import org.junit.Assert;
import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
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

    private static NotationBody buildNotation(NumberOfBells bells, String name, String notation1) {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(bells)
                .setName(name)
                .setUnfoldedNotationShorthand(notation1)
                .build();
    }


    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableComposition observableComposition = new ObservableComposition();

        assertEquals(ImmutableSet.of(), observableComposition.get().getAllNotations());
    }

    @Test
    public void canAddAndRemoveNotations() {

        ObservableComposition composition = new ObservableComposition();
        composition.addNotation(METHOD_A_6_BELL);

        Set<NotationBody> retrievedNotations = composition.get().getAllNotations();
        assertEquals(1, retrievedNotations.size());
        assertEquals(METHOD_A_6_BELL, Iterators.getOnlyElement(retrievedNotations.iterator()));

        composition.addNotation(METHOD_B_6_BELL);

        retrievedNotations = composition.get().getAllNotations();
        assertEquals(2, retrievedNotations.size());

        composition.removeNotation(METHOD_A_6_BELL);
        retrievedNotations = composition.get().getAllNotations();
        assertEquals(METHOD_B_6_BELL, Iterators.getOnlyElement(retrievedNotations.iterator()));
    }


    @Test(expected = IllegalArgumentException.class)
    public void addingDuplicateNotationNameThrows() {
        ObservableComposition composition = null;
        NotationBody mockNotation2 = null;
        try {
            NotationBody mockNotation1 = mock(NotationBody.class);
            when(mockNotation1.getName()).thenReturn("Duplicate Name");
            when(mockNotation1.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);

            mockNotation2 = mock(NotationBody.class);
            when(mockNotation2.getName()).thenReturn("Duplicate Name");
            when(mockNotation2.getNumberOfWorkingBells()).thenReturn(NumberOfBells.BELLS_6);

            composition = new ObservableComposition();
            composition.addNotation(mockNotation1);
        }
        catch (Exception e) {
            fail();
        }
        composition.addNotation(mockNotation2);
    }

    @Test
    public void notationCanBeExchanged() {
        ObservableComposition composition = new ObservableComposition();
        composition.setNumberOfBells(NumberOfBells.BELLS_6);

        composition.addNotation(METHOD_A_6_BELL);
        assertEquals(METHOD_A_6_BELL, Iterators.getOnlyElement(composition.get().getAllNotations().iterator()));

        composition.exchangeNotation(METHOD_A_6_BELL, METHOD_B_6_BELL);
        assertEquals(METHOD_B_6_BELL, Iterators.getOnlyElement(composition.get().getAllNotations().iterator()));
    }

    @Test
    public void addingFirstNotationToNonSplicedSetsDefaultNotation() {
        ObservableComposition composition = new ObservableComposition();
        composition.setSpliced(false);
        composition.addNotation(METHOD_A_8_BELL);

        assertEquals(Optional.of(METHOD_A_8_BELL), composition.get().getNonSplicedActiveNotation());
        assertFalse(composition.get().isSpliced());
    }

    @Test
    public void addingFirstNotationToSplicedSetsDefaultNotation() {
        ObservableComposition composition = new ObservableComposition();
        composition.setSpliced(true);
        composition.addNotation(METHOD_A_8_BELL);

        assertEquals(Optional.of(METHOD_A_8_BELL), composition.get().getNonSplicedActiveNotation());
        assertFalse(composition.get().isSpliced());
    }

    @Test
    public void removingOnlyNotationRemovesActiveNotation() throws CloneNotSupportedException {
        ObservableComposition composition = new ObservableComposition();
        composition.setNumberOfBells(NumberOfBells.BELLS_8);

        composition.addNotation(METHOD_A_8_BELL);

        composition.removeNotation(METHOD_A_8_BELL);
        assertEquals(Optional.empty() ,composition.get().getNonSplicedActiveNotation());
    }

    @Test
    public void removingActiveNotationChoosesNextNotationAlphabetically() {
        ObservableComposition composition = new ObservableComposition();
        composition.setSpliced(false);

        composition.addNotation(METHOD_A_6_BELL);
        composition.addNotation(METHOD_B_6_BELL);
        composition.addNotation(METHOD_C_6_BELL);
        composition.addNotation(METHOD_D_6_BELL);

        composition.setNonSplicedActiveNotation(METHOD_C_6_BELL);
        Assert.assertEquals(Optional.of(METHOD_C_6_BELL), composition.get().getNonSplicedActiveNotation());

        composition.removeNotation(METHOD_C_6_BELL);
        Assert.assertEquals(Optional.of(METHOD_D_6_BELL), composition.get().getNonSplicedActiveNotation());
        composition.removeNotation(METHOD_D_6_BELL);
        Assert.assertEquals(Optional.of(METHOD_B_6_BELL), composition.get().getNonSplicedActiveNotation());
        composition.removeNotation(METHOD_A_6_BELL);
        Assert.assertEquals(Optional.of(METHOD_B_6_BELL), composition.get().getNonSplicedActiveNotation());
        composition.removeNotation(METHOD_B_6_BELL);
        assertFalse(composition.get().getNonSplicedActiveNotation().isPresent());
    }

    @Test
    public void removingActiveNotationOnlyChoosesValidNotations() {
        ObservableComposition composition = new ObservableComposition();
        composition.setNumberOfBells(NumberOfBells.BELLS_6);
        composition.setSpliced(false);

        composition.addNotation(METHOD_A_6_BELL);
        composition.addNotation(METHOD_B_8_BELL);//Invalid number of bells
        composition.addNotation(METHOD_C_6_BELL);
        composition.addNotation(METHOD_D_8_BELL);//Invalid number of bells

        composition.setNonSplicedActiveNotation(METHOD_C_6_BELL);
        Assert.assertEquals(Optional.of(METHOD_C_6_BELL), composition.get().getNonSplicedActiveNotation());

        composition.removeNotation(METHOD_C_6_BELL);
        Assert.assertEquals(Optional.of(METHOD_A_6_BELL), composition.get().getNonSplicedActiveNotation());
        composition.removeNotation(METHOD_A_6_BELL);
        assertFalse(composition.get().getNonSplicedActiveNotation().isPresent());
    }

    @Test
    public void removingNotationSwitchesToLexicographicallyNextActiveNotationWithinClosestNumberOfBells() {
        ObservableComposition composition = new ObservableComposition();
        composition.setNumberOfBells(NumberOfBells.BELLS_8);

        composition.addNotation(METHOD_A_8_BELL);
        composition.addNotation(METHOD_B_8_BELL);
        composition.addNotation(METHOD_C_6_BELL);
        Assert.assertEquals(Optional.of(METHOD_A_8_BELL), composition.get().getNonSplicedActiveNotation());

        composition.setNonSplicedActiveNotation(METHOD_B_8_BELL);
        Assert.assertEquals(Optional.of(METHOD_B_8_BELL), composition.get().getNonSplicedActiveNotation());

        composition.removeNotation(METHOD_B_8_BELL);
        Assert.assertEquals(Optional.of(METHOD_A_8_BELL), composition.get().getNonSplicedActiveNotation());

        composition.removeNotation(METHOD_A_8_BELL);
        Assert.assertEquals(Optional.of(METHOD_C_6_BELL), composition.get().getNonSplicedActiveNotation());
    }

    @Test
    public void removingNotationSwitchesToClosestNumberOfBellsHighestFirst() {
        ObservableComposition composition = new ObservableComposition();

        composition.addNotation(METHOD_A_6_BELL);
        composition.addNotation(METHOD_A_7_BELL);
        composition.addNotation(METHOD_A_8_BELL);
        composition.setNumberOfBells(NumberOfBells.BELLS_8);

        Assert.assertEquals(Optional.of(METHOD_A_6_BELL), composition.get().getNonSplicedActiveNotation());

        composition.setNonSplicedActiveNotation(METHOD_A_7_BELL);
        Assert.assertEquals(Optional.of(METHOD_A_7_BELL), composition.get().getNonSplicedActiveNotation());

        composition.removeNotation(METHOD_A_7_BELL);
        Assert.assertEquals(Optional.of(METHOD_A_8_BELL), composition.get().getNonSplicedActiveNotation());

        composition.removeNotation(METHOD_A_8_BELL);
        Assert.assertEquals(Optional.of(METHOD_A_6_BELL), composition.get().getNonSplicedActiveNotation());
    }

    @Test
    public void addingNotationsWithDifferentNumberOfBellsFiltersInappropriateNotations() {
        ObservableComposition composition = new ObservableComposition();

        composition.setSpliced(false);
        composition.addNotation(METHOD_A_6_BELL);
        composition.addNotation(METHOD_B_6_BELL);
        composition.addNotation(METHOD_A_8_BELL);

        assertEquals(3, composition.get().getAllNotations().size());
        assertEquals(1, composition.get().getAvailableNotations().size());
        assertEquals(2, composition.get().getValidNotations().size());

        composition.setSpliced(true);
        assertEquals(3, composition.get().getAllNotations().size());
        assertEquals(2, composition.get().getAvailableNotations().size());
        assertEquals(2, composition.get().getValidNotations().size());
    }

    @Test
    public void settingActiveNotationUnsetsSpliced() {
        ObservableComposition composition = new ObservableComposition();

        composition.addNotation(METHOD_A_6_BELL);
        composition.addNotation(METHOD_B_6_BELL);

        composition.setSpliced(true);
        assertEquals(true, composition.get().isSpliced());

        composition.setNonSplicedActiveNotation(METHOD_A_6_BELL);
        assertEquals(false, composition.get().isSpliced());
    }

}