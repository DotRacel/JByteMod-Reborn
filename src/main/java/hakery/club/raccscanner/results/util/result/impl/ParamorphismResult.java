package hakery.club.raccscanner.results.util.result.impl;

import hakery.club.raccscanner.Raccoon;
import hakery.club.raccscanner.results.util.Obfuscator;
import hakery.club.raccscanner.results.util.result.ObfuscatorResult;
import hakery.club.raccscanner.scanner.impl.obfuscators.paramorphism.ParamorphismClassloaderScanner;
import hakery.club.raccscanner.scanner.impl.obfuscators.paramorphism.ParamorphismDecrypterScanner;
import hakery.club.raccscanner.scanner.impl.obfuscators.paramorphism.ParamorphismManifestScanner;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

public class ParamorphismResult extends ObfuscatorResult {

    private ParamorphismClassloaderScanner classloaderScanner;
    private ParamorphismDecrypterScanner decrypterScanner;
    private ParamorphismManifestScanner manifestScanner;

    public ParamorphismResult(Raccoon raccoon) {
        super(Obfuscator.PARAMORPHISM, raccoon);
    }

    @Override
    public void parse() {
        this.classloaderScanner = (ParamorphismClassloaderScanner) this.getScanner(ParamorphismClassloaderScanner.class);
        this.decrypterScanner = (ParamorphismDecrypterScanner) this.getScanner(ParamorphismDecrypterScanner.class);
        this.manifestScanner = (ParamorphismManifestScanner) this.getScanner(ParamorphismManifestScanner.class);

        if (getClassLoaderResults().size() > 1)
            increasePercentage(25);

        if (getDecrypterResults().size() > 1)
            increasePercentage(15);

        if (isPresentInManifest())
            increasePercentage(30);
    }

    /**
     * Same sort of wrapper here
     *
     * @return ArrayList that contains ClassNode's of all classes that were found to be containing this
     * ClassLoader stub
     */
    public ArrayList<ClassNode> getClassLoaderResults() {
        return this.classloaderScanner.getResult();
    }

    /**
     * A 'wrapper' of sorts to the Decrypter scanner so that we can easily get the Nodes that were detected
     *
     * @return The results of the Decrypter scanner
     */
    public ArrayList<ClassNode> getDecrypterResults() {
        return this.decrypterScanner.getResult();
    }

    /**
     * @return Returns true if the manifest contained any signs of Paramorphism
     */
    public boolean isPresentInManifest() {
        return this.manifestScanner.getResult();
    }

}
