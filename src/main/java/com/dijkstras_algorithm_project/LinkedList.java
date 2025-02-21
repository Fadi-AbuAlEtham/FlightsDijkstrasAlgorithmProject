package com.dijkstras_algorithm_project;

public class LinkedList {
    private Node Front, Back;
    private int Size;

    public LinkedList() {
        Front = Back = null;
        Size = 0;
    }

    public Node getFront() {
        return Front;
    }

    public void setFront(Node front) {
        Front = front;
    }

    public Node getBack() {
        return Back;
    }

    public void setBack(Node back) {
        Back = back;
    }

    public int getSize() {
        return Size;
    }

    public void setSize(int size) {
        Size = size;
    }

    /* Methods go here */
    public void addFirst(Object element) {
        Node newNode;
        newNode = new Node(element);
        if (Size == 0) {// Empty List
            Front = Back = newNode;
        } else {
            newNode.setNext(Front);
            Front = newNode;
        }
        Size++;// update Size
    }

    public Object getFirst() {
        if (Size == 0)
            return null;
        else
            return Front.getElement();
    }

    public void addLast(Object element) {
        Node newNode;
        newNode = new Node(element);
        if (Size == 0) {// Empty List
            Front = Back = newNode;
        } else {
            Back.setNext(newNode);
            Back = newNode; // Or Back=Back.next;
        }
        Size++;// update Size
    }

    public Object getLast() {
        if (Size == 0)
            return null;
        else
            return Back.getElement();
    }

    public Object get(int index) {
        if (Size == 0)
            return null; // empty list
        else if (index == 0)
            return getFirst(); // first element
        else if (index == Size - 1)
            return getLast(); // last element
        else if (index > 0 && index < Size - 1) {
            Node current = Front;
            for (int i = 0; i < index; i++)
                current = current.getNext();
            return current.getElement();
        } else
            return null; // out of boundary
    }

    public int size() {
        return Size;
    }

    public void add(int index, Object element) {
        if (index == 0)
            addFirst(element);
        else if (index >= size())
            addLast(element);
        else {
            Node newNode = new Node(element);
            Node current = Front; // used to advance to proper position
            for (int i = 0; i < index - 1; i++)
                current = current.getNext();
            newNode.setNext(current.getNext());
            current.setNext(newNode);
            Size++;// update size
        }
    }

    public void add(Object element) {
        add(size(), element);
    }

    public boolean removeFirst() {
        if (Size == 0)
            return false; // empty list
        else if (Size == 1) // one element inside list
            Front = Back = null;
        else
            Front = Front.getNext();
        Size--; // update size
        return true;
    }

    public boolean removeLast() {
        if (Size == 0)
            return false; // empty list
        else if (Size == 1) // one element inside the list
            Front = Back = null;
        else {
            Node current = Front;
            for (int i = 0; i < Size - 2; i++)
                current = current.getNext();
            current.setNext(null);
            Back = current;
        }
        Size--; // update size
        return true;
    }

    public boolean remove(int index) {
        if (Size == 0)
            return false;// empty linked list
        else if (index == 0)
            return removeFirst(); // remove first element
        else if (index == getSize() - 1)
            return removeLast();// remove last element
        else if (index > 0 && index < Size - 1) {
            Node current = Front;
            for (int i = 0; i < index - 1; i++)
                current = current.getNext();
            current.setNext(current.getNext().getNext());
            Size--;
            return true;
        } else
            return false; // out of boundary(invalid index)
    }

    public boolean removeByTargetName(String targetName) {
        if (Size == 0) {
            return false; // Empty list
        }
        // Check if the first node is the target
        if (((FlightRoute) Front.getElement()).getTarget().equals(targetName)) {
            return removeFirst();
        }

        Node current = Front;
        while (current.getNext() != null) {
            FlightRoute nextRoute = (FlightRoute) current.getNext().getElement();
            if (nextRoute.getTarget().equalsIgnoreCase(targetName)) {
                // Skip the target node
                current.setNext(current.getNext().getNext());
                Size--;
                return true;
            }
            current = current.getNext();
        }

        return false; // Target not found
    }

    public boolean contains(String matcher) {
        Node current = Front;
        while (current != null) {
            if (matcher.equalsIgnoreCase(((FlightRoute)current.getElement()).getTarget())) {
                return true; // Match found
            }
            current = current.getNext();
        }
        return false; // No match found
    }

    public FlightRoute findByTargetName(String targetName) {
        Node current = Front;
        while (current != null) {
            FlightRoute route = (FlightRoute) current.getElement();
            if (route.getTarget().equalsIgnoreCase(targetName)) {
                return route; // Found the matching route
            }
            current = current.getNext();
        }
        return null; // Not found
    }

    public void print(Node current) {
        if (current != null) {
            System.out.println(current.getElement());
            print(current.getNext());
        }
    }

    // Display the list (for debugging purposes)
    public void display() {
        Node current = Front;
        while (current != null) {
            System.out.println(
                    "Source City: " + ((FlightRoute)(current.getElement())).getSource() +
                    "Target City: " + ((FlightRoute)(current.getElement())).getTarget() +
                            ", Cost: " + ((FlightRoute) (current.getElement())).getCost() +
                            "$, Time: " + ((FlightRoute) (current.getElement())).getTime() + " min"
            );
            current = current.getNext();
        }
    }
}