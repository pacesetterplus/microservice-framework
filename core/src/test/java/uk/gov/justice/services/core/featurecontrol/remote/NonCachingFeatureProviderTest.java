package uk.gov.justice.services.core.featurecontrol.remote;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.core.featurecontrol.FeatureFetcher;
import uk.gov.justice.services.core.featurecontrol.domain.Feature;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NonCachingFeatureProviderTest {

    @Mock
    private FeatureFetcher featureFetcher;

    @InjectMocks
    private NonCachingFeatureProvider nonCachingFeatureProvider;

    @Test
    public void shouldGetTheListOfFeaturesAndReturnTheNamedOne() throws Exception {

        final String featureName_1 = "feature 1";
        final String featureName_2 = "feature 2";
        final String featureName_3 = "feature 3";

        final Feature feature_1 = mock(Feature.class);
        final Feature feature_2 = mock(Feature.class);
        final Feature feature_3 = mock(Feature.class);

        when(featureFetcher.fetchFeatures()).thenReturn(asList(feature_1, feature_2, feature_3));
        when(feature_1.getFeatureName()).thenReturn(featureName_1);
        when(feature_2.getFeatureName()).thenReturn(featureName_2);
        when(feature_3.getFeatureName()).thenReturn(featureName_3);

        assertThat(nonCachingFeatureProvider.lookup(featureName_2), is(of(feature_2)));
    }

    @Test
    public void shouldReturnEmptyIfNoFeatureFound() throws Exception {

        final String featureName_1 = "feature 1";
        final String featureName_2 = "feature 2";
        final String featureName_3 = "feature 3";

        final Feature feature_1 = mock(Feature.class);
        final Feature feature_2 = mock(Feature.class);
        final Feature feature_3 = mock(Feature.class);

        when(featureFetcher.fetchFeatures()).thenReturn(asList(feature_1, feature_2, feature_3));
        when(feature_1.getFeatureName()).thenReturn(featureName_1);
        when(feature_2.getFeatureName()).thenReturn(featureName_2);
        when(feature_3.getFeatureName()).thenReturn(featureName_3);

        assertThat(nonCachingFeatureProvider.lookup("some other feature name"), is(empty()));
    }
}