package sample.reactcompiler

import kotlin.time.Duration.Companion.seconds

fun main() {
    val memoizedListRef: (Array<out String>) -> List<String> = ::useMemoizedList
    val samples = arrayOf(
        ::useFilteredLabels,
        CounterCard,
        InlineFragmentCounter,
        ZeroArgCounter,
        ::useSearchResults,
        memoizedListRef,
        ::useUserRequestUrl,
        UserId(1),
        1.seconds,
        ::usePolledItems,
        ConfirmBanner,
        StatusBadge,
        AnnotatedStatusBadge,
        ExpandableSection,
        DestructuredStateDemo,
        EffectAndGettersDemo,
        CompilerFriendlyCounter,
    )
    console.log("Kotlin/JS React Compiler compatibility samples:", samples.size)
}
