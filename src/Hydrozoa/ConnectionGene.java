package Hydrozoa;

public class ConnectionGene {

    private int nodoEntrada;
    private int nodoSalida;
    private float peso;
    private boolean expressed;
    private int inovacion;

    public ConnectionGene(int nodoEntrada, int nodoSalida, float peso, boolean expressed, int inovacion) {
        this.nodoEntrada = nodoEntrada;
        this.nodoSalida = nodoSalida;
        this.peso = peso;
        this.expressed = expressed;
        this.inovacion = inovacion;
    }

    public int getNodoEntrada() {
        return nodoEntrada;
    }

    public int getNodoSalida() {
        return nodoSalida;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public boolean isExpressed() {
        return expressed;
    }

    public int getInovacion() {
        return inovacion;
    }

    public void disable(){
        expressed = false;
    }

    public ConnectionGene copy(){
        return new ConnectionGene(nodoEntrada,nodoSalida,peso,expressed,inovacion);
    }
}
