package Hydrozoa;

public class NodeGene {

    enum TYPE {
        INPUT,
        HIDDEN,
        OUTPUT,
        ;
    }

    private TYPE type;
    private int id;

    // Constructor
    public NodeGene(TYPE type, int id) {
        this.type = type;
        this.id = id;
    }

    // Getters
    public TYPE getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public NodeGene copy(){
        return new NodeGene(type, id);
    }
}
