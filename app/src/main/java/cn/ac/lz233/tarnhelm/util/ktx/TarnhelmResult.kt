package cn.ac.lz233.tarnhelm.util.ktx

data class TarnhelmUrlResult(
    val cleaned: CharSequence,
    val hasTimeConsumingOperation: Boolean,
    val appliedRules: List<String>,
    val removedParameters: List<String>,
)
