package hakery.club.raccscanner.scanner.impl.obfuscators.stringer;

import hakery.club.raccscanner.scanner.Scanner;
import hakery.club.raccscanner.util.DataUtils;

public class StringerManifestScanner extends Scanner<Boolean> {
    @Override
    public boolean scan() {
        setResult(false);

        if (raccoon.getJarManifest() != null) {
            byte[] email = "AV contact email".getBytes();
            byte[] stringer = "Stringer".getBytes();
            if (DataUtils.INSTANCE.findInByteArray(email, raccoon.getJarManifest())
                    || DataUtils.INSTANCE.findInByteArray(stringer, raccoon.getJarManifest())) {
                log("Stringer was found in Manifest file.");
                setResult(true);
            }
        }

        return raccoon.getJarManifest() != null;
    }
}
