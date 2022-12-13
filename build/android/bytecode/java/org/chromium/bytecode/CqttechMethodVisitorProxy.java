package org.chromium.bytecode;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import static org.chromium.bytecode.TypeUtils.BUILD_HOOKS_ANDROID;
import static org.chromium.bytecode.TypeUtils.INT;
import static org.chromium.bytecode.TypeUtils.RESOURCES;
import static org.chromium.bytecode.TypeUtils.STRING;

/**
 * Remaps Resources.getIdentifier() method calls to use BuildHooksAndroid.
 *
 * resourceObj.getIdentifier(String, String, String) becomes:
 * BuildHooksAndroid.getIdentifier(resourceObj, String, String, String);
 */
public class CqttechMethodVisitorProxy extends MethodVisitor {
    private static final String GET_IDENTIFIER_DESCRIPTOR =
            TypeUtils.getMethodDescriptor(INT, STRING, STRING, STRING);

    private final JSRInlinerAdapter mJsrAdapter;

    public CqttechMethodVisitorProxy(
            MethodVisitor mv,
            final int access, final String name, String desc,
            String signature, String[] exceptions) {
        super(Opcodes.ASM8);
        mJsrAdapter = new JSRInlinerAdapter(mv, access, name, desc, signature, exceptions);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        String methodName = "getIdentifier";
        if (opcode == INVOKEVIRTUAL && owner.equals(RESOURCES) && name.equals(methodName)
                && descriptor.equals(GET_IDENTIFIER_DESCRIPTOR)) {
            mJsrAdapter.visitMethodInsn(INVOKESTATIC, BUILD_HOOKS_ANDROID, methodName,
                    TypeUtils.getMethodDescriptor(INT, RESOURCES, STRING, STRING, STRING), isInterface);
        } else {
            mJsrAdapter.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }

    @Override
    public void visitParameter(String name, int access) {
        mJsrAdapter.visitParameter(name, access);
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return mJsrAdapter.visitAnnotationDefault();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return mJsrAdapter.visitAnnotation(descriptor, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return mJsrAdapter.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
        mJsrAdapter.visitAnnotableParameterCount(parameterCount, visible);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        return mJsrAdapter.visitParameterAnnotation(parameter, descriptor, visible);
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        mJsrAdapter.visitAttribute(attribute);
    }

    @Override
    public void visitCode() {
        mJsrAdapter.visitCode();
    }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        mJsrAdapter.visitFrame(type, numLocal, local, numStack, stack);
    }

    @Override
    public void visitInsn(int opcode) {
        mJsrAdapter.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        mJsrAdapter.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        mJsrAdapter.visitVarInsn(opcode, varIndex);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        mJsrAdapter.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        mJsrAdapter.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor) {
        mJsrAdapter.visitMethodInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        mJsrAdapter.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        mJsrAdapter.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(Label label) {
        mJsrAdapter.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(Object value) {
        mJsrAdapter.visitLdcInsn(value);
    }

    @Override
    public void visitIincInsn(int varIndex, int increment) {
        mJsrAdapter.visitIincInsn(varIndex, increment);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        mJsrAdapter.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        mJsrAdapter.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        mJsrAdapter.visitMultiANewArrayInsn(descriptor, numDimensions);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return mJsrAdapter.visitInsnAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        mJsrAdapter.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return mJsrAdapter.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        mJsrAdapter.visitLocalVariable(name, descriptor, signature, start, end, index);
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
        return mJsrAdapter.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        mJsrAdapter.visitLineNumber(line, start);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        mJsrAdapter.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        mJsrAdapter.visitEnd();
    }
}
