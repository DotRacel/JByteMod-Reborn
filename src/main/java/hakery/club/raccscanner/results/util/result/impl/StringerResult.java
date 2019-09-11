package hakery.club.raccscanner.results.util.result.impl;

import hakery.club.raccscanner.Raccoon;
import hakery.club.raccscanner.results.util.Obfuscator;
import hakery.club.raccscanner.results.util.result.ObfuscatorResult;
import hakery.club.raccscanner.scanner.impl.classes.ClassCountScanner;
import hakery.club.raccscanner.scanner.impl.obfuscators.stringer.StringerHideAccessScanner;
import hakery.club.raccscanner.scanner.impl.obfuscators.stringer.StringerIntegrityControlScanner;
import hakery.club.raccscanner.scanner.impl.obfuscators.stringer.StringerManifestScanner;
import hakery.club.raccscanner.scanner.impl.obfuscators.stringer.StringerStringEncryptionScanner;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

public class StringerResult extends ObfuscatorResult {

    StringerHideAccessScanner hideAccessScanner;
    StringerIntegrityControlScanner integrityControlScanner;
    StringerStringEncryptionScanner stringerStringEncryptionScanner;
    StringerManifestScanner stringerManifestScanner;

    public StringerResult(Raccoon raccoon) {
        super(Obfuscator.STRINGER, raccoon);
    }

    @Override
    public void parse() {
        this.hideAccessScanner = (StringerHideAccessScanner) getScanner(StringerHideAccessScanner.class);
        this.integrityControlScanner = (StringerIntegrityControlScanner) getScanner(StringerIntegrityControlScanner.class);
        this.stringerStringEncryptionScanner = (StringerStringEncryptionScanner) getScanner(StringerStringEncryptionScanner.class);
        this.stringerManifestScanner = (StringerManifestScanner) getScanner(StringerManifestScanner.class);

        ClassCountScanner classCountScanner = (ClassCountScanner) getScanner(ClassCountScanner.class);

        if (classCountScanner.getResult() < 10)
            this.parent.getLogger().log("[StringerResult#parse] SITUATION: Amount of classes is lower than 10 -> RESULT: Scanning might be inconsistent");

        if (this.hideAccessScanner.getResult().size() >= 2)
            increasePercentage(30);

        if (this.integrityControlScanner.getResult().size() >= 3)
            increasePercentage(25);

        if (this.stringerStringEncryptionScanner.getResult().size() >= 2)
            increasePercentage(30);

        if (this.stringerManifestScanner.getResult())
            increasePercentage(50);
    }

    /**
     * Wappers
     */
    public ArrayList<ClassNode> getHideAccessResult() {
        return this.hideAccessScanner.getResult();
    }

    public ArrayList<ClassNode> getIntegrityControlResult() {
        return this.integrityControlScanner.getResult();
    }

    public ArrayList<ClassNode> getStringEncryptionResult() {
        return this.stringerStringEncryptionScanner.getResult();
    }

    public boolean isPresentInManifest() {
        return this.stringerManifestScanner.getResult();
    }
}
