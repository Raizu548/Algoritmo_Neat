package Hydrozoa;

public class Contador {

    private int currentInnovation = 0;

    public int getInovation(){
        return currentInnovation++; // Suma uno y lo retorna, el valor se guarda
    }
}
