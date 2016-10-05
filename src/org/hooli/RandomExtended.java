package org.hooli;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;


public class RandomExtended extends Random{

    public <T> ArrayList<T> randomChoice(ArrayList<T> input, int n){
        if(input.size() <= n){
            return new ArrayList<>(input);
        }

        HashSet<Integer> indexes = new HashSet<>();

        while(indexes.size() < n){
            int number = nextInt(input.size());
            if(!indexes.contains(number)){
                indexes.add(number);
            }
        }

        ArrayList<T> selection = new ArrayList<>();

        for(Integer i : indexes){
            selection.add(input.get(i));
        }

        return selection;
    }

}
