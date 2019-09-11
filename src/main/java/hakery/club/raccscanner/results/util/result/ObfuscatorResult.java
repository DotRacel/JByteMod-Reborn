package hakery.club.raccscanner.results.util.result;

import hakery.club.raccscanner.Raccoon;
import hakery.club.raccscanner.results.util.Obfuscator;
import hakery.club.raccscanner.scanner.Scanner;

import java.util.Optional;

/* basically a pair */
public abstract class ObfuscatorResult {

    protected Raccoon parent;
    private Obfuscator obfuscator;
    private int percentage;

    public ObfuscatorResult(Obfuscator obfuscator, Raccoon raccoon) {
        this.obfuscator = obfuscator;
        this.percentage = 0;
        this.parent = raccoon;
    }

    public int getPercentage() {
        return Math.min(100, this.percentage);
    }

    public void increasePercentage(int percentage) {
        this.percentage += percentage;
    }

    public Obfuscator getObfuscator() {
        return this.obfuscator;
    }

    /**
     * Parse all the results
     * <p>
     * NOTE: This should never totally add up to 100% these probabilities should be compared to the other results,
     * and from there should you decide which result to display
     */
    public abstract void parse();

    /**
     * Get a scanner instance :D
     *
     * @param clazz class of the scanner
     * @return An instance of the class
     */
    public Scanner<?> getScanner(Class<? extends Scanner<?>> clazz) {
        Optional<Scanner<?>> scannerOptional = this.parent.getScanner().getScanners()
                .stream()
                .filter(scanner -> scanner.getClass() == clazz)
                .findAny();

        if (scannerOptional.isPresent())
            return scannerOptional.get();
        return null;
    }
}
