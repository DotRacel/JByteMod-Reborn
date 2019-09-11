package hakery.club.raccscanner.scanner.impl.obfuscators.stringer;

import hakery.club.raccscanner.scanner.Scanner;
import hakery.club.raccscanner.util.OpcodeUtils;
import hakery.club.raccscanner.util.opcodes.InstructionList;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;

/**
 * Info about this stub:
 * - Instructions are always 60
 * - Instructions are always the same (except for 1 LDC)
 * - Method is always the same
 */
public class StringerIntegrityControlScanner extends Scanner<ArrayList<ClassNode>> {

    InstructionList stringerClassIntegrityControlInstructionsV9_1_15 = new InstructionList(Arrays.asList(
            ACONST_NULL,
            ASTORE,
            ALOAD,
            INVOKESTATIC,
            ASTORE,
            LDC,
            INVOKEVIRTUAL,
            ASTORE,
            ALOAD,
            ALOAD,
            INVOKESTATIC,
            ASTORE,
            ILOAD,
            TABLESWITCH,
            0xFF,
            ALOAD,
            ALOAD,
            ALOAD,
            ALOAD,
            INVOKEVIRTUAL

    ));

    @Override
    public boolean scan() {
        ArrayList<ClassNode> tmp = new ArrayList<>();
        raccoon.getClasses().forEach((classPath, classNode) -> {
            classNode.methods.forEach(methodNode -> {
                if (methodNode.desc.equals("(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;"))
                    incrementFlagsReached();
                if (methodNode.maxLocals == 11 && methodNode.maxStack == 4)
                    incrementFlagsReached();

                InstructionList instructionList = new InstructionList(methodNode.instructions);

                if (instructionList.size() == 60)
                    incrementFlagsReached();

                if (OpcodeUtils.getInstance().compareOpcodes(stringerClassIntegrityControlInstructionsV9_1_15, instructionList, 0))
                    incrementFlagsReached();

            });

            if (getFlagsReached() > 2)
                tmp.add(classNode);

            if (raccoon.isDebugging() && getFlagsReached() != 0) {
                log("%s.class with certainty level: %d (%s)",
                        classPath,
                        Math.min(getFlagsReached(), 3),
                        getFlagsReached() == 1 ?
                                "Plausible" :
                                getFlagsReached() == 2
                                        ? "Undecisive" :
                                        "Confident");
                this.reset();
            }
        });
        setResult(tmp);
        return true;
    }
}
