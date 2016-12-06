/**
 * 
 */
public class Die {


    private DieResult result;
    private boolean savedDie;

    public Die() {
        this.result = DieResult.NOT_ROLLED;
    }

    public Die(int value){
        this.setResult(value);
    }

    public void printValue() {
        System.out.print(this.result);
    }

    public void randomizeResult() {
        int resultValue = 1 + (int) (Math.random()*6);

        setResult(resultValue);

    }

    /**
     * Getters and Setters
     */
    public boolean isSavedDie() {
        return savedDie;
    }

    public void setSavedDie(boolean savedDie) {
        this.savedDie = savedDie;
    }

    public DieResult getDieResult() {
        return result;
    }

    public void setResult(int value) {
        switch (value){
            case 1:
                result = DieResult.ONE;
                break;
            case 2:
                result = DieResult.TWO;
                break;
            case 3:
                result = DieResult.THREE;
                break;
            case 4:
                result = DieResult.FOUR;
                break;
            case 5:
                result = DieResult.FIVE;
                break;
            case 6:
                result = DieResult.SIX;
                break;
        }
    }
}