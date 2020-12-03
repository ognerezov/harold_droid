package net.okhotnikov.harald.model.processing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class DoubleRing implements Collection<Double> {
    private final int capacity;
    private final double[] buffer;
    private int cursor = 0;
    private boolean closed = false;

    public DoubleRing(int capacity) {
        this.capacity = capacity;
        buffer = new double[capacity];
    }

    public boolean isFull(){
        return closed || cursor == capacity - 1;
    }

    public double last(){
        return buffer[cursor - 1];
    }

    private int shifted(int index){
        if (closed){
            int res = index + cursor;

            if (res >= capacity){
                res -= capacity;
            }

            return res;
        }
        return index;
    }

    public double get(int index){
        return buffer[shifted(index)];
    }

    public void set(float value, int index){
        buffer[shifted(index)] = value;
    }

    @Override
    public int size() {
        return closed ? capacity : cursor;
    }

    @Override
    public boolean isEmpty() {
        return closed || cursor > 0;
    }

    @Override
    public boolean contains(@Nullable Object o) {
        if (!(o instanceof Float))
            return false;
        for(double f: buffer){
            if(o.equals(f))
                return true;
        }
        return false;
    }

    @NonNull
    @Override
    public Iterator<Double> iterator() {
        return new DoubleIterator(this);
    }

    @NonNull
    @Override
    public Object[] toArray() {
        Double[] res = new Double[capacity];
        for(int i=0; i<capacity; i++)
            res[i] = buffer[shifted(i)];
        return res;
    }

    public double[] snapshot(){
        double[] res = new double[capacity];
        for(int i=0; i<capacity; i++)
            res[i] = buffer[shifted(i)];
        return res;
    }

    public double[] sortedSnapshot(){
        double[] res = snapshot();
        Arrays.sort(res);
        return res;
    }


    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return (T[])toArray();
    }

    @Override
    public boolean add(Double aFloat) {
        if (cursor == capacity){
            cursor = 0;
            closed = true;
        }
        buffer[cursor] = aFloat;
        cursor ++;
        return true;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return false;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Double> c) {
        for(Double f: c)
            add(f);
        return true;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        cursor = 0;
        closed = false;
    }


    public static class DoubleIterator implements Iterator<Double>{
        int index =0;
        final DoubleRing ring;

        public DoubleIterator(DoubleRing ring) {
            this.ring = ring;
        }

        @Override
        public boolean hasNext() {
            return ring.closed ? index < ring.capacity : index < ring.cursor;
        }

        @Override
        public Double next() {
            return ring.get(index++);
        }
    }

}
