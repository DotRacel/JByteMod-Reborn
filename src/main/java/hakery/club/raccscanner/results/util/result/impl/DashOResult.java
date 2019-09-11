package hakery.club.raccscanner.results.util.result.impl;

import hakery.club.raccscanner.Raccoon;
import hakery.club.raccscanner.results.util.Obfuscator;
import hakery.club.raccscanner.results.util.result.ObfuscatorResult;
import hakery.club.raccscanner.scanner.impl.obfuscators.dasho.DashOStringEncryptionScanner;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

public class DashOResult extends ObfuscatorResult {

    DashOStringEncryptionScanner dashOStringEncryptionScanner;

    public DashOResult(Raccoon raccoon) {
        super(Obfuscator.DASHO, raccoon);
    }

    @Override
    public void parse() {
        this.dashOStringEncryptionScanner = (DashOStringEncryptionScanner) this.getScanner(DashOStringEncryptionScanner.class);

        if (this.dashOStringEncryptionScanner.getResult().size() >= 2)
            increasePercentage(50);
    }

    public ArrayList<ClassNode> getStringEncryptionResult() {
        return this.dashOStringEncryptionScanner.getResult();
    }
}
