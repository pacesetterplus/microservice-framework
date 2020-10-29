package uk.gov.justice.services.core.featurecontrol.local;

import static java.nio.file.Paths.get;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.core.featurecontrol.local.LocalFeatureFileLocator.FEATURE_CONTROL_FILE_NAME;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class WildflyDeploymentDirLocalFeatureFileLocatorTest {

    @Spy
    private FileToUrlConverter fileToUrlConverter = new FileToUrlConverter();

    @Mock
    private WildflyDeploymentDirectoryLocator wildflyDeploymentDirectoryLocator;

    @InjectMocks
    private WildflyDeploymentDirLocalFeatureFileLocator wildflyDeploymentDirLocalFeatureFileLocator;

    @Test
    public void shouldGetTheUrlOfAFileInTheWildflyDeploymentDirectory() throws Exception {

        final Path aDirectory = directoryOfFileOnClasspath(FEATURE_CONTROL_FILE_NAME);
        when(wildflyDeploymentDirectoryLocator.getDeploymentDirectory()).thenReturn(aDirectory);

        final Optional<URL> localFeatureFileLocation = wildflyDeploymentDirLocalFeatureFileLocator.findLocalFeatureFileLocation(FEATURE_CONTROL_FILE_NAME);

        if (localFeatureFileLocation.isPresent()) {
            assertThat(localFeatureFileLocation.get().getFile(), endsWith("/" + FEATURE_CONTROL_FILE_NAME));
        } else {
            fail();
        }
    }

    @Test
    public void shouldReturnEmptyIfTheFileCannotBeFoundInTheWildflyDeploymentDirectory() throws Exception {

        final Path aDirectory = directoryOfFileOnClasspath("json/envelope.json");
        when(wildflyDeploymentDirectoryLocator.getDeploymentDirectory()).thenReturn(aDirectory);

        final Optional<URL> localFeatureFileLocation = wildflyDeploymentDirLocalFeatureFileLocator.findLocalFeatureFileLocation(FEATURE_CONTROL_FILE_NAME);

        assertThat(localFeatureFileLocation.isPresent(), is(false));
    }

    @Test
    public void shouldReturnEmptyIfTheWildflyDeploymentDirectoryDoesNotExist() throws Exception {

        final Path aDirectory = get("/this/directory-does-not-exist");
        when(wildflyDeploymentDirectoryLocator.getDeploymentDirectory()).thenReturn(aDirectory);

        final Optional<URL> localFeatureFileLocation = wildflyDeploymentDirLocalFeatureFileLocator.findLocalFeatureFileLocation(FEATURE_CONTROL_FILE_NAME);

        assertThat(localFeatureFileLocation.isPresent(), is(false));
    }

    private Path directoryOfFileOnClasspath(final String fileOnClasspath) throws URISyntaxException {

        final URL localFeatureFileLocation = new ClasspathLocalFeatureFileLocator()
                .findLocalFeatureFileLocation(fileOnClasspath)
                .orElseThrow(AssertionError::new);

        return get(localFeatureFileLocation.toURI()).getParent().toAbsolutePath();
    }
}