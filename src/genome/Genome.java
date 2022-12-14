package genome;

import Calculations.Calculator;
import Hydrozoa.Genoma;
import data_structure.RandomHashSet;
import neat.Neat;

public class Genome {

    private RandomHashSet<ConnectionGene> connections = new RandomHashSet<>();
    private RandomHashSet<NodeGene> nodes = new RandomHashSet<>();

    private Neat neat;
    private Calculator calculator;

    // Constructor
    public Genome(Neat neat){
        this.neat = neat;
    }

    // Getters
    public RandomHashSet<ConnectionGene> getConnections() {
        return connections;
    }

    public RandomHashSet<NodeGene> getNodes() {
        return nodes;
    }

    public Neat getNeat() {
        return neat;
    }

    // Metodos
    public double distance(Genome g2){

        Genome g1 = this;

        int highest_innovation_gene1 = 0;
        if (g1.getConnections().size() != 0){
            highest_innovation_gene1 = g1.getConnections().get(g1.getConnections().size()-1).getInnovation_number();
        }

        int highest_innovation_gene2 = 0;
        if (g2.getConnections().size() != 0){
            highest_innovation_gene2 = g2.getConnections().get(g2.getConnections().size()-1).getInnovation_number();
        }

        if (highest_innovation_gene1 < highest_innovation_gene2){
            Genome g = g1;
            g1 = g2;
            g2 = g;
        }

        int index_g1 = 0;
        int index_g2 = 0;

        int disjoint = 0;
        int excess = 0;
        int similar = 0;
        double weight_diff = 0;

        while (index_g1 < g1.getConnections().size() && index_g2 < g2.getConnections().size()){

            ConnectionGene gene1 = g1.getConnections().get(index_g1);
            ConnectionGene gene2 = g2.getConnections().get(index_g2);

            int in1 = gene1.getInnovation_number();
            int in2 = gene2.getInnovation_number();

            if (in1 == in2){
                // Similar gene
                similar++;
                weight_diff += Math.abs(gene1.getWeight() - gene2.getWeight());
                index_g1++;
                index_g2++;
            } else if (in1 > in2) {
                // disjoint gene of b
                disjoint++;
                index_g2++;
            } else {
                // disjoint gene of a
                disjoint++;
                index_g1++;
            }
        }

        weight_diff /= Math.max(1,similar);
        excess = g1.getConnections().size() - index_g1;

        double N = Math.max(g1.getConnections().size(), g2.getConnections().size());
        if (N < 20){
            N = 1;
        }

        return neat.getC1() * disjoint / N + neat.getC2() * excess / N + neat.getC3() * weight_diff / N;
    }

    public static Genome crossOver(Genome g1, Genome g2){

        Neat neat = g1.getNeat();

        Genome genome = neat.empty_genome();

        int index_g1 = 0;
        int index_g2 = 0;

        while (index_g1 < g1.getConnections().size() && index_g2 < g2.getConnections().size()){

            ConnectionGene gene1 = g1.getConnections().get(index_g1);
            ConnectionGene gene2 = g2.getConnections().get(index_g2);

            int in1 = gene1.getInnovation_number();
            int in2 = gene2.getInnovation_number();

            if (in1 == in2){
                // Similar gene
                if (Math.random() > 0.5){
                    genome.getConnections().add(neat.getConnection(gene1));
                } else {
                    genome.getConnections().add(neat.getConnection(gene2));
                }

                index_g1++;
                index_g2++;
            } else if (in1 > in2) {
                // disjoint gene of b
                //genome.getConnections().add(neat.getConnection(gene2));
                index_g2++;
            } else {
                // disjoint gene of a
                genome.getConnections().add(neat.getConnection(gene1));
                index_g1++;
            }
        }

        while (index_g1 < g1.getConnections().size()){
            ConnectionGene gene1 = g1.getConnections().get(index_g1);
            genome.getConnections().add(neat.getConnection(gene1));
            index_g1++;
        }

        for (ConnectionGene c: genome.getConnections().getData()){
            genome.getNodes().add(c.getFrom());
            genome.getNodes().add(c.getTo());
        }

        return genome;
    }

    public void generate_calculator(){
        this.calculator = new Calculator(this);
    }

    public double[] calculate(double... ar){
        if (calculator != null){
            return calculator.calculate(ar);
        }
        return null;
    }

    // Metodos de mutaciones
    public void mutate(){
        if (neat.getPROBABILITY_MUTATE_LINK() > Math.random()){
            mutate_link();
        }
        if (neat.getPROBABILITY_MUTATE_NODE() > Math.random()){
            mutate_node();
        }
        if (neat.getPROBABILITY_MUTATE_WEIGHT_SHIFT() > Math.random()){
            mutate_weight_shift();
        }
        if (neat.getPROBABILITY_MUTATE_WEIGHT_RANDOM() > Math.random()){
            mutate_weight_random();
        }
        if (neat.getPROBABILITY_MUTATE_TOGGLE_LINK() > Math.random()){
            mutate_link_toggle();
        }
    }

    public void mutate_link(){

        for (int i = 0; i < 100; i++){

            NodeGene a = nodes.random_element();
            NodeGene b = nodes.random_element();

            if (a.getX() == b.getX()){ // Si son iguales significan que estan en la misma ubicacion input-input output-output
                continue;
            }

            ConnectionGene con;
            if (a.getX() < b.getX()){
                con = new ConnectionGene(a,b);
            } else {
                con = new ConnectionGene(b,a);
            }

            if (connections.contains(con)){ // Si existe la conexion no hace nada
                continue;
            }

            con = neat.getConnection(con.getFrom(), con.getTo());
            con.setWeight((Math.random() * 2 - 1) * neat.getWEIGHT_RANDOM_STRENGTH());

            connections.add_sorted(con);

            return;
        }
    }

    // Agrega un nuevo nodo en medio de una coneccion.
    public void mutate_node(){
        ConnectionGene con = connections.random_element();
        if (con == null) return;

        NodeGene from = con.getFrom();
        NodeGene to = con.getTo();

        int replaceIndex = neat.getReplaceIndex(from,to);
        //replaceIndex = 0;
        NodeGene middle;
        if (replaceIndex == 0){
            middle = neat.getNode();
            middle.setX((from.getX() + to.getX()) / 2); // toma la mitad de la posicion de los 2 nodos en X
            middle.setY((from.getY() + to.getY()) / 2 + Math.random() * 0.1 - 0.05); // Posicion Y
            neat.setReplaceIndex(from,to,middle.getInnovation_number());
        } else {
            middle = neat.getNode(replaceIndex);
        }

        ConnectionGene con1 = neat.getConnection(from, middle);
        ConnectionGene con2 = neat.getConnection(middle, to);

        con1.setWeight(1);
        con2.setWeight(con.getWeight());
        con2.setEnabled(con.isEnabled());

        connections.remove(con);
        connections.add(con1);
        connections.add(con2);

        nodes.add(middle);
    }

    public void mutate_weight_shift(){
        ConnectionGene con = connections.random_element();
        if (con != null){
            con.setWeight((con.getWeight() + (Math.random() * 2 - 1) * neat.getWEIGHT_SHIFT_STRENGTH()));
        }
    }

    public void mutate_weight_random(){
        ConnectionGene con = connections.random_element();
        if (con != null){
            con.setWeight((Math.random() * 2 - 1) * neat.getWEIGHT_RANDOM_STRENGTH());
        }
    }

    // Habilita o desabilita una mutaccion
    public void mutate_link_toggle(){
        ConnectionGene con = connections.random_element();
        if (con != null){
            con.setEnabled(!con.isEnabled()); // Pone lo contrario del resultado
        }
    }
}

// borrar calculadora y generar calculadora