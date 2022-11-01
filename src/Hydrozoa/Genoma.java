package Hydrozoa;//import java.util.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Genoma {

    private static List<Integer> tmpList1 = new ArrayList<Integer>();
    private static List<Integer> tmpList2 = new ArrayList<Integer>();

    private final float PROBABILIY_PERTURBING = 0.9f; // probabilidad de que se asigne un nuevo peso

    private Map<Integer, ConnectionGene> conexiones;
    private Map<Integer, NodeGene> nodes;

    public Genoma(){
        nodes = new HashMap<Integer, NodeGene>();
        conexiones = new HashMap<Integer, ConnectionGene>();
    }

    public void addNodeGene(NodeGene gene){
        nodes.put(gene.getId(), gene);
    }

    public void addConecctionGene(ConnectionGene gene){
        conexiones.put(gene.getInovacion(), gene);
    }

    public Map<Integer,ConnectionGene> getConexiones(){
        return conexiones;
    }

    public Map<Integer,NodeGene> getNodesGenes(){
        return nodes;
    }

    public void mutation(Random r){
        for (ConnectionGene con : conexiones.values()){
            if (r.nextFloat() < PROBABILIY_PERTURBING){         // uniformly perturbing weights
                con.setPeso(con.getPeso() * (r.nextFloat()*4f-2f));
            } else {                                            // assigning new weight
                con.setPeso(r.nextFloat()*4f-2f);
            }
        }
    }

    public void addConnectionMutation(Random r){
        NodeGene node1 = nodes.get(r.nextInt(nodes.size()));
        NodeGene node2 = nodes.get(r.nextInt(nodes.size()));
        float peso = r.nextFloat()*2f-1f;

        boolean reversed = false;

        // Comprueba si esta invertido la coneccion
        if (node1.getType() == NodeGene.TYPE.HIDDEN && node2.getType() == NodeGene.TYPE.INPUT) {
            reversed = true;
        } else if (node1.getType() == NodeGene.TYPE.OUTPUT && node2.getType() == NodeGene.TYPE.HIDDEN){
            reversed = true;
        } else if (node1.getType() == NodeGene.TYPE.OUTPUT && node2.getType() == NodeGene.TYPE.INPUT){
            reversed = true;
        }

        boolean connectionExists = false;
        for (ConnectionGene conexion: conexiones.values()){
            if (conexion.getNodoEntrada() == node1.getId() && conexion.getNodoSalida() == node2.getId()){ // Existe la conexion
                connectionExists = true;
                break;
            } else if (conexion.getNodoEntrada() == node2.getId() && conexion.getNodoSalida() == node1.getId()){ // Existe la conexion
                connectionExists = true;
                break;
            }
        }

        if (connectionExists){
            return;
        }

        ConnectionGene newCon = new ConnectionGene(reversed ? node2.getId() : node1.getId(), reversed ? node1.getId() : node2.getId(), peso, true, 0);
        conexiones.put(newCon.getInovacion(), newCon);
    }

    public void addNodeMutation(Random r, Contador innovation) {
        ConnectionGene con = conexiones.get(r.nextInt(conexiones.size()));

        NodeGene inNode = nodes.get(con.getNodoEntrada());
        NodeGene outNode = nodes.get(con.getNodoSalida());

        con.disable();

        NodeGene newNode = new NodeGene(NodeGene.TYPE.HIDDEN, nodes.size());
        ConnectionGene inToNew = new ConnectionGene(inNode.getId(), newNode.getId(), 1f, true, innovation.getInovation());
        ConnectionGene newToOut = new ConnectionGene(newNode.getId(), outNode.getId(), con.getPeso(), true, innovation.getInovation());

        nodes.put(newNode.getId(), newNode);
        conexiones.put(inToNew.getInovacion(), inToNew);
        conexiones.put(newToOut.getInovacion(), newToOut);
    }

    /*
        padre1  Padre mas apto
        padre2  Padre menos apto
     */

    public static Genoma cruzar(Genoma padre1, Genoma padre2, Random r){
        Genoma child = new Genoma();

        for (NodeGene parent1Node: padre1.getNodesGenes().values()){
            child.addNodeGene(parent1Node.copy());
        }

        for (ConnectionGene parent1Node: padre1.getConexiones().values()){
            if (padre2.getConexiones().containsKey(parent1Node.getInovacion())){ // Gen coincidente
                ConnectionGene childConGene = r.nextBoolean() ? parent1Node.copy() : padre2.getConexiones().get(parent1Node.getInovacion()).copy();
                child.addConecctionGene(childConGene);
            } else { // Genes disjuntos o en exceso
                ConnectionGene childConGen = parent1Node.copy();
                child.addConecctionGene(childConGen);
            }
        }

        return child;
    }

    public static float compatibilityDistance(Genoma genoma1, Genoma genoma2, int c1, int c2, int c3){
        int excessGene = countExcessGenes(genoma1,genoma2);
        int disjointGenes = countDisjoinGenes(genoma1,genoma2);
        float avgWeightDiff = averageWeightDiff(genoma1,genoma2);

        return excessGene * c1 + disjointGenes * c2 + avgWeightDiff * c3;
    }

    // Cuenta genes coincidentes
    public static int countMatchingGenes(Genoma genoma1, Genoma genoma2){
        int matchingGenes = 0;

        List<Integer> nodokeys1 = asSortedList(genoma1.getNodesGenes().keySet(),tmpList1);
        List<Integer> nodokeys2 = asSortedList(genoma2.getNodesGenes().keySet(),tmpList2);

        int highestInnovation1 = nodokeys1.get(nodokeys1.size()-1);
        int highestInnovation2 = nodokeys2.get(nodokeys2.size()-1);
        int indices = Math.max(highestInnovation1,highestInnovation2);

        for (int i = 0; i <= indices; i++){
            NodeGene node1 = genoma1.getNodesGenes().get(i);
            NodeGene node2 = genoma2.getNodesGenes().get(i);

            if (node1 != null && node2 != null){
                matchingGenes++;
            }
        }

        List<Integer> conKey1 = asSortedList(genoma1.getConexiones().keySet(),tmpList1);
        List<Integer> conKey2 = asSortedList(genoma2.getConexiones().keySet(),tmpList2);

        highestInnovation1 = conKey1.get(conKey1.size()-1);
        highestInnovation2 = conKey2.get(conKey2.size()-1);

        indices = Math.max(highestInnovation1,highestInnovation2);

        for (int i = 0; i <= indices; i++){
            ConnectionGene connection1 = genoma1.getConexiones().get(i);
            ConnectionGene connection2 = genoma2.getConexiones().get(i);
            if (connection1 != null && connection2 != null){
                matchingGenes++;
            }
        }

        return matchingGenes;
    }

    // Cuenta la cantidad de genes disjuntos
    public static int countDisjoinGenes(Genoma genoma1, Genoma genoma2){
        int disjointGenes = 0;

        List<Integer> nodokeys1 = asSortedList(genoma1.getNodesGenes().keySet(),tmpList1);
        List<Integer> nodokeys2 = asSortedList(genoma2.getNodesGenes().keySet(),tmpList2);

        int highestInnovation1 = nodokeys1.get(nodokeys1.size()-1);
        int highestInnovation2 = nodokeys2.get(nodokeys2.size()-1);
        int indices = Math.max(highestInnovation1,highestInnovation2);

        for (int i = 0; i <= indices; i++){
            NodeGene node1 = genoma1.getNodesGenes().get(i);
            NodeGene node2 = genoma2.getNodesGenes().get(i);

            if (node1 == null && highestInnovation1 > i && node2 != null){
                disjointGenes++;
            } else if (node2 == null && highestInnovation2 > i && node1 != null){
                disjointGenes++;
            }
        }

        List<Integer> conKey1 = asSortedList(genoma1.getConexiones().keySet(),tmpList1);
        List<Integer> conKey2 = asSortedList(genoma2.getConexiones().keySet(),tmpList2);

        highestInnovation1 = conKey1.get(conKey1.size()-1);
        highestInnovation2 = conKey2.get(conKey2.size()-1);

        indices = Math.max(highestInnovation1,highestInnovation2);

        for (int i = 0; i <= indices; i++){
            ConnectionGene connection1 = genoma1.getConexiones().get(i);
            ConnectionGene connection2 = genoma2.getConexiones().get(i);
            if (connection1 == null && highestInnovation1 > i && connection2 != null){
                disjointGenes++;
            } else if (connection2 == null && highestInnovation2 > i && connection1 != null){
                disjointGenes++;
            }
        }

        return disjointGenes;
    }

    public static int countExcessGenes(Genoma genoma1, Genoma genoma2){
        int excessGenes = 0;

        List<Integer> nodokeys1 = asSortedList(genoma1.getNodesGenes().keySet(),tmpList1);
        List<Integer> nodokeys2 = asSortedList(genoma2.getNodesGenes().keySet(),tmpList2);

        int highestInnovation1 = nodokeys1.get(nodokeys1.size()-1);
        int highestInnovation2 = nodokeys2.get(nodokeys2.size()-1);
        int indices = Math.max(highestInnovation1,highestInnovation2);

        for (int i = 0; i <= indices; i++){
            NodeGene node1 = genoma1.getNodesGenes().get(i);
            NodeGene node2 = genoma2.getNodesGenes().get(i);

            if (node1 == null && highestInnovation1 < i && node2 != null){
                excessGenes++;
            } else if (node2 == null && highestInnovation2 < i && node1 != null){
                excessGenes++;
            }
        }

        List<Integer> conKey1 = asSortedList(genoma1.getConexiones().keySet(),tmpList1);
        List<Integer> conKey2 = asSortedList(genoma2.getConexiones().keySet(),tmpList2);

        highestInnovation1 = conKey1.get(conKey1.size()-1);
        highestInnovation2 = conKey2.get(conKey2.size()-1);

        indices = Math.max(highestInnovation1,highestInnovation2);

        for (int i = 0; i <= indices; i++){
            ConnectionGene connection1 = genoma1.getConexiones().get(i);
            ConnectionGene connection2 = genoma2.getConexiones().get(i);
            if (connection1 == null && highestInnovation1 < i && connection2 != null){
                excessGenes++;
            } else if (connection2 == null && highestInnovation2 < i && connection1 != null){
                excessGenes++;
            }
        }

        return excessGenes;
    }

    public static float averageWeightDiff(Genoma genoma1, Genoma genoma2){
        int matchingGenes = 0;
        int weightDifference = 0;

        List<Integer> conKey1 = asSortedList(genoma1.getConexiones().keySet(),tmpList1);
        List<Integer> conKey2 = asSortedList(genoma2.getConexiones().keySet(),tmpList2);

        int highestInnovation1 = conKey1.get(conKey1.size()-1);
        int highestInnovation2 = conKey2.get(conKey2.size()-1);

        int indices = Math.max(highestInnovation1,highestInnovation2);
        for (int i = 0; i <= indices; i++){
            ConnectionGene connection1 = genoma1.getConexiones().get(i);
            ConnectionGene connection2 = genoma2.getConexiones().get(i);
            if (connection1 != null && connection2 != null){
                matchingGenes++;
                weightDifference += Math.abs(connection1.getPeso() - connection2.getPeso());
            }
        }

        return weightDifference/matchingGenes;
    }

    // Convierte una coleccion de mapa en una coleccion de listas.
    private static List<Integer> asSortedList(Collection<Integer> c, List<Integer> list){
        list.clear();
        list.addAll(c);
        java.util.Collections.sort(list);
        return list;
    }
}
