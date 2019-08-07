package ru.otus.lesson8.agent;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * Created by Alexander Bryantsev on 01.08.2019.
 */
public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {

                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
                ClassLoggerVisitor cv = new ClassLoggerVisitor(Opcodes.ASM5, cw);
                cr.accept(cv, Opcodes.ASM5);

                return cw.toByteArray();
            }
        });
    }

}
