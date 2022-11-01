package data_structure;

import genome.Gene;

import java.util.ArrayList;
import java.util.HashSet;

public class RandomHashSet<T> {

    HashSet<T> set;
    ArrayList<T> data;

    public RandomHashSet(){
        set = new HashSet<>();
        data = new ArrayList<>();
    }

    // Comprueba si el objeto ya esta contenido
    public boolean contains(T object){
        return set.contains(object);
    }

    // Debuelve un elemento aleatorio si el tamañp de set es mayor a 0
    public T random_element(){
        if (set.size() > 0){
            return data.get((int)(Math.random() * size()));
        }
        return null;
    }

    public int size(){
        return data.size();
    }

    // Agrega un objeto a la lista si el objeto no existe
    public void add(T object){
        if (!set.contains(object)){
            set.add(object);
            data.add(object);
        }
    }

    // Agrega ordenandamente
    public void add_sorted(Gene object){
        for (int i = 0; i < this.size(); i++){
            int innovation = ((Gene)data.get(i)).getInnovation_number();
            if (object.getInnovation_number() < innovation){
                data.add(i, (T)object);
                set.add((T)object);
                return;
            }
        }
        data.add((T)object);
        set.add((T)object);
    }

    public void clear(){
        set.clear();
        data.clear();
    }

    public T get(int index){
        if (index < 0 || index >= size()) return null;
        return data.get(index);
    }

    public void remove(int index){
        if (index < 0 || index >= size()) return;
        set.remove(data.get(index));
        data.remove(index);
    }

    public void remove(T object){
        set.remove(object);
        data.remove(object);
    }

    public HashSet<T> getSet() {
        return set;
    }

    public ArrayList<T> getData() {
        return data;
    }
}
