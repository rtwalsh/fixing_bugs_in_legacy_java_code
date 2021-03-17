import com.assetco.hotspots.optimization.SearchResultHotspotOptimizer;
import com.assetco.search.results.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BugsTest {

    private SearchResults searchResults;

    @BeforeEach
    public void setUp() {
        searchResults = new SearchResults();
    }

    @Test
    public void precedingPartnerWithLongTrailingAssetsDoesNotWin() {
        AssetVendor partnerVendor = makeVendor(AssetVendorRelationshipLevel.Partner);
        AssetVendor otherVendor = makeVendor(AssetVendorRelationshipLevel.Partner);

        Asset missing = givenAssetsInResultsWithVendor(partnerVendor);
        Asset disruptingAsset = givenAssetsInResultsWithVendor(otherVendor);

        List<Asset> expected = new ArrayList<Asset>();
        expected.add(givenAssetsInResultsWithVendor(partnerVendor));
        expected.add(givenAssetsInResultsWithVendor(partnerVendor));
        expected.add(givenAssetsInResultsWithVendor(partnerVendor));
        expected.add(givenAssetsInResultsWithVendor(partnerVendor));

        whenOptimize();

        thenHotspotDoesNotHave(HotspotKey.Showcase, missing);
        thenHotspotHasExactly(HotspotKey.Showcase, expected);
    }

    private void thenHotspotHasExactly(HotspotKey hotspotKey, List<Asset> expected) {
        assertEquals(expected, searchResults.getHotspot(hotspotKey).getMembers());
    }

    private void thenHotspotDoesNotHave(HotspotKey hotspotKey, Asset asset) {
        assertFalse(searchResults.getHotspot(hotspotKey).getMembers().contains(asset));
    }

    private void whenOptimize() {
        SearchResultHotspotOptimizer optimzier = new SearchResultHotspotOptimizer();
        optimzier.optimize(searchResults);
    }

    private Asset givenAssetsInResultsWithVendor(AssetVendor vendor) {
        BigDecimal zero = new BigDecimal("0.0");
        AssetPurchaseInfo last30Days = new AssetPurchaseInfo(0, 0, new Money(zero), new Money(zero));
        AssetPurchaseInfo last24Hours = new AssetPurchaseInfo(0, 0, new Money(zero), new Money(zero));
        Asset asset = new Asset(null, null, null, null, last30Days, last24Hours, null, vendor);
        searchResults.addFound(asset);
        return asset;
    }

    private AssetVendor makeVendor(AssetVendorRelationshipLevel relationshipLevel) {
        AssetVendor vendor = new AssetVendor(null, null, relationshipLevel, 0.0f);
        return vendor;
    }
}
