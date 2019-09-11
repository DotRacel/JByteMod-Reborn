package hakery.club.raccscanner.scanner.impl.jar;

import hakery.club.raccscanner.scanner.Scanner;

public class JarSizeScanner extends Scanner<Long> {

    @Override
    public boolean scan() {
        this.setResult(raccoon.getTargetFile().length());

        if (raccoon.isDebugging())
            log("Jar is %o bytes", this.getResult());

        return true;
    }
}
