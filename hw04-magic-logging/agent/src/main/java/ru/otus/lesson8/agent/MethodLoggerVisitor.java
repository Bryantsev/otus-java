package ru.otus.lesson8.agent;

import org.objectweb.asm.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.stream.IntStream;

import static org.objectweb.asm.Opcodes.H_INVOKESTATIC;

public class MethodLoggerVisitor extends MethodVisitor {

    private ClassLogSettings classLogSettings;
    private MethodDescription methodDescription;
    private boolean doMethodLogging = false;

    public MethodLoggerVisitor(int api, MethodVisitor methodVisitor, ClassLogSettings classLogSettings, MethodDescription methodDescription) {
        super(api, methodVisitor);
        this.classLogSettings = classLogSettings;
        this.methodDescription = methodDescription;
    }

    public MethodLoggerVisitor(int api) {
        super(api);
    }

    public MethodLoggerVisitor(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }


    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        // Если метод помечен аннотацией Log, то запоминаем его для логирования
        if ("Lru/otus/lesson8/annotations/Log;".equals(descriptor)) {
            doMethodLogging = true;
        }
        return mv.visitAnnotation(descriptor, visible);
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        // Если метод необходимо логировать, то добавляем соответствующий код в его начало
        if (doMethodLogging || classLogSettings.isDoLoggingAllMethods()) {

            Type[] argumentTypes = Type.getArgumentTypes(methodDescription.getDescriptor());
            // Методы без аргументов не логируем
            // TODO Добавить логирование самих фактов вызова методов без аргументов
            if (argumentTypes.length > 0) {
                Handle handle = new Handle(
                    H_INVOKESTATIC, Type.getInternalName(java.lang.invoke.StringConcatFactory.class), "makeConcatWithConstants",
                    MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class,
                        Object[].class).toMethodDescriptorString(), false);

                // Готовим данные и выводим в System.out
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                addMethodArgs(argumentTypes, methodDescription.getAccess(), mv); // Добавляем аргументы исходного вызова

                mv.visitInvokeDynamicInsn("makeConcatWithConstants", getDescriptorByArgumentTypes(argumentTypes), handle,
                    "executed method: " + methodDescription.getName() + ", params: " +
                        (argumentTypes.length > 0 ? "\u0001" : "<none>") + "; \u0001".repeat(argumentTypes.length - 1));

                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V",
                    false);
            }

        }
    }

    /**
     * Добавляем в стек аргументы исходного вызова в рамках метода
     */
    private static void addMethodArgs(Type[] argumentTypes, int access, MethodVisitor mv) {
        boolean dOrLType = false; // Тип аргумента D или L
        // У статического метода нет ссылки на this в начале, поэтому сдвигаем стартовую позицию на -1
        int var = ((access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC ? -1 : 0);
        for (int i = 0; i < argumentTypes.length; i++) {
            var += (dOrLType ? 2 : 1); // Учитываем размер предыдущего аргумента в байтах при расчете индекса стартового байта следующего
            mv.visitVarInsn(argumentTypes[i].getOpcode(Opcodes.ILOAD), var);
            final String descriptor = argumentTypes[i].getDescriptor();
            dOrLType = descriptor.equals("L") || descriptor.equals("D"); // Сохраняем тип текущего аргумента
        }
    }

    /**
     * Возвращаем дескриптор аргументов метода и возвращаемым типом String
     */
    private static String getDescriptorByArgumentTypes(Type[] argumentTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        IntStream.range(0, argumentTypes.length).forEach(i -> sb.append(argumentTypes[i].getDescriptor()));
        sb.append(")Ljava/lang/String;");
        return sb.toString();
    }

}
