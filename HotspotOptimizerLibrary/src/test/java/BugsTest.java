import com.assetco.hotspots.optimization.SearchResultHotspotOptimizer;
import com.assetco.search.results.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BugsTest {

    private SearchResults searchResults;
    private SearchResultHotspotOptimizer optimzier;
    private AssetVendor partnerVendor;
    private AssetVendor basicVendor;

    @BeforeEach
    public void setUp() {
        searchResults = new SearchResults();
        optimzier = new SearchResultHotspotOptimizer();
        partnerVendor = makeVendor(AssetVendorRelationshipLevel.Partner);
        basicVendor = makeVendor(AssetVendorRelationshipLevel.Basic);
    }

    @Test
    public void prevailingPartnerReceivesFiveAssetsInShowcase() {
        AssetVendor otherVendor = makeVendor(AssetVendorRelationshipLevel.Partner);

        List<Asset> expected = new ArrayList<Asset>();
        expected.add(givenAssetInResultsWithVendor(partnerVendor));

        givenAssetInResultsWithVendor(otherVendor);

        expected.add(givenAssetInResultsWithVendor(partnerVendor));
        expected.add(givenAssetInResultsWithVendor(partnerVendor));
        expected.add(givenAssetInResultsWithVendor(partnerVendor));
        expected.add(givenAssetInResultsWithVendor(partnerVendor));

        whenOptimize();

        thenHotspotHasExactly(HotspotKey.Showcase, expected);
    }

    @Test
    public void allItemsThatShouldBeHighlightedAre() {
        AssetTopic moreHotTopic = makeTopic();
        AssetTopic lessHotTopic = makeTopic();
        setHotTopics(moreHotTopic, lessHotTopic);
        var expectedAssets = givenAssetsWithTopics(basicVendor, 2, lessHotTopic);
        givenAssetsWithTopics(basicVendor, 3, moreHotTopic);
        expectedAssets.add(givenAssetInResultsWithVendorAndTopics(basicVendor, lessHotTopic));

        whenOptimize();

        thenHotsoptHas(HotspotKey.Highlight, expectedAssets);
    }

    private AssetVendor makeVendor(AssetVendorRelationshipLevel relationshipLevel) {
        AssetVendor vendor = new AssetVendor(UUID.randomUUID().toString(), null, relationshipLevel, 0.0f);
        return vendor;
    }

    private Asset makeAsset(AssetVendor vendor) {
        return makeAssetWithTopics(vendor, null);
    }

    private Asset makeAssetWithTopics(AssetVendor vendor, AssetTopic... topics) {
        BigDecimal zero = new BigDecimal("0.0");
        AssetPurchaseInfo last30Days = new AssetPurchaseInfo(0, 0, new Money(zero), new Money(zero));
        AssetPurchaseInfo last24Hours = new AssetPurchaseInfo(0, 0, new Money(zero), new Money(zero));

        List<AssetTopic> topicsList = null;
        if (topics != null) {
            topicsList = Arrays.asList(topics);
        }
        return new Asset(null, null, null, null, last30Days, last24Hours, topicsList, vendor);
    }

    private AssetTopic makeTopic() {
        AssetTopic topic = new AssetTopic(UUID.randomUUID().toString(), "A topic");
        return topic;
    }

    private void setHotTopics(AssetTopic... topics) {
        optimzier.setHotTopics(() -> Arrays.asList(topics));
    }

    private List<Asset> givenAssetsWithTopics(AssetVendor vendor, int count, AssetTopic... topics) {
        List<Asset> assets = new ArrayList<Asset>();
        for (int x = 0; x < count; ++x) {
            assets.add(givenAssetInResultsWithVendorAndTopics(vendor, topics));
        }
        return assets;
    }

    private Asset givenAssetInResultsWithVendorAndTopics(AssetVendor vendor, AssetTopic... topics) {
        Asset asset = makeAssetWithTopics(vendor, topics);
        searchResults.addFound(asset);
        return asset;
    }

    private Asset givenAssetInResultsWithVendor(AssetVendor vendor) {
        Asset asset = makeAsset(vendor);
        searchResults.addFound(asset);
        return asset;
    }

    private void whenOptimize() {
        optimzier.optimize(searchResults);
    }

    private void thenHotspotHasExactly(HotspotKey hotspotKey, List<Asset> expected) {
        assertEquals(expected, searchResults.getHotspot(hotspotKey).getMembers());
    }

    private void thenHotsoptHas(HotspotKey hotspotKey, List<Asset> expectedAssets) {
        List<Asset> actualAssets = searchResults.getHotspot(hotspotKey).getMembers();
        for (Asset asset : expectedAssets) {
            assertTrue(actualAssets.contains(asset));
        }
    }
}
