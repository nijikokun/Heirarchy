package heir.arch;

public class Node {
    private String name;
    private boolean negated;

    public Node(String name, boolean negated) {
        this.name = name;
        this.negated = negated;
    }

    public String getName() {
        return name;
    }

    public boolean isNegated() {
        return negated;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }
}
