package hakery.club.raccscanner.scanner.impl.classes;

import hakery.club.raccscanner.scanner.Scanner;

public class ClassCountScanner extends Scanner<Integer> {

    @Override
    public boolean scan() {
        //TODO: Filter out invalid classes
        this.setResult(raccoon.getClasses().size());

        if (raccoon.isDebugging())
            log("Found %d classes", this.getResult());

        return true;
    }
}
