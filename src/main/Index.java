package main;

public class Index {
    short i;
    short j;

    Index(short i,short j){
        this.i = i;
        this.j = j;
    }

    @Override
    public boolean equals(Object o) {
        // typecast o to Complex so that we can compare data members
        Index c = (Index) o;

        // Compare the data members and return accordingly
        return c.i == i && c.j == j;
    }
}
