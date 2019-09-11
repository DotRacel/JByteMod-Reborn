package hakery.club.raccscanner.scanner;

import hakery.club.raccscanner.Raccoon;
import hakery.club.raccscanner.scanner.impl.classes.ClassCountScanner;
import hakery.club.raccscanner.scanner.impl.classes.DebugInfoScanner;
import hakery.club.raccscanner.scanner.impl.jar.JarSizeScanner;
import hakery.club.raccscanner.scanner.impl.obfuscators.allatori.AllatoriStringEncryptionScanner;
import hakery.club.raccscanner.scanner.impl.obfuscators.dasho.DashOStringEncryptionScanner;
import hakery.club.raccscanner.scanner.impl.obfuscators.paramorphism.ParamorphismClassloaderScanner;
import hakery.club.raccscanner.scanner.impl.obfuscators.paramorphism.ParamorphismDecrypterScanner;
import hakery.club.raccscanner.scanner.impl.obfuscators.paramorphism.ParamorphismManifestScanner;
import hakery.club.raccscanner.scanner.impl.obfuscators.stringer.StringerHideAccessScanner;
import hakery.club.raccscanner.scanner.impl.obfuscators.stringer.StringerIntegrityControlScanner;
import hakery.club.raccscanner.scanner.impl.obfuscators.stringer.StringerManifestScanner;
import hakery.club.raccscanner.scanner.impl.obfuscators.stringer.StringerStringEncryptionScanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scanners {

    private Raccoon raccoonInstance;

    private ArrayList<Scanner<?>> scannerArrayList = new ArrayList<>();

    public Scanners(Raccoon instance) {
        this.raccoonInstance = instance;

        add(Arrays.asList(
                /* classes */
                ClassCountScanner.class,
                DebugInfoScanner.class,

                /* debug */
                //DebugScanner.class,

                /* obfuscators */
                AllatoriStringEncryptionScanner.class,

                ParamorphismDecrypterScanner.class,
                ParamorphismClassloaderScanner.class,
                ParamorphismManifestScanner.class,

                StringerStringEncryptionScanner.class,
                StringerHideAccessScanner.class,
                StringerIntegrityControlScanner.class,
                StringerManifestScanner.class,

                DashOStringEncryptionScanner.class
        ));
    }

    public void scan() {
        this.scannerArrayList.forEach(scanner -> {
            if (!scanner.scan())
                System.out.println(String.format("[Raccoon] scanner %s ended in failure", scanner.getClass().getName()));
        });
    }

    private void add(List<Class<? extends Scanner<?>>> scanner) {
        scanner.forEach(scanner1 -> {
            try {
                Scanner instance = scanner1.newInstance();
                instance.raccoon = raccoonInstance;

                this.scannerArrayList.add(instance);
            } catch (Exception _) {
                _.printStackTrace();
            }
        });
    }

    public ArrayList<Scanner<?>> getScanners() {
        return scannerArrayList;
    }

}
