package ui;

import java.lang.ref.WeakReference;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ParentIssuesDisplayBox extends HBox {
	
	private ObservableList<Integer> issueNumbers = null;
	private WeakListChangeListener<Integer> listChangeListener;
	private boolean displayWhenEmpty;
	
	public ParentIssuesDisplayBox(ObservableList<Integer> items, boolean displayWhenEmpty) {
		setListableItems(items);
		this.displayWhenEmpty = displayWhenEmpty;
		setup();
	}
	
	private void setup() {
		if (displayWhenEmpty) {
			setStyle(UI.STYLE_BORDERS_FADED);
		}
		update();
	}

	private void setListableItems(ObservableList<Integer> issueNumbers) {
		this.issueNumbers = issueNumbers;
		initialiseChangeListener();
		issueNumbers.addListener(listChangeListener);
		
		update();
	}
	
	private void initialiseChangeListener(){
		if(this.issueNumbers != null){
			WeakReference<ParentIssuesDisplayBox> that = new WeakReference<ParentIssuesDisplayBox>(this);
			ListChangeListener<Integer> listener = new ListChangeListener<Integer>() {
				@Override
				public void onChanged(ListChangeListener.Change<? extends Integer> arg0) {
					that.get().update();
				}
			};
			listChangeListener = new WeakListChangeListener<Integer>(listener);
		}
	}
	
	private void update() {
		getChildren().clear();

		Label label;
		if (displayWhenEmpty && issueNumbers.size() == 0) {
			label = new Label("Parents");
			label.setStyle(UI.STYLE_FADED + "-fx-padding: 5;");
			getChildren().add(label);
		} else {
			StringBuilder parentSB = new StringBuilder();
			for (Integer p : issueNumbers) {
				parentSB.append("#" + p);
				parentSB.append(", ");
			}
			if (parentSB.length() != 0) parentSB.delete(parentSB.length()-2, parentSB.length());

			if (displayWhenEmpty || (!displayWhenEmpty && !parentSB.toString().isEmpty())) {
				label = new Label(parentSB.toString());
				label.setStyle("-fx-padding: 5;");
				getChildren().add(label);
			}
		}
	}
	
}
