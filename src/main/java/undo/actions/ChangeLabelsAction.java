package undo.actions;

import backend.resource.TurboIssue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ChangeLabelsAction implements Action<TurboIssue> {

    public static final String DESCRIPTION = "change label(s)";

    private final Set<String> addedLabels;
    private final Set<String> removedLabels;

    // Represents changes to the labels of a TurboIssue
    public ChangeLabelsAction(List<String> addedLabels, List<String> removedLabels) {
        this.addedLabels = new HashSet<>(addedLabels);
        this.removedLabels = new HashSet<>(removedLabels);
    }

    @Override
    public ActionType getType() {
        return ActionType.CHANGELABELS;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    // Adds the labels to be added and removes the labels to be removed
    @Override
    public TurboIssue act(TurboIssue issue) {
        Set<String> newLabels = new TreeSet<>(issue.getLabels());
        newLabels.addAll(addedLabels);
        newLabels.removeAll(removedLabels);
        issue.setLabels(newLabels.stream().collect(Collectors.toList()));
        return issue;
    }

    // Adds the labels to be removed and removes the labels to be added
    @Override
    public TurboIssue undo(TurboIssue issue) {
        Set<String> newLabels = new TreeSet<>(issue.getLabels());
        newLabels.addAll(removedLabels);
        newLabels.removeAll(addedLabels);
        issue.setLabels(newLabels.stream().collect(Collectors.toList()));
        return issue;
    }

    // Compares original labels of the TurboIssue with the list of new labels to get the lists of the
    // added and removed labels
    public static ChangeLabelsAction createChangeLabelsAction(TurboIssue issue, List<String> newLabels) {
        return new ChangeLabelsAction(
                newLabels.stream()
                        .filter(newLabel -> !issue.getLabels().contains(newLabel))
                        .collect(Collectors.toList()),
                issue.getLabels().stream()
                        .filter(originalLabel -> !newLabels.contains(originalLabel))
                        .collect(Collectors.toList()));
    }

}
