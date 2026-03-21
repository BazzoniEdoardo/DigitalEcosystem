package entities.population.logic.activity;

public final class ActionScore {

    private Action action;
    private double score;

    public ActionScore(final Action action, final double score) {
        this.action = action;
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public void setScore(final double score) {
        this.score = score;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(final Action action) {
        this.action = action;
    }
}
