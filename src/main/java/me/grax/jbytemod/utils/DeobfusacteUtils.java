package me.grax.jbytemod.utils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.CheckClassAdapter;

import java.util.Iterator;
import java.util.Map;

public class DeobfusacteUtils {
    // Some of them are from https://github.com/java-deobfuscator or https://github.com/ItzSomebody/Radon/

    public static void fixSignature(Map<String, ClassNode> classes){
        classes.values().forEach(DeobfusacteUtils::fixSignature);
    }

    public static void fixSignature(ClassNode classNode) {
        if (classNode.signature != null) {
            try {
                CheckClassAdapter.checkClassSignature(classNode.signature);
            } catch (IllegalArgumentException IAE) {
                classNode.signature = null;
            } catch (Throwable x) {
                x.printStackTrace();
            }
        }

        classNode.methods.forEach(methodNode -> {
            if (methodNode.signature != null) {
                try {
                    CheckClassAdapter.checkMethodSignature(methodNode.signature);
                } catch (IllegalArgumentException IAE) {
                    methodNode.signature = null;
                } catch (Throwable x) {
                    x.printStackTrace();
                }
            }
        });

        classNode.fields.forEach(fieldNode -> {
            if (fieldNode.signature != null) {
                try {
                    CheckClassAdapter.checkFieldSignature(fieldNode.signature);
                } catch (IllegalArgumentException IAE) {
                    fieldNode.signature = null;
                } catch (Throwable x) {
                    x.printStackTrace();
                }
            }
        });

    }

    public static void removeLineNumber(Map<String, ClassNode> classes){
        classes.values().forEach(DeobfusacteUtils::removeLineNumber);
    }

    public static void removeLineNumber(ClassNode classNode) {
        classNode.methods.forEach(methodNode -> {
            Iterator<AbstractInsnNode> it = methodNode.instructions.iterator();
            while (it.hasNext()) {
                if (it.next() instanceof LineNumberNode) {
                    it.remove();
                }
            }
        });
    }

    public static void removeLocalVariable(Map<String, ClassNode> classes) {
        classes.values().forEach(DeobfusacteUtils::removeLocalVariable);
    }

    public static void removeLocalVariable(ClassNode classNode){
        classNode.methods.forEach(methodNode -> {
            methodNode.localVariables = null;
        });
    }

    public static void removeSyntheticBridge(Map<String, ClassNode> classes){
        classes.values().forEach(DeobfusacteUtils::removeSyntheticBridge);
    }

    public static void removeSyntheticBridge(ClassNode classNode){
        classNode.access &= ~(Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE);

        classNode.methods.forEach(methodNode -> {
            methodNode.access &= ~(Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE);
        });

        classNode.fields.forEach(fieldNode -> {
            fieldNode.access &= ~(Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE);
        });
    }

    public static void removeIllegalVarargs(Map<String, ClassNode> classes){
        classes.values().forEach(DeobfusacteUtils::removeIllegalVarargs);
    }

    public static void removeIllegalVarargs(ClassNode classNode) {
        classNode.methods.forEach(methodNode -> {
            Type[] args = Type.getArgumentTypes(methodNode.desc);
            if (args.length > 0 && args[args.length - 1].getSort() != Type.ARRAY) {
                methodNode.access &= ~Opcodes.ACC_VARARGS;
            }
        });
    }

    private static boolean hasAnnotations(ClassNode classNode) {
        return (classNode.visibleAnnotations != null && !classNode.visibleAnnotations.isEmpty())
                || (classNode.invisibleAnnotations != null && !classNode.invisibleAnnotations.isEmpty());
    }

    private static boolean hasAnnotations(MethodNode methodNode) {
        return (methodNode.visibleAnnotations != null && !methodNode.visibleAnnotations.isEmpty())
                || (methodNode.invisibleAnnotations != null && !methodNode.invisibleAnnotations.isEmpty());
    }

    private static boolean hasAnnotations(FieldNode fieldNode) {
        return (fieldNode.visibleAnnotations != null && !fieldNode.visibleAnnotations.isEmpty())
                || (fieldNode.invisibleAnnotations != null && !fieldNode.invisibleAnnotations.isEmpty());
    }
}
