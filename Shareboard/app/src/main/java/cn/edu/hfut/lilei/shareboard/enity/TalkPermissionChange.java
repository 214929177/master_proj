package cn.edu.hfut.lilei.shareboard.enity;


public class TalkPermissionChange {
    boolean istalkable;

    public TalkPermissionChange(boolean istalkable) {
        this.istalkable = istalkable;
    }

    public boolean istalkable() {
        return istalkable;
    }

    public void setIstalkable(boolean istalkable) {
        this.istalkable = istalkable;
    }

    public TalkPermissionChange() {


    }
}
