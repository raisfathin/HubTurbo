package tests;

import backend.resource.TurboMilestone;
import org.eclipse.egit.github.core.Milestone;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class TurboMilestoneTest {

    @Test
    public void turboMilestoneTest() {
        Milestone milestone = new Milestone();
        milestone.setNumber(1);
        milestone.setState("open");
        TurboMilestone turboMilestone = new TurboMilestone("dummy/dummy", milestone);
        assertEquals(1, turboMilestone.getId());
        assertEquals("dummy/dummy", turboMilestone.getRepoId());
        turboMilestone.setDueDate(Optional.<LocalDate>empty());
        assertEquals(Optional.empty(), turboMilestone.getDueDate());
        turboMilestone.setDescription("test description");
        assertEquals("test description", turboMilestone.getDescription());
        turboMilestone.setOpen(false);
        assertEquals(false, turboMilestone.isOpen());
        turboMilestone.setOpenIssues(0);
        assertEquals(0, turboMilestone.getOpenIssues());
        turboMilestone.setClosedIssues(0);
        assertEquals(0, turboMilestone.getClosedIssues());
    }

    @Test
    public void turboMilestoneSortingTest() {
        // test for turbo milestone due date sorting behaviour
        TurboMilestone milestone1 = new TurboMilestone("1", 1, "milestone1");
        milestone1.setDueDate(Optional.of(LocalDate.now().minusDays(1)));
        TurboMilestone milestone2 = new TurboMilestone("2", 2, "milestone2");
        milestone2.setDueDate(Optional.of(LocalDate.now().minusDays(2)));
        TurboMilestone milestone3 = new TurboMilestone("3", 3, "milestone3");
        milestone3.setDueDate(Optional.of(LocalDate.now().minusDays(1)));
        TurboMilestone milestone4 = new TurboMilestone("3", 4, "milestone4");
        TurboMilestone milestone5 = new TurboMilestone("3", 5, "milestone5");
        List<TurboMilestone> milestones = Arrays.asList(milestone1,
                milestone2,
                milestone3,
                milestone4,
                milestone5);
        List<TurboMilestone> sortedMilestones = TurboMilestone.sortByDueDate(milestones);
        // the sort should be stable
        assertEquals("milestone2", sortedMilestones.get(0).getTitle());
        assertEquals("milestone1", sortedMilestones.get(1).getTitle());
        assertEquals("milestone3", sortedMilestones.get(2).getTitle());
        assertEquals("milestone4", sortedMilestones.get(3).getTitle());
        assertEquals("milestone5", sortedMilestones.get(4).getTitle());
    }

}
