public class LnkdList {

    int size = 0;
    private final String name;
    Element first;
    Element last;


    LnkdList (String name){
        this.name=name;
    }

    public String getName(){
        return name;
    }

    void addElement(int e) {
        final Element l = last;
        final Element newElement = new Element(l, e, null);
        last = newElement;
        if (l == null)
            first = newElement;
        else
            l.next = newElement;
        size++;
    }

    Element getElement(int index) {
        Element x;
        if (index < (size / 2)) {
            x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
        } else {
            x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
        }
        return x;
    }

    void deleteElement(int index) {
        Element x = getElement(index);
        final Element next = x.next;
        final Element prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }
        x.value = 0; // find better way to delete
        size--;
    }

    void setElement (int index, int newValue){
        Element x = getElement(index);
        x.value = newValue;
    }




}
