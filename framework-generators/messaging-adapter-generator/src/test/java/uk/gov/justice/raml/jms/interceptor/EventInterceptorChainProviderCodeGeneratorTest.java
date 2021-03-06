package uk.gov.justice.raml.jms.interceptor;

import static com.squareup.javapoet.JavaFile.builder;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.justice.raml.jms.core.ClassNameFactory.EVENT_FILTER_INTERCEPTOR;
import static uk.gov.justice.raml.jms.core.ClassNameFactory.EVENT_INTERCEPTOR_CHAIN_PROVIDER;
import static uk.gov.justice.services.test.utils.core.compiler.JavaCompilerUtility.javaCompilerUtil;

import uk.gov.justice.raml.jms.core.ClassNameFactory;
import uk.gov.justice.services.components.event.listener.interceptors.EventBufferInterceptor;
import uk.gov.justice.services.core.interceptor.InterceptorChainEntry;
import uk.gov.justice.services.core.interceptor.InterceptorChainEntryProvider;

import java.io.File;
import java.util.List;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventInterceptorChainProviderCodeGeneratorTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @InjectMocks
    private EventInterceptorChainProviderCodeGenerator eventInterceptorChainProviderCodeGenerator;

    @Test
    public void shouldGenerateAWorkingEventListenerInterceptorChainProviderWithTheCorrectInterceptorChainEntiresAndComponentName() throws Exception {

        final String componentName = "MY_CUSTOM_EVENT_LISTENER";

        final String packageName = "uk.gov.justice.api.interceptor.filter";
        final String simpleName = "MyCustomEventListenerInterceptorChainProvider";

        final ClassName eventListenerInterceptorChainProviderClassName = ClassName.get(packageName, simpleName);
        final ClassName eventFilterInterceptorClassName = ClassName.get(StubEventFilterInterceptor.class);
        final ClassNameFactory classNameFactory = mock(ClassNameFactory.class);

        when(classNameFactory.classNameFor(EVENT_INTERCEPTOR_CHAIN_PROVIDER)).thenReturn(eventListenerInterceptorChainProviderClassName);
        when(classNameFactory.classNameFor(EVENT_FILTER_INTERCEPTOR)).thenReturn(eventFilterInterceptorClassName);

        final TypeSpec typeSpec = eventInterceptorChainProviderCodeGenerator.generate(
                componentName,
                classNameFactory);

        final File codeGenerationOutputDirectory = temporaryFolder.newFolder("test-generation");
        final File compilationOutputDirectory = temporaryFolder.newFolder("interceptorChainProvider-generation");

        builder(packageName, typeSpec)
                .build()
                .writeTo(codeGenerationOutputDirectory);

        final Class<?> compiledClass = javaCompilerUtil().compiledClassOf(
                codeGenerationOutputDirectory,
                compilationOutputDirectory,
                packageName,
                simpleName);

        final InterceptorChainEntryProvider interceptorChainEntryProvider = (InterceptorChainEntryProvider) compiledClass.newInstance();

        assertThat(interceptorChainEntryProvider.component(), is(componentName));

        final List<InterceptorChainEntry> interceptorChainEntries = interceptorChainEntryProvider.interceptorChainTypes();
        assertThat(interceptorChainEntries, hasItem(new InterceptorChainEntry(1000, EventBufferInterceptor.class)));
        assertThat(interceptorChainEntries, hasItem(new InterceptorChainEntry(2000, StubEventFilterInterceptor.class)));
    }
}
