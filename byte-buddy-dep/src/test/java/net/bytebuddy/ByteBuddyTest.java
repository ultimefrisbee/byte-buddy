package net.bytebuddy;

import net.bytebuddy.asm.ClassVisitorWrapper;
import net.bytebuddy.dynamic.scaffold.BridgeMethodResolver;
import net.bytebuddy.dynamic.scaffold.MethodRegistry;
import net.bytebuddy.instrumentation.Instrumentation;
import net.bytebuddy.instrumentation.ModifierContributor;
import net.bytebuddy.instrumentation.attribute.FieldAttributeAppender;
import net.bytebuddy.instrumentation.attribute.MethodAttributeAppender;
import net.bytebuddy.instrumentation.attribute.TypeAttributeAppender;
import net.bytebuddy.instrumentation.method.MethodLookupEngine;
import net.bytebuddy.instrumentation.method.matcher.MethodMatcher;
import net.bytebuddy.instrumentation.type.TypeDescription;
import net.bytebuddy.utility.HashCodeEqualsTester;
import net.bytebuddy.utility.MockitoRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.instrumentation.method.matcher.MethodMatchers.isMethod;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class ByteBuddyTest {

    private static final int MASK = Opcodes.ACC_PUBLIC;
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);
    @Mock
    private TypeAttributeAppender typeAttributeAppender;
    @Mock
    private BridgeMethodResolver.Factory bridgeMethodResolverFactory;
    @Mock
    private ClassFileVersion classFileVersion;
    @Mock
    private ClassVisitorWrapper classVisitorWrapper;
    @Mock
    private FieldAttributeAppender.Factory fieldAttributeAppenderFactory;
    @Mock
    private MethodAttributeAppender.Factory methodAttributeAppenderFactory;
    @Mock
    private MethodMatcher methodMatcher;
    @Mock
    private TypeDescription typeDescription;
    @Mock
    private MethodLookupEngine.Factory methodLookupEngineFactory;
    @Mock
    private ModifierContributor.ForType modifierContributorForType;
    @Mock
    private NamingStrategy namingStrategy;
    @Mock
    private Instrumentation instrumentation;

    @Before
    public void setUp() throws Exception {
        when(modifierContributorForType.getMask()).thenReturn(MASK);
        when(typeDescription.isInterface()).thenReturn(true);
    }

    @Test
    public void testDomainSpecificLanguage() throws Exception {
        assertProperties(new ByteBuddy()
                .method(methodMatcher).intercept(instrumentation)
                .withAttribute(typeAttributeAppender)
                .withBridgeMethodResolver(bridgeMethodResolverFactory)
                .withClassFileVersion(classFileVersion)
                .withClassVisitor(classVisitorWrapper)
                .withDefaultFieldAttributeAppender(fieldAttributeAppenderFactory)
                .withDefaultMethodAttributeAppender(methodAttributeAppenderFactory)
                .withIgnoredMethods(methodMatcher)
                .withImplementing(typeDescription)
                .withMethodLookupEngine(methodLookupEngineFactory)
                .withModifiers(modifierContributorForType)
                .withNamingStrategy(namingStrategy));
    }

    @Test
    public void testDomainSpecificLanguageOnAnnotationTarget() throws Exception {
        assertProperties(new ByteBuddy()
                .withAttribute(typeAttributeAppender)
                .withBridgeMethodResolver(bridgeMethodResolverFactory)
                .withClassFileVersion(classFileVersion)
                .withClassVisitor(classVisitorWrapper)
                .withDefaultFieldAttributeAppender(fieldAttributeAppenderFactory)
                .withDefaultMethodAttributeAppender(methodAttributeAppenderFactory)
                .withIgnoredMethods(methodMatcher)
                .withImplementing(typeDescription)
                .withMethodLookupEngine(methodLookupEngineFactory)
                .withModifiers(modifierContributorForType)
                .withNamingStrategy(namingStrategy)
                .method(methodMatcher).intercept(instrumentation));
    }

    private void assertProperties(ByteBuddy byteBuddy) {
        assertThat(byteBuddy.getTypeAttributeAppender(), is(typeAttributeAppender));
        assertThat(byteBuddy.getClassFileVersion(), is(classFileVersion));
        assertThat(byteBuddy.getDefaultFieldAttributeAppenderFactory(), is(fieldAttributeAppenderFactory));
        assertThat(byteBuddy.getDefaultMethodAttributeAppenderFactory(), is(methodAttributeAppenderFactory));
        assertThat(byteBuddy.getIgnoredMethods(), is(methodMatcher));
        assertThat(byteBuddy.getBridgeMethodResolverFactory(), is(bridgeMethodResolverFactory));
        assertThat(byteBuddy.getInterfaceTypes().size(), is(1));
        assertThat(byteBuddy.getInterfaceTypes(), hasItem(typeDescription));
        assertThat(byteBuddy.getMethodLookupEngineFactory(), is(methodLookupEngineFactory));
        assertThat(byteBuddy.getModifiers().isDefined(), is(true));
        assertThat(byteBuddy.getModifiers().resolve(0), is(MASK));
        assertThat(byteBuddy.getNamingStrategy(), is(namingStrategy));
        assertThat(byteBuddy.getClassVisitorWrapperChain(), instanceOf(ClassVisitorWrapper.Chain.class));
        ClassVisitor classVisitor = mock(ClassVisitor.class);
        byteBuddy.getClassVisitorWrapperChain().wrap(classVisitor);
        verify(classVisitorWrapper).wrap(classVisitor);
        verifyNoMoreInteractions(classVisitorWrapper);
        assertThat(byteBuddy.getMethodRegistry(), is(new MethodRegistry.Default()
                .append(new MethodRegistry.LatentMethodMatcher.Simple(isMethod().and(methodMatcher)),
                        instrumentation,
                        MethodAttributeAppender.NoOp.INSTANCE)));
    }

    @Test
    public void testClassFileVersionConstructor() throws Exception {
        assertThat(new ByteBuddy(ClassFileVersion.JAVA_V6).getClassFileVersion(), is(ClassFileVersion.JAVA_V6));
    }

    @Test
    public void testHashCodeEquals() throws Exception {
        HashCodeEqualsTester.of(ByteBuddy.class).apply();
        HashCodeEqualsTester.of(ByteBuddy.MatchedMethodInterception.class).apply();
        HashCodeEqualsTester.of(ByteBuddy.MethodAnnotationTarget.class).apply();
        HashCodeEqualsTester.of(ByteBuddy.OptionalMethodInterception.class).apply();
    }
}
