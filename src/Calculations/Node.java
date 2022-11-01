package Calculations;

import java.util.ArrayList;

public class Node implements Comparable<Node> {

    private double x;
    private double output;
    private ArrayList<Connection> connections = new ArrayList<>();

    // Metodos
    // Realiza el calculo de las conecciones para tomar una decicicion
    public void calculate(){
        double s = 0;
        for (Connection c:connections){
            if (c.isEnabled()){
                s += c.getWeight() * c.getFrom().getOutput();
            }
        }

        output = activation_function(s);
    }

    // Funcion Sigmoid
    private double activation_function(double x){
        return 1d / (1 + Math.exp(-x));
    }


    // Constructor
    public Node(double x) {
        this.x = x;
    }

    // Getters and Setters
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<Connection> connections) {
        this.connections = connections;
    }

    @Override
    public int compareTo(Node o) {
        if (this.x > o.x) return -1;
        if (this.x < o.x) return 1;
        return 0;
    }
}
