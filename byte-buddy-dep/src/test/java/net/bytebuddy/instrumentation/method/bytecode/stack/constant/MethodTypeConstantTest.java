package net.bytebuddy.instrumentation.method.bytecode.stack.constant;

import net.bytebuddy.instrumentation.Instrumentation;
import net.bytebuddy.instrumentation.method.MethodDescription;
import net.bytebuddy.instrumentation.method.bytecode.stack.StackManipulation;
import net.bytebuddy.utility.MockitoRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.objectweb.asm.MethodVisitor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MethodTypeConstantTest {

    private static final String FOO = "foo", BAR = "bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription methodDescription;
    @Mock
    private MethodVisitor methodVisitor;
    @Mock
    private Instrumentation.Context instrumentationContext;

    @Before
    public void setUp() throws Exception {
        when(methodDescription.getDescriptor()).thenReturn(FOO);
    }

    @Test
    public void testApplication() throws Exception {
        StackManipulation stackManipulation = new MethodTypeConstant(methodDescription);
        assertThat(stackManipulation.isValid(), is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, instrumentationContext);
        assertThat(size.getSizeImpact(), is(1));
        assertThat(size.getMaximalSize(), is(1));
    }

    @Test
    public void testHashCodeEquals() throws Exception {
        MethodDescription other = mock(MethodDescription.class);
        when(other.getDescriptor()).thenReturn(BAR);
        assertThat(new MethodTypeConstant(methodDescription).hashCode(), is(new MethodTypeConstant(methodDescription).hashCode()));
        assertThat(new MethodTypeConstant(methodDescription), is(new MethodTypeConstant(methodDescription)));
        assertThat(new MethodTypeConstant(methodDescription).hashCode(), not(is(new MethodTypeConstant(other).hashCode())));
        assertThat(new MethodTypeConstant(methodDescription), not(is(new MethodTypeConstant(other))));
    }
}
