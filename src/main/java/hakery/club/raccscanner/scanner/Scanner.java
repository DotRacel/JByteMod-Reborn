package hakery.club.raccscanner.scanner;

import hakery.club.raccscanner.Raccoon;

public abstract class Scanner<T> {

    protected Raccoon raccoon;
    private T result;

    private int flagsReached;

    public Scanner() {
    }

    public abstract boolean scan();

    public T getResult() {
        return this.result;
    }

    protected void setResult(T result) {
        this.result = result;
    }

    public int getFlagsReached() {
        return flagsReached;
    }

    public void incrementFlagsReached() {
        this.flagsReached++;
    }

    public void incrementFlagsReached(int inc) {
        this.flagsReached += inc;
    }

    public void reset() {
        this.flagsReached = 0;
    }

    public String getResultAsString() {
        return null;
    }

    public void log(String format, Object... formatting) {
        this.raccoon.getLogger().log("[" + this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.') + 1) + "] " + format, formatting);
    }

}
