package ru.otus.lesson3;

import java.util.*;

/**
 * Created by Alexander Bryantsev on 08.07.2019.
 */
public class DIYarrayList<E> implements List<E> {

    private Object[] dataList;
    private int size;
    private int stepToIncCapacity = 10;

    public DIYarrayList() {
        this.dataList = new Object[stepToIncCapacity];
    }

    public DIYarrayList(int initialCapacity) {
        this.dataList = new Object[initialCapacity];
    }

    public DIYarrayList(int initialCapacity, int stepToIncCapacity) {
        this.dataList = new Object[initialCapacity];
        this.stepToIncCapacity = stepToIncCapacity;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
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
        return Arrays.copyOf(dataList, size);
    }

    @Override
    public <E> E[] toArray(E[] a) {
        if (a.length < size)
            return (E[]) Arrays.copyOf(dataList, size, a.getClass());
        System.arraycopy(dataList, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    @Override
    public boolean add(E t) {
        checkAdequacyCapacity(size + 1);
        dataList[size++] = t;
        return true;
    }

    private void checkAdequacyCapacity(int requiredCapacity) {
        if (requiredCapacity > dataList.length) {
            dataList = Arrays.copyOf(dataList, requiredCapacity + stepToIncCapacity);
        }
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
        return addAllExtended(size, c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkRange(index);

        return addAllExtended(index, c);
    }

    private void checkRange(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
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
            size = oldSize + src.length;
            checkAdequacyCapacity(size);
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
        checkRange(index);
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
            int lastElementReturned = -1; // index of last element returned, -1 if not such

            @Override
            public boolean hasNext() {
                return position < size;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                lastElementReturned = position++;
                return (E) dataList[lastElementReturned];
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
                lastElementReturned = --position;
                return (E) dataList[lastElementReturned];
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
                if (lastElementReturned < 0) {
                    throw new IllegalStateException("Method set must be executed only after applying previous or next methods!");
                }
                DIYarrayList.this.set(lastElementReturned, e);
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
        return Arrays.toString(toArray());
    }
}
