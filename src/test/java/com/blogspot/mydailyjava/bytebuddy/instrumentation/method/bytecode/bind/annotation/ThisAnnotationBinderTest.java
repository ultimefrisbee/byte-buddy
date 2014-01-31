package com.blogspot.mydailyjava.bytebuddy.instrumentation.method.bytecode.bind.annotation;

import com.blogspot.mydailyjava.bytebuddy.instrumentation.method.bytecode.assign.IllegalAssignment;
import com.blogspot.mydailyjava.bytebuddy.instrumentation.type.TypeDescription;
import com.blogspot.mydailyjava.bytebuddy.instrumentation.type.TypeList;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Annotation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;

public class ThisAnnotationBinderTest extends AbstractAnnotationBinderTest<This> {

    public ThisAnnotationBinderTest() {
        super(This.class);
    }

    @Test
    public void testAnnotationType() throws Exception {
        assertEquals(This.class, This.Binder.INSTANCE.getHandledType());
    }

    private TypeList typeList;
    private TypeDescription parameterType;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(assignment.isValid()).thenReturn(true);
        typeList = mock(TypeList.class);
        parameterType = mock(TypeDescription.class);
        when(typeList.get(0)).thenReturn(parameterType);
    }

    @Test
    public void testLegalBinding() throws Exception {
        when(assignment.isValid()).thenReturn(true);
        when(target.getParameterTypes()).thenReturn(typeList);
        when(target.getParameterAnnotations()).thenReturn(new Annotation[1][0]);
        AnnotationDrivenBinder.ArgumentBinder.IdentifiedBinding<?> identifiedBinding = This.Binder.INSTANCE
                .bind(annotation, 0, source, target, typeDescription, assigner);
        assertThat(identifiedBinding.isValid(), is(true));
        verify(assigner).assign(typeDescription, parameterType, false);
        verifyNoMoreInteractions(assigner);
        verify(target, atLeast(1)).getParameterTypes();
        verify(target, atLeast(1)).getParameterAnnotations();
    }

    @Test
    public void testLegalBindingRuntimeType() throws Exception {
        when(assignment.isValid()).thenReturn(true);
        when(target.getParameterTypes()).thenReturn(typeList);
        RuntimeType runtimeType = mock(RuntimeType.class);
        doReturn(RuntimeType.class).when(runtimeType).annotationType();
        when(target.getParameterAnnotations()).thenReturn(new Annotation[][]{{runtimeType}});
        AnnotationDrivenBinder.ArgumentBinder.IdentifiedBinding<?> identifiedBinding = This.Binder.INSTANCE
                .bind(annotation, 0, source, target, typeDescription, assigner);
        assertThat(identifiedBinding.isValid(), is(true));
        verify(assigner).assign(typeDescription, parameterType, true);
        verifyNoMoreInteractions(assigner);
        verify(target, atLeast(1)).getParameterTypes();
        verify(target, atLeast(1)).getParameterAnnotations();
    }

    @Test
    public void testIllegalBinding() throws Exception {
        when(assignment.isValid()).thenReturn(false);
        when(target.getParameterTypes()).thenReturn(typeList);
        when(target.getParameterAnnotations()).thenReturn(new Annotation[1][0]);
        when(assigner.assign(any(TypeDescription.class), any(TypeDescription.class), anyBoolean())).thenReturn(IllegalAssignment.INSTANCE);
        AnnotationDrivenBinder.ArgumentBinder.IdentifiedBinding<?> identifiedBinding = This.Binder.INSTANCE
                .bind(annotation, 0, source, target, typeDescription, assigner);
        assertThat(identifiedBinding.isValid(), is(false));
        verify(assigner).assign(typeDescription, parameterType, false);
        verifyNoMoreInteractions(assigner);
        verify(target, atLeast(1)).getParameterTypes();
        verify(target, atLeast(1)).getParameterAnnotations();
    }

    @Test(expected = IllegalStateException.class)
    public void testPrimitiveType() throws Exception {
        when(parameterType.isPrimitive()).thenReturn(true);
        when(target.getParameterTypes()).thenReturn(typeList);
        This.Binder.INSTANCE.bind(annotation, 0, source, target, typeDescription, assigner);
    }

    @Test(expected = IllegalStateException.class)
    public void testArrayType() throws Exception {
        when(parameterType.isArray()).thenReturn(true);
        when(target.getParameterTypes()).thenReturn(typeList);
        This.Binder.INSTANCE.bind(annotation, 0, source, target, typeDescription, assigner);
    }
}