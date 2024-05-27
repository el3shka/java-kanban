package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyTask = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            int id = task.getId();
            Node tempNode = historyTask.get(id);

            if (tempNode != null) {
                remove(id);
            }

            Node nodeToAdd = linkLast(task);
            historyTask.put(id, nodeToAdd);
        }
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = historyTask.get(id);

        if (nodeToRemove != null) {
            removeNode(nodeToRemove);
            historyTask.remove(id);
        }
    }

    private Node linkLast(Task task) {
        Node newNode = new Node(null, task, null);

        if (head == null) {
            head = newNode;
        } else if (tail == null) {
            tail = newNode;
            newNode.prev = head;
            head.next = tail;
        } else {
            Node oldTail = tail;
            tail = newNode;
            oldTail.next = tail;
            tail.prev = oldTail;
        }
        return newNode;
    }

    private void removeNode(Node node) {
        Node prevNode;
        Node nextNode;

        if (node == head) {

            if (head.next != null) {
                head = node.next;
                head.prev = null;
            } else {
                head = null;
            }

        } else if (node == tail) {
            tail = node.prev;
            tail.next = null;
        } else {
            prevNode = node.prev;
            nextNode = node.next;
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }

    private List<Task> getTasks() {
        List<Task> tasksInHistory = new ArrayList<>(historyTask.size());
        Node tempNode = head;

        while (tempNode != null) {
            tasksInHistory.add(tempNode.task);
            tempNode = tempNode.next;
        }

        return tasksInHistory;
    }

    private static class Node {
        public Task task;
        public Node prev;
        public Node next;

        Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }
}
