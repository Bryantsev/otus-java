package ru.otus.lesson8.agent;

import org.objectweb.asm.*;

public class ClassLoggerVisitor extends ClassVisitor {

    private ClassLogSettings classLogSettings = new ClassLogSettings();

    public ClassLoggerVisitor() {
        super(Opcodes.ASM5);
    }

    public ClassLoggerVisitor(int api) {
        super(api);
    }

    public ClassLoggerVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if ("Lru/otus/lesson8/annotations/LogMethods;".equals(desc)) {
            classLogSettings.setDoLogging(true); // Делаем логгирование посещаемого класса
            return new ClassLogMethodsAnnotationVisitor(Opcodes.ASM5, classLogSettings);
        }
        return cv.visitAnnotation(desc, visible);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        // Не логгируем конструкторы и методы без аргументов
        // TODO Добавить логирование самих фактов вызова методов без аргументов
        if (classLogSettings.isDoLogging() && !"<init>".equals(name) && Type.getArgumentTypes(descriptor).length > 0) {
            return new MethodLoggerVisitor(Opcodes.ASM5, mv, classLogSettings,
                new MethodDescription(access, name, descriptor, signature, exceptions));
        }

        return mv;
    }

}
