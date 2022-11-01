package genome;

public class NodeGene extends Gene {

    private double x,y; // Para dibujar

    // Constructor
    public NodeGene(int innovation_number){
        super(innovation_number);
    }

    // Getters and setters
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    // Metodos
    public boolean equals(Object o){
        if (!(o instanceof NodeGene)) return false;
        return innovation_number == ((NodeGene) o).getInnovation_number(); // Casting a NodeGene
    }

    public int hashCode(){
        return innovation_number;
    }
}
