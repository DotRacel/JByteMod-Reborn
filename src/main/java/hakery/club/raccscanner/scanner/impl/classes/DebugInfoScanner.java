package hakery.club.raccscanner.scanner.impl.classes;

import hakery.club.raccscanner.scanner.Scanner;
import hakery.club.raccscanner.util.SourceFileData;

import java.util.ArrayList;

/***
 * Shoutout to myself for knowing it was the SourceFileAttribute but thinking
 * it would be sourceDebug
 */
public class DebugInfoScanner extends Scanner<ArrayList<SourceFileData>> {

    @Override
    public boolean scan() {
        ArrayList<SourceFileData> tmp = new ArrayList<>();
        raccoon.getClasses().forEach((name, node) -> {
            if (node.sourceFile != null
                    && node.sourceFile.length() > 0
                    && node.sourceFile.endsWith(".java")
                    && !name.contains(node.sourceFile.replace(".java", ""))) {
                raccoon.setDebugInfoFound(true);

                tmp.add(new SourceFileData(node, node.sourceFile));

                if (raccoon.isDebugging()) {
                    log("Found debug info %s -> %s", name, node.sourceFile);
                }
            }
        });

        this.setResult(tmp);

        return true;
    }
}
