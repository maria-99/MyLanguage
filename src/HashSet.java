import java.util.ArrayList;
import java.util.List;

public class HashSet {

    private String name;
    List<Integer> list0 = new ArrayList<>();
    List<Integer> list1 = new ArrayList<>();
    List<Integer> list2 = new ArrayList<>();
    List<Integer> list3 = new ArrayList<>();


    HashSet (String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public List<Integer> getAllElements(){
        List<Integer> list = new ArrayList<>();
        list.addAll(list0);
        list.addAll(list1);
        list.addAll(list2);
        list.addAll(list3);
        return list;
    }

    private int getHashes(int x){
        int hashKey;
        hashKey = x % 4;
        return Math.abs(hashKey);
    }

    private List<Integer> getList(int grpNb) {
        if (grpNb == 0){
            return list0;
        }
        else if (grpNb == 1){
            return list1;
        }
        else if (grpNb == 2){
            return list2;
        }
        else if (grpNb == 3){
            return list3;
        }
        throw new RuntimeException("unexpected grp number " + grpNb);
    }

    public boolean contains(int x){
        int grpNb= getHashes(x);
        return getList(grpNb).contains(x);
    }

    public void addValue(int x){
        int grpNb = getHashes(x);
        if(!getList(grpNb).contains(x)) {
            getList(grpNb).add(x);
        }
    }



}
