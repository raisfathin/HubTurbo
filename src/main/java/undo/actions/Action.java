package undo.actions;

public interface Action<T> {

    @SuppressWarnings("SpellCheckingInspection")
    enum ActionType { CHANGELABELS }

    ActionType getType();

    String getDescription();

    T act(T t);

    T undo(T t);

    // Returns an inverse of the Action.
    @SuppressWarnings("unchecked")
    default Action invert() {
        Action that = this;
        return new Action<T>() {
            @Override
            public ActionType getType() {
                return that.getType();
            }

            @Override
            public String getDescription() {
                return that.getDescription();
            }

            @Override
            public T act(T t) {
                return (T) that.undo(t);
            }

            @Override
            public T undo(T t) {
                return (T) that.act(t);
            }
        };
    }

}
