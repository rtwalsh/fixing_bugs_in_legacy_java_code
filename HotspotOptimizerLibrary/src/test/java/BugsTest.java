import com.assetco.search.results.Asset;
import com.assetco.search.results.AssetVendor;
import com.assetco.search.results.AssetVendorRelationshipLevel;
import com.assetco.search.results.HotspotKey;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BugsTest {

    @Test
    public void precedingPartnerWithLongTrailingAssetsDoesNotWin() {
        AssetVendor partnerVendor = makeVendor(AssetVendorRelationshipLevel.Partner);
        AssetVendor otherVendor = makeVendor(AssetVendorRelationshipLevel.Partner);

        Asset missing = givenAssetsInResultWithVendor(partnerVendor);
        Asset disruptingAsset = givenAssetsInResultWithVendor(otherVendor);

        List<Asset> expected = new ArrayList<Asset>();
        expected.add(givenAssetsInResultWithVendor(partnerVendor));
        expected.add(givenAssetsInResultWithVendor(partnerVendor));
        expected.add(givenAssetsInResultWithVendor(partnerVendor));
        expected.add(givenAssetsInResultWithVendor(partnerVendor));

        whenOptimize();

        thenHotspotDoesNotHave(HotspotKey.Showcase, missing);
        thenHotspotHasExactly(HotspotKey.Showcase, expected);
    }

    private void thenHotspotHasExactly(HotspotKey hotspotKey, List<Asset> expected) {
    }

    private void thenHotspotDoesNotHave(HotspotKey hotspotKey, Asset asset) {
    }

    private void whenOptimize() {
    }

    private Asset givenAssetsInResultWithVendor(AssetVendor partnerVendor) {
        return null;
    }

    private AssetVendor makeVendor(AssetVendorRelationshipLevel partner) {
        return null;
    }
}
