package hakery.club.raccscanner.scanner.impl.debugging;

import com.sun.org.apache.xpath.internal.operations.String;
import hakery.club.raccscanner.scanner.Scanner;
import me.lpk.util.ASMUtils;
import org.objectweb.asm.ClassWriter;

public class DebugScanner extends Scanner<Integer> {

    @Override
    public boolean scan() {
        raccoon.getClasses().forEach((name, node) -> {
            System.out.println(name);
            node.methods.forEach(methodNode -> System.out.println(methodNode.name));
        });

        return true;
    }
}
