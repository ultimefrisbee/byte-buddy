package net.bytebuddy.instrumentation.method.bytecode.stack.constant;

import net.bytebuddy.instrumentation.Instrumentation;
import net.bytebuddy.instrumentation.method.bytecode.stack.StackManipulation;
import net.bytebuddy.utility.MockitoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class IntegerConstantTest {

    private final int value;
    private final PushType pushType;
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);
    @Mock
    private MethodVisitor methodVisitor;
    @Mock
    private Instrumentation.Context instrumentationContext;

    public IntegerConstantTest(int value, PushType pushType) {
        this.value = value;
        this.pushType = pushType;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Integer.MIN_VALUE, PushType.LDC},
                {Short.MIN_VALUE - 1, PushType.LDC},
                {Short.MIN_VALUE, PushType.SIPUSH},
                {Byte.MIN_VALUE - 1, PushType.SIPUSH},
                {Byte.MIN_VALUE, PushType.BIPUSH},
                {-100, PushType.BIPUSH},
                {-2, PushType.BIPUSH},
                {6, PushType.BIPUSH},
                {7, PushType.BIPUSH},
                {100, PushType.BIPUSH},
                {Byte.MAX_VALUE, PushType.BIPUSH},
                {Byte.MAX_VALUE + 1, PushType.SIPUSH},
                {Short.MAX_VALUE, PushType.SIPUSH},
                {Short.MAX_VALUE + 1, PushType.LDC},
                {Integer.MAX_VALUE, PushType.LDC},
        });
    }

    @Test
    public void testBiPush() throws Exception {
        StackManipulation.Size size = IntegerConstant.forValue(value).apply(methodVisitor, instrumentationContext);
        assertThat(size.getSizeImpact(), is(1));
        assertThat(size.getMaximalSize(), is(1));
        pushType.verifyInstruction(methodVisitor, value);
        verifyNoMoreInteractions(methodVisitor);
        verifyZeroInteractions(instrumentationContext);
    }

    @Test
    public void testHashCodeEquals() throws Exception {
        assertThat(IntegerConstant.forValue(value).hashCode(), is(IntegerConstant.forValue(value).hashCode()));
        assertThat(IntegerConstant.forValue(value), is(IntegerConstant.forValue(value)));
        assertThat(IntegerConstant.forValue(value).hashCode(), not(is(IntegerConstant.forValue(value * 2).hashCode())));
        assertThat(IntegerConstant.forValue(value), not(is(IntegerConstant.forValue(value * 2))));
    }

    private static enum PushType {

        BIPUSH,
        SIPUSH,
        LDC;

        private void verifyInstruction(MethodVisitor methodVisitor, int value) {
            switch (this) {
                case BIPUSH:
                    verify(methodVisitor).visitIntInsn(Opcodes.BIPUSH, value);
                    break;
                case SIPUSH:
                    verify(methodVisitor).visitIntInsn(Opcodes.SIPUSH, value);
                    break;
                case LDC:
                    verify(methodVisitor).visitLdcInsn(value);
                    break;
                default:
                    throw new AssertionError();
            }
        }
    }
}
