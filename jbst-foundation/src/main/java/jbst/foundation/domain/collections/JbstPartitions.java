package jbst.foundation.domain.collections;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class JbstPartitions<T> extends AbstractList<List<T>> {

    private final List<T> list;
    private final int chunkSize;

    public JbstPartitions(List<T> list, int chunkSize) {
        this.list = new ArrayList<>(list);
        this.chunkSize = chunkSize;
    }

    public static <T> JbstPartitions<T> ofSize(List<T> list, int chunkSize) {
        return new JbstPartitions<>(list, chunkSize);
    }

    @Override
    public List<T> get(int index) {
        int start = index * this.chunkSize;
        int end = Math.min(start + this.chunkSize, this.list.size());
        if (start > end) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of the list range <0," + (size() - 1) + ">");
        }
        return new ArrayList<>(this.list.subList(start, end));
    }

    @Override
    public int size() {
        return (int) Math.ceil((double) this.list.size() / (double) this.chunkSize);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JbstPartitions<?> partitions = (JbstPartitions<?>) o;
        return this.chunkSize == partitions.chunkSize && Objects.equals(this.list, partitions.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.list, this.chunkSize);
    }
}

