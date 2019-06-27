package org.ringingmaster.engine.parser.cell.grouping;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.SPLICE;
import static org.ringingmaster.engine.parser.cell.grouping.GroupingFactory.buildSection;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class DefaultGroupTest {

    private static final String TEST_MESSAGE = "TEST MESSAGE";
    private static final Section SECTION = buildSection(0, 1, CALL);
    private static final HashSet<Section> SECTIONS = Sets.newHashSet(SECTION);

    @Test
    public void getValuesAsExpected() {
        Group group1 = new DefaultGroup(0, 1, true, ImmutableList.of(TEST_MESSAGE), SECTIONS);
        assertEquals(0, group1.getStartIndex());
        assertEquals(1, group1.getLength());
        assertEquals(1, group1.getEndIndex());
        assertTrue(group1.isValid());
        assertEquals(1, group1.getSections().size());
        assertEquals(SECTION, group1.getSections().get(0));
        assertEquals("[" +TEST_MESSAGE + "]", group1.getMessages().toString());

        Section section2 = buildSection(10, 5, CALL);
        HashSet<Section> sections2 = Sets.newHashSet(section2);
        Group group2 = new DefaultGroup(10, 5, false, ImmutableList.of(TEST_MESSAGE), sections2);
        assertEquals(10, group2.getStartIndex());
        assertEquals(5,  group2.getLength());
        assertEquals(15, group2.getEndIndex());
        assertFalse(group2.isValid());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotSetNegativeStartIndex() {
        new DefaultGroup(-1, 2, true, ImmutableList.of(TEST_MESSAGE), SECTIONS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotSetNegativeLength() {
        new DefaultGroup(1, -2, true, ImmutableList.of(TEST_MESSAGE), SECTIONS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotSetZeroLength() {
        new DefaultGroup(1, 0, true, ImmutableList.of(TEST_MESSAGE), SECTIONS);
    }

    @Test
    public void createStartIndex0OK() {
        new DefaultGroup(0, 1, true, ImmutableList.of(TEST_MESSAGE), SECTIONS);
    }

    @Test
    public void fallsWithinBoundsCheck() {
        Section section2 = buildSection(3, 1, CALL);
        HashSet<Section> sections2 = Sets.newHashSet(section2);

        Group group = new DefaultGroup(3, 1, true, ImmutableList.of(TEST_MESSAGE), sections2);
        assertEquals(false, group.fallsWithin(2));
        assertEquals(true,  group.fallsWithin(3));
        assertEquals(false, group.fallsWithin(4));
    }

    @Test
    public void intergroupCheck() {
        Section section2 = buildSection(3, 3, CALL);
        HashSet<Section> sections2 = Sets.newHashSet(section2);
        Group group = new DefaultGroup(3, 3, true, ImmutableList.of(TEST_MESSAGE), sections2);

        assertEquals(true,  group.intersection(1,10));

        assertEquals(false, group.intersection(0, 2));
        assertEquals(false, group.intersection(0, 3));
        assertEquals(true,  group.intersection(0, 4));
        assertEquals(true,  group.intersection(0, 6));
        assertEquals(true,  group.intersection(0, 7));

        assertEquals(true,  group.intersection(3, 1));
        assertEquals(true,  group.intersection(3, 2));
        assertEquals(true,  group.intersection(3, 3));

        assertEquals(true,  group.intersection(5, 1));
        assertEquals(true,  group.intersection(5, 3));
        assertEquals(false, group.intersection(6, 3));
    }

    @Test
    public void canBuildGroupWithCompoundSections() {
        final HashSet<Section> sections = Sets.newHashSet(buildSection(0, 1, CALL), buildSection(1, 1, CALL));
        new DefaultGroup(0, 2, true, ImmutableList.of(TEST_MESSAGE), sections);
    }

    @Test (expected = IllegalArgumentException.class)
    public void cannotBuildGroupWithNoSections() {
        new DefaultGroup(1, 1, true, ImmutableList.of(TEST_MESSAGE), Sets.newHashSet());
    }

    @Test (expected = IllegalArgumentException.class)
    public void buildingSectionsThatLeaveEndGapInGroupThrows() {
        final HashSet<Section> sections = Sets.newHashSet(buildSection(0, 1, CALL));
        new DefaultGroup(0, 2, true, ImmutableList.of(TEST_MESSAGE), sections);
    }

    @Test (expected = IllegalArgumentException.class)
    public void buildingSectionsThatLeaveStartGapInGroupThrows() {
        final HashSet<Section> sections = Sets.newHashSet(buildSection(1, 1, CALL));
        new DefaultGroup(0, 2, true, ImmutableList.of(TEST_MESSAGE), sections);
    }

    @Test (expected = IllegalArgumentException.class)
    public void buildingSectionsThatLeaveMidGapInGroupThrows() {
        final HashSet<Section> sections = Sets.newHashSet(buildSection(2, 1, CALL), buildSection(0, 1, CALL));
        new DefaultGroup(0, 3, true, ImmutableList.of(TEST_MESSAGE), sections);
    }

    @Test (expected = IllegalArgumentException.class)
    public void buildingSectionsThatOverlapsEndOfGroupThrows() {
        final HashSet<Section> sections = Sets.newHashSet(buildSection(0, 1, CALL), buildSection(1, 2, CALL));
        new DefaultGroup(0, 2, true, ImmutableList.of(TEST_MESSAGE), sections);
    }

    @Test (expected = IllegalArgumentException.class)
    public void buildingSectionsThatOverlapsStartOfGroupThrows() {
        final HashSet<Section> sections = Sets.newHashSet(buildSection(0, 3, CALL), buildSection(3, 4, CALL));
        new DefaultGroup(1, 4, true, ImmutableList.of(TEST_MESSAGE), sections);
    }

    @Test (expected = IllegalArgumentException.class)
    public void buildingSectionsThatIsOutsideGroupThrows() {
        final HashSet<Section> sections = Sets.newHashSet(buildSection(0, 1, CALL), buildSection(1, 1, CALL));
        new DefaultGroup(0, 1, true, ImmutableList.of(TEST_MESSAGE), sections);
    }

    @Test (expected = IllegalArgumentException.class)
    public void buildingSectionsThatIsEncompasesGroupThrows() {
        final HashSet<Section> sections = Sets.newHashSet(buildSection(0, 3, CALL));
        new DefaultGroup(1, 1, true, ImmutableList.of(TEST_MESSAGE), sections);
    }

    @Test
    public void canGetFirstSectionTypeInCorrectOrder() {
        final HashSet<Section> sections = Sets.newHashSet(buildSection(1, 1, SPLICE),
                buildSection(0, 1, CALL));
        Group group = new DefaultGroup(0, 2, true, ImmutableList.of(TEST_MESSAGE), sections);
        assertEquals(CALL, group.getFirstSectionParseType());
    }

    @Test
    public void canSafelyCallToString() {
        Group group = new DefaultGroup(0, 1, true, ImmutableList.of(TEST_MESSAGE), SECTIONS);
        group.toString();
    }

}
