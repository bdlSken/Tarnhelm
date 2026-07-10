package cn.ac.lz233.tarnhelm

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Guards the "Global Tracking Parameters" regex rule shipped in
 * app/src/main/assets/rules/default_rules.json.
 *
 * The b[]/c[] pairs below MUST stay identical to that rule. This test replicates
 * how [cn.ac.lz233.tarnhelm.util.ktx.doTarnhelm] applies a regex rule: the first
 * pattern gates the rule (must match), then every pattern/replacement runs in order.
 */
class GlobalTrackingRuleTest {

    // Keep in sync with the "Global Tracking Parameters" rule in default_rules.json.
    private val patterns = listOf(
        "([?&])(?:utm_[^=&#]+|fbclid|gclid|gclsrc|dclid|gbraid|wbraid|msclkid|mc_eid|mc_cid|mkt_tok|igshid|igsh|yclid|twclid|ttclid|spm|scm|_ga|vero_id|_hsenc|_hsmi|__hssc|__hstc|__hsfp|oly_anon_id|oly_enc_id)=[^&#]*",
        "\\?&+",
        "&&+",
        "[?&]+#",
        "[?&]+$",
    )
    private val replacements = listOf("$1", "?", "&", "#", "")

    private fun clean(url: String): String {
        // Gate: rule only runs if the first pattern matches.
        if (!Regex(patterns[0]).containsMatchIn(url)) return url
        var result = url
        patterns.indices.forEach { i ->
            result = Regex(patterns[i]).replace(result, replacements[i])
        }
        return result
    }

    @Test
    fun ruleIsWellFormed() {
        assertEquals(patterns.size, replacements.size)
    }

    @Test
    fun removesSingleTracker() {
        assertEquals("https://shop.com/p", clean("https://shop.com/p?utm_source=newsletter"))
    }

    @Test
    fun removesLeadingTrackerKeepsRealParam() {
        assertEquals("https://shop.com/p?id=5", clean("https://shop.com/p?utm_source=a&id=5"))
    }

    @Test
    fun removesTrailingTrackerKeepsRealParam() {
        assertEquals("https://shop.com/p?id=5", clean("https://shop.com/p?id=5&fbclid=xyz"))
    }

    @Test
    fun removesMultipleTrackers() {
        assertEquals("https://shop.com/p?id=5", clean("https://shop.com/p?utm_source=a&id=5&gclid=b&fbclid=c"))
    }

    @Test
    fun preservesFragment() {
        assertEquals("https://shop.com/p#section", clean("https://shop.com/p?fbclid=xyz#section"))
    }

    @Test
    fun leavesCleanUrlUntouched() {
        assertEquals("https://shop.com/p?id=5", clean("https://shop.com/p?id=5"))
    }
}
