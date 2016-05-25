import java.util.HashSet;
import java.util.Set;

public class SkipList<T extends Comparable<? super T>>
    implements SkipListInterface<T> {
    // Do not add any additional instance variables
    private CoinFlipper coinFlipper;
    private int size;
    private Node<T> head;

    /**
     * Constructs a SkipList object that stores data in ascending order.
     * When an item is inserted, the flipper is called until it returns a tails.
     * If, for an item, the flipper returns n heads, the corresponding node has
     * n + 1 levels.
     *
     * @param coinFlipper the source of randomness
     */
    public SkipList(CoinFlipper coinFlipper) {
        if (coinFlipper == null) {
            this.coinFlipper = new CoinFlipper();
            head = new Node<>(null, 1);
        } else {
            this.coinFlipper = coinFlipper;
            head = new Node<>(null, 1);
        }
    }

    @Override
    public T first() {
        if (size == 0) {
            throw new java.util.NoSuchElementException("SkipList is empty!");
        } else {
            Node<T> currNode = head;
            while (currNode.getDown() != null) {
                currNode = currNode.getDown();
            }
            return currNode.getNext().getData();
        }
    }

    @Override
    public T last() {
        if (size == 0) {
            throw new java.util.NoSuchElementException("SkipList is empty!");
        }
        Node<T> currNode = head;
        boolean found = false;
        while (!found) {
            if (currNode.getNext() != null) {
                currNode = currNode.getNext();
            } else if (currNode.getDown() != null) {
                currNode = currNode.getDown();
            } else {
                found = true;
            }
        }
        return currNode.getData();
    }

    /**
     * Traverses the SkipList to find the node containing the data or the
     * preceding node closest to it
     *
     * @param currNode the starting node, head
     * @param data the data to be found or inserted
     * @return node containing the data or the node preceding it where the
     * data will be placed
     */
    private Node<T> finder(Node<T> currNode, T data) {
        boolean search = false;
        while (!search) {
            if (currNode.getNext() != null
                    && data.compareTo(currNode.getNext().getData()) >= 0) {
                currNode = currNode.getNext();
            } else if (currNode.getDown() != null) {
                currNode = currNode.getDown();
            } else {
                search = true;
            }
        }
        return currNode;
    }

    /**
     * Links the newly added node to the SkipList
     *
     * @param currNode the node preceding the new node to be added
     * @param newNode the node to be added
     */
    private void attach(Node<T> currNode, Node<T> newNode) {
        if (currNode.getNext() != null) {
            Node<T> right = currNode.getNext();
            currNode.setNext(newNode);
            newNode.setPrev(currNode);
            newNode.setNext(right);
            right.setPrev(newNode);
        } else {
            currNode.setNext(newNode);
            newNode.setPrev(currNode);
        }
    }

    /**
     * Randomly layers a newly added data and links it accordingly
     *
     * @param currNode the node preceding the new added data
     * @param layerNode the node to be layered on top of the newNode
     * @param data the data to be placed in the layered node
     */
    private void randomCoinPut(Node<T> currNode, Node<T> layerNode, T data) {
        while (coinFlipper.flipCoin() == CoinFlipper.Coin.HEADS) {
            Node<T> up = new Node<>(data, layerNode.getLevel() + 1);
            boolean added = false;
            while (!added) {
                if (currNode.getUp() == null
                        && currNode.getPrev() != null) {
                    currNode = currNode.getPrev();
                } else if (currNode.getUp() != null) {
                    currNode = currNode.getUp();
                    attach(currNode, up);
                    up.setDown(layerNode);
                    layerNode.setUp(up);
                    layerNode = up;
                    added = true;
                } else {
                    currNode.setUp(new Node<>(null,
                            currNode.getLevel() + 1));
                    head = currNode.getUp();
                    head.setDown(currNode);
                    attach(head, up);
                    up.setDown(layerNode);
                    layerNode.setUp(up);
                    layerNode = up;
                    currNode = currNode.getUp();
                    added = true;
                }
            }
        }
    }

    @Override
    public void put(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data is null!");
        } else if (size == 0) {
            Node<T> newNode = new Node<>(data, 1);
            Node<T> currNode = head;
            currNode.setNext(newNode);
            newNode.setPrev(currNode);
            randomCoinPut(currNode, newNode, data);
            size++;
        } else {
            Node<T> currNode = finder(head, data);
            size++;
            Node<T> newNode = new Node<>(data, 1);
            if (currNode.getData() != null
                    && currNode.getData().compareTo(data) == 0) {
                System.out.println(currNode.getData());
                System.out.println(data);
                size--;
                currNode.setData(data);
                while (currNode.getUp() != null) {
                    currNode.getUp().setData(data);
                    currNode = currNode.getUp();
                }
            } else {
                attach(currNode, newNode);
                randomCoinPut(currNode, newNode, data);
            }
        }
    }

    @Override
    public T remove(T data) {
        T retval;
        if (data == null) {
            throw new IllegalArgumentException("Data is null!");
        }
        Node<T> currNode = finder(head, data);

        if (size == 0 || currNode.getData() == null
                || currNode.getData().compareTo(data) != 0) {
            throw new java.util.NoSuchElementException(
                    "Data not in SkipList or SkipList is empty!");
        } else {
            size--;
            retval = currNode.getData();
            while (currNode.getUp() != null) {
                currNode = currNode.getUp();
            }
            detach(currNode);
        }
        return retval;
    }

    /**
     * Removes the links from the node to be deleted from the SkipList
     *
     * @param currNode the node to be unlinked from the SkipList
     */
    private void detach(Node<T> currNode) {
        int i = currNode.getLevel();
        while (i >= 1) {
            Node<T> prev = currNode.getPrev();
            if (currNode.getNext() != null) {
                prev.setNext(currNode.getNext());
                currNode.getNext().setPrev(prev);
            } else {
                prev.setNext(null);
                currNode.setPrev(null);
            }
            if (head.getNext() == null && head.getDown() != null) {
                head = head.getDown();
                head.setUp(null);
            }
            if (currNode.getDown() != null) {
                currNode = currNode.getDown();
            }
            i--;
        }
    }

    @Override
    public boolean contains(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data is null!");
        }
        Node<T> currNode = finder(head, data);
        boolean wasFound = false;
        if (size == 0 || currNode.getData() != null
                && currNode.getData().compareTo(data) != 0) {
            wasFound = false;
        } else if (currNode.getData() != null
                && currNode.getData().compareTo(data) == 0) {
            wasFound = true;
        }
        return wasFound;
    }

    @Override
    public T get(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data is null!");
        }
        Node<T> currNode = finder(head, data);
        T retval = null;
        if (size == 0 || currNode.getData() == null
                || currNode.getData().compareTo(data) != 0) {
            throw new java.util.NoSuchElementException(
                    "Data not in SkipList or SkipList is empty!");
        } else if (currNode.getData() != null
                && currNode.getData().compareTo(data) == 0) {
            retval = currNode.getData();
        }
        return retval;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        size = 0;
        head = new Node<>(null, 1);
    }

    @Override
    public Set<T> dataSet() {
        Set<T> set = new HashSet<>();
        if (size == 0) {
            return set;
        } else {
            Node<T> currNode = head;
            while (currNode.getDown() != null) {
                currNode = currNode.getDown();
            }
            currNode = currNode.getNext();
            while (currNode != null) {
                set.add(currNode.getData());
                currNode = currNode.getNext();
            }
        }
        return set;
    }

    @Override
    public Node<T> getHead() {
        return head;
    }
}
