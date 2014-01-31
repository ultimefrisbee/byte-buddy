package com.blogspot.mydailyjava.bytebuddy.instrumentation.method;

import com.blogspot.mydailyjava.bytebuddy.instrumentation.type.TypeDescription;
import org.junit.Before;
import org.junit.Test;
import org.mockito.asm.Type;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

public class MethodDescriptionForMethodTest {

    private static final String HASH_CODE = "hashCode";
    private static final String INT_VALUE = "intValue";
    private static final String DOUBLE_VALUE = "intValue";
    private static final String CONSTRUCTOR_INTERNAL_NAME = "<init>";

    private MethodDescription objectHashCode;
    private MethodDescription integerIntValue;
    private MethodDescription doubleDoubleValue;

    @Before
    public void setUp() throws Exception {
        objectHashCode = new MethodDescription.ForMethod(Object.class.getDeclaredMethod(HASH_CODE));
        integerIntValue = new MethodDescription.ForMethod(Integer.class.getDeclaredMethod(INT_VALUE));
        doubleDoubleValue = new MethodDescription.ForMethod(Double.class.getDeclaredMethod(DOUBLE_VALUE));
    }

    @Test
    public void testRepresents() throws Exception {
        assertThat(objectHashCode.represents(Object.class.getDeclaredMethod(HASH_CODE)), is(true));
        assertThat(objectHashCode.represents(Integer.class.getDeclaredMethod(INT_VALUE)), is(false));
        assertThat(objectHashCode.represents(Double.class.getDeclaredMethod(DOUBLE_VALUE)), is(false));
        assertThat(objectHashCode.represents(Object.class.getDeclaredConstructor()), is(false));
        assertThat(integerIntValue.represents(Object.class.getDeclaredMethod(HASH_CODE)), is(false));
        assertThat(integerIntValue.represents(Integer.class.getDeclaredMethod(INT_VALUE)), is(true));
        assertThat(integerIntValue.represents(Double.class.getDeclaredMethod(DOUBLE_VALUE)), is(false));
        assertThat(integerIntValue.represents(Object.class.getDeclaredConstructor()), is(false));
        assertThat(doubleDoubleValue.represents(Object.class.getDeclaredMethod(HASH_CODE)), is(false));
        assertThat(doubleDoubleValue.represents(Integer.class.getDeclaredMethod(INT_VALUE)), is(false));
        assertThat(doubleDoubleValue.represents(Double.class.getDeclaredMethod(DOUBLE_VALUE)), is(true));
        assertThat(doubleDoubleValue.represents(Object.class.getDeclaredConstructor()), is(false));
    }

    @Test
    public void testGetInternalName() throws Exception {
        assertThat(objectHashCode.getInternalName(), is(HASH_CODE));
        assertThat(integerIntValue.getInternalName(), is(INT_VALUE));
        assertThat(doubleDoubleValue.getInternalName(), is(DOUBLE_VALUE));
    }

    @Test
    public void testGetDescriptor() throws Exception {
        assertThat(objectHashCode.getDescriptor(), is(Type.getMethodDescriptor(Object.class.getDeclaredMethod(HASH_CODE))));
        assertThat(integerIntValue.getDescriptor(), is(Type.getMethodDescriptor(Integer.class.getDeclaredMethod(INT_VALUE))));
        assertThat(doubleDoubleValue.getDescriptor(), is(Type.getMethodDescriptor(Double.class.getDeclaredMethod(DOUBLE_VALUE))));
    }

    @Test
    public void testIsOverridable() throws Exception {
        assertThat(objectHashCode.isOverridable(), is(true));
        assertThat(integerIntValue.isOverridable(), is(false));
        assertThat(doubleDoubleValue.isOverridable(), is(false));
    }

    @Test
    public void testHashCode() throws Exception {
        assertThat(objectHashCode.hashCode(), is(hashCode(Object.class.getMethod(HASH_CODE))));
        assertThat(integerIntValue.hashCode(), is(hashCode(Integer.class.getMethod(INT_VALUE))));
        assertThat(doubleDoubleValue.hashCode(), is(hashCode(Double.class.getMethod(DOUBLE_VALUE))));
    }

    private static int hashCode(Method method) {
        return (Type.getInternalName(method.getDeclaringClass()) + method.getName() + Type.getMethodDescriptor(method)).hashCode();
    }

    @Test
    public void testEquals() throws Exception {
        assertMethodEquality(objectHashCode, Object.class.getMethod(HASH_CODE));
        assertMethodEquality(integerIntValue, Integer.class.getMethod(INT_VALUE));
        assertMethodEquality(doubleDoubleValue, Double.class.getMethod(DOUBLE_VALUE));
    }

    private static void assertMethodEquality(MethodDescription methodDescription, Method method) {
        MethodDescription otherMethod = mock(MethodDescription.class);
        TypeDescription otherType = mock(TypeDescription.class);
        when(otherMethod.getUniqueSignature()).thenReturn(method.getName() + Type.getMethodDescriptor(method));
        when(otherMethod.getDeclaringType()).thenReturn(otherType);
        when(otherType.getName()).thenReturn(method.getDeclaringClass().getName());
        assertThat(methodDescription.equals(otherMethod), is(true));
        verify(otherType).getName();
        verifyNoMoreInteractions(otherType);
    }
}
