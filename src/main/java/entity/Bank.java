package entity;

import java.util.Arrays;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "bankElem")
public class Bank {

    private String name;
    private int[] rankElem;

    public Bank() {
        this.name = null;
        this.rankElem = null;
    }

    public Bank(String name, int[] rankElem) {
        this.name = name;
        this.rankElem = rankElem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getRankElem() {
        return rankElem;
    }

    public void setRankElem(int[] rankElem) {
        this.rankElem = rankElem;
    }

    @Override
    public String toString() {
        return "Bank{" + "name=" + name + ", ranks=" + Arrays.toString(rankElem) + '}';
    }

}
