
/**
 * 
 */
public enum DieResult {

    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    NOT_ROLLED(-1);

    private int value;

    DieResult(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}