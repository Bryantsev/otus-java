package ru.otus.lesson8.agent;

import org.objectweb.asm.AnnotationVisitor;

public class ClassLogMethodsAnnotationVisitor extends AnnotationVisitor {

    private ClassLogSettings classLogSettings;

    public ClassLogMethodsAnnotationVisitor(int api, ClassLogSettings classLogSettings) {
        super(api);
        this.classLogSettings = classLogSettings;
    }

    public ClassLogMethodsAnnotationVisitor(int api) {
        super(api);
    }

    public ClassLogMethodsAnnotationVisitor(int api, AnnotationVisitor annotationVisitor) {
        super(api, annotationVisitor);
    }

    @Override
    public void visitEnum(String name, String descriptor, String value) {
        super.visitEnum(name, descriptor, value);

        if ("value".equals(name) && "ALL".equals(value)) {
            // Делаем логгирование ВСЕХ методов посещаемого класса, иначе только помеченных аннотацией Log
            classLogSettings.setDoLoggingAllMethods(true);
        }
    }
}
