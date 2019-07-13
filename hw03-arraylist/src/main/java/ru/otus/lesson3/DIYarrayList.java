package ru.otus.lesson3;

import java.util.*;

/**
 * Created by Alexander Bryantsev on 08.07.2019.
 */
public class DIYarrayList<E> implements List<E> {

    private Object[] dataList;

    public DIYarrayList() {
        this.dataList = new Object[0];
    }

    public DIYarrayList(int initialCapacity) {
        this.dataList = new Object[initialCapacity];
    }

    @Override
    public int size() {
        return dataList.length;
    }

    @Override
    public boolean isEmpty() {
        return dataList.length == 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        return dataList;
    }

    @Override
    public <E> E[] toArray(E[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(E t) {
        dataList = Arrays.copyOf(dataList, dataList.length + 1);
        dataList[dataList.length - 1] = t;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAllExtended(dataList.length, c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkRange(index);

        return addAllExtended(index, c);
    }

    private void checkRange(int index) {
        if (index < 0 || index >= dataList.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + dataList.length);
        }
    }

    /**
     * Insert all of the elements in the specified collection into this List at the specified index.
     * If index > length then List will be extend filling elements between length and index - 1 with null
     *
     * @param index index at which to insert the first element from the
     *              specified collection. Maybe greater length
     * @param c     collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     */
    public boolean addAllExtended(int index, Collection<? extends E> c) {
        if (c.size() > 0) {
            int oldSize = dataList.length;
            Object[] src = c.toArray();
            dataList = Arrays.copyOf(dataList, oldSize + src.length);
            if (index < oldSize) {
                System.arraycopy(dataList, index, dataList, index + src.length, oldSize - index);
            }
            System.arraycopy(src, 0, dataList, index, src.length);

            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E get(int index) {
        return (E) dataList[index];
    }

    @Override
    public E set(int index, E element) {
        checkRange(index);
        dataList[index] = element;
        return element;
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        checkRange(index);

        return new ListIterator<E>() {
            int position = index;

            @Override
            public boolean hasNext() {
                return position < dataList.length;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return (E) dataList[position++];
            }

            @Override
            public boolean hasPrevious() {
                return position > 0;
            }

            @Override
            public E previous() {
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                }
                return (E) dataList[--position];
            }

            @Override
            public int nextIndex() {
                return position;
            }

            @Override
            public int previousIndex() {
                return position - 1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(E e) {
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                }
                DIYarrayList.this.set(position - 1, e);
            }

            @Override
            public void add(E e) {
                DIYarrayList.this.add(e);
            }
        };
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return Arrays.toString(dataList);
    }
}
