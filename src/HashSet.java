public class HashSet {

    private String name;
    LnkdList[] lists = new LnkdList[4];



    HashSet (String name){
        this.name = name;
        for(int i=0; i<4; i++ ){
            lists[i] = new LnkdList("hashSetList" + String.valueOf(i));
        }
    }

    public String getName(){
        return name;
    }

    public void getAllElements(){

        for (LnkdList list : lists){
            while(list.size>0){
                System.out.println( list.first.value);
                list.deleteElement(0);
            }
        }
    }

    private int getHashes(int x){
        int hashKey;
        hashKey = x % 4;
        return Math.abs(hashKey);
    }

    private LnkdList getList(int grpNb) {
        return lists[grpNb];
    }

    public boolean contains(int x){
        int grpNb= getHashes(x);
        return getList(grpNb).contains(x);
    }

    public void addValue(int x){
        int grpNb = getHashes(x);
        if(!getList(grpNb).contains(x)) {
            getList(grpNb).addElement(x);
        }
    }



}
