package hakery.club.raccscanner.scanner.impl.obfuscators.paramorphism;

import hakery.club.raccscanner.scanner.Scanner;
import hakery.club.raccscanner.util.DataUtils;

/**
 * Paramorphism leaves
 * "Obfuscated by: Paramorphism" in the manifest, can be removed
 */
public class ParamorphismManifestScanner extends Scanner<Boolean> {
    @Override
    public boolean scan() {
        setResult(false);
        if (raccoon.getJarManifest() != null) {
            byte[] paramorphismByteArray = "Paramorphism".getBytes();
            if (DataUtils.INSTANCE.findInByteArray(paramorphismByteArray, raccoon.getJarManifest())) {
                log("Paramorphism was found in Manifest file.");
                setResult(true);
            }
        }

        return raccoon.getJarManifest() != null;
    }
}
