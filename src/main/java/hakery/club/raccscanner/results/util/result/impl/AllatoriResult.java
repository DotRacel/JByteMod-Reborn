package hakery.club.raccscanner.results.util.result.impl;

import hakery.club.raccscanner.Raccoon;
import hakery.club.raccscanner.results.util.Obfuscator;
import hakery.club.raccscanner.results.util.result.ObfuscatorResult;
import hakery.club.raccscanner.scanner.impl.obfuscators.allatori.AllatoriStringEncryptionScanner;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

public class AllatoriResult extends ObfuscatorResult {

    private AllatoriStringEncryptionScanner stringEncryptionScanner;

    public AllatoriResult(Raccoon raccoon) {
        super(Obfuscator.ALLATORI, raccoon);
    }

    @Override
    public void parse() {
        this.stringEncryptionScanner = (AllatoriStringEncryptionScanner) getScanner(AllatoriStringEncryptionScanner.class);

        if (stringEncryptionScanner.getResult().size() > 2)
            this.increasePercentage(50);
    }

    public ArrayList<ClassNode> getStringEncryptionResult() {
        return this.stringEncryptionScanner.getResult();
    }
}
