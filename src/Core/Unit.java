package Core;

/**
 * Created by Allen on 2016/3/20.
 */
public class Unit {
    int code;
    int pos;

    public Unit() {
        pos = -1;
    }

    public Unit(int code) {
        this.code = code;
        pos = -1;
    }

    public Unit(int code, int pos) {
        this.code = code;
        this.pos = pos;
    }
}
