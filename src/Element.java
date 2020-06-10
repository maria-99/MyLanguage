public class Element {
    int value;
    Element next;
    Element prev;

    Element(Element prev, int element, Element next) {
        this.value = element;
        this.next = next;
        this.prev = prev;
    }
}
