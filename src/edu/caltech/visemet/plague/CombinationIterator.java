package edu.caltech.visemet.plague;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Max Hirschhorn #visemet
 */
public class CombinationIterator<T> implements Iterator<List<T>> {

    private final int[] indexes;

    private final List<T> choices;

    public CombinationIterator(int k, List<T> choices) {
        indexes = new int[k];
        for (int index = 0; index < k; index++) {
            indexes[index] = index;
        }

        this.choices = ImmutableList.<T>copyOf(choices);
    }

    @Override
    public boolean hasNext() {
        return indexes[indexes.length - 1] < choices.size();
    }

    @Override
    public List<T> next() {
        List<T> tuple = new ArrayList<>();

        for (int index : indexes) {
            tuple.add(choices.get(index));
        }

        int index = indexes.length - 1;

        indexes[index]++;

        while (indexes[index] == choices.size() - (indexes.length - index - 1)) {
            if (index == 0) {
                break;
            }

            index--;
            indexes[index]++;
        }

        index++;
        for (; index < indexes.length; index++) {
            indexes[index] = indexes[index - 1] + 1;
        }

        return tuple;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
